/*
  Copyright 2012  Damian Murphy <murff@warlock.org>

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.warlock.tk.handlers;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.http.HttpException;
import org.warlock.http.HttpRequest;
import org.warlock.http.HttpResponse;
import org.warlock.tk.boot.ToolkitService;
import org.safehaus.uuid.UUIDGenerator;
import org.warlock.tk.internalservices.process.ProcessData;
import org.warlock.util.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import org.warlock.tk.internalservices.send.SenderRequest;

/**
 * Handler implementation to receive an asynchronous ITK SOAP message, check it,
 * to call either the rules engine or a process as directed by the simulator
 * configuration file, and to return a response asynchronously.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
class AsynchronousWorker
    extends SynchronousWorker
{
    protected AsynchronousSoapRequestHandler asyncHandler = null;

    private static final String ORIGINALMESSAGEID = "__ORIGINAL_MESSAGEID__";
    private static final String TIMESTAMP = "__TIMESTAMP__";
    private static final String EXPIRES = "__EXPIRES__";
    private static final String TO = "__TO__";
    private static final String RESPONSETO = "__RESPONSETO__";

    private static SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    AsynchronousWorker(AsynchronousSoapRequestHandler asrh) {
        super(asrh);
        asyncHandler = asrh;
    }

    @Override
    public void handle(String path, String params, HttpRequest req, HttpResponse resp)
        throws HttpException
    {
        ServiceResponse r = null;
        try {
            if (!doChecks(path, params, req, resp)) {
                return;
            }
            // ReplyTo is mandatory for asynchronous responses
            //
            if ((replyTo == null) || (replyTo.trim().length() == 0)) {
                String m = this.makeSoapFault("Invalid message", "wsa:ReplyTo missing or empty", "1000");
                synchronousResponse(500, "Internal server error", m, req, resp, null);
                return;
            }
            if (replyTo.contentEquals("http://www.w3.org/2005/08/addressing/anonymous") || replyTo.contentEquals("http://www.w3.org/2005/08/addressing/none")) {
                String m = this.makeSoapFault("Invalid message", "Invalid wsa:ReplyTo (explicit anonymous or none)", "1000");
                synchronousResponse(500, "Internal server error", m, req, resp, null);
                return;
            }
            if (replyTo.startsWith("urn:nhs-itk:subscriber:")) {
                try {
                    String queuename = replyTo.substring(23);
                    ToolkitService svc = asyncHandler.getToolkit().getService("QueueManager");
                    if (svc == null) {
                        String m = this.makeSoapFault("Invalid server configuration", "wsa:ReplyTo specified queue and queue manager not running", "2100");
                        synchronousResponse(500, "Internal server error", m, req, resp, null);
                        return;
                    }
                    if (svc.execute(null, queuename) == null) {
                        String m = this.makeSoapFault("Invalid message", "Invalid wsa:ReplyTo (no such queue)", "1000");
                        synchronousResponse(500, "Internal server error", m, req, resp, null);
                        return;
                    }
                }
                catch(IndexOutOfBoundsException eIndex) {
                    String m = this.makeSoapFault("Invalid message", "Invalid wsa:ReplyTo (bad queue specification given)", "1000");
                    synchronousResponse(500, "Internal server error", m, req, resp, null);
                    return;
                }
            } else {
                if (!replyTo.startsWith("http")) {
                    String m = this.makeSoapFault("Invalid message", "Invalid wsa:ReplyTo (neither http nor queue specification given): " + replyTo, "1000");
                    synchronousResponse(500, "Internal server error", m, req, resp, null);
                    return;
                }
            }
            if (faultTo != null) {
                if (faultTo.startsWith("urn:nhs-itk:subscriber:")) {
                    try {
                        String queuename = faultTo.substring(23);
                        ToolkitService svc = asyncHandler.getToolkit().getService("QueueManager");
                        if (svc == null) {
                            String m = this.makeSoapFault("Invalid server configuration", "wsa:FaultTo specified queue and queue manager not running", "2100");
                            synchronousResponse(500, "Internal server error", m, req, resp, null);
                            return;
                        }
                        if (svc.execute(null, queuename) == null) {
                            String m = this.makeSoapFault("Invalid message", "Invalid wsa:FaultTo (no such queue)", "1000");
                            synchronousResponse(500, "Internal server error", m, req, resp, null);
                            return;
                        }
                    }
                    catch(IndexOutOfBoundsException eIndex) {
                        String m = this.makeSoapFault("Invalid message", "Invalid wsa:FaultTo (bad queue specification given)", "1000");
                        synchronousResponse(500, "Internal server error", m, req, resp, null);
                        return;
                    }
                } else {
                    if (!faultTo.startsWith("http")) {
                        String m = this.makeSoapFault("Invalid message", "Invalid wsa:FaultTo (neither http nor queue specification given)", "1000");
                        synchronousResponse(500, "Internal server error", m, req, resp, null);
                        return;
                    }
                }
            }
            ToolkitService svc = asyncHandler.getToolkit().getService("RulesEngine");
            if (svc != null) {
                r = svc.execute(soapaction, request);
                if (r != null) {
                    if (r.getCode() < 300) {
                        if (r.getCode() == 0) {
                            doProcess(req, resp);
                            return;
                        }
                        synchronousAck(req, resp);
                        asynchronousResponse(r);
                    } else {
                        synchronousResponse(r.getCode(), "Internal server error", r.getResponse(), req, resp, null);
                    }
                } else {
                    req.setHandled(true);
                    resp.forceClose();
                }
            } else {
                doProcess(req, resp);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                synchronousResponse(500, "Internal server error", "Error reading message: " + e.getMessage(), req, resp, null);
            }
            catch (Exception e1) {
                throw new HttpException("Exception reading request: " + e.getMessage() + " : " + e1.getMessage());
            }
            throw new HttpException("Exception reading request: " + e.getMessage());
        }
    }

    protected void doProcess(HttpRequest req, HttpResponse resp)
            throws Exception
    {
        ServiceResponse r = null;
        ToolkitService svc = asyncHandler.getToolkit().getService("Processor");
         if (svc != null) {
            ProcessData p = new ProcessData(soapaction, request);
            p.setRequestContext(req.getContext());
            p.setAsynchronous();
            r = svc.execute(p);
            if (r != null) {
                if (r.getCode() < 300) {
                    synchronousAck(req, resp);
                    asynchronousResponse(r);
                } else {
                    synchronousResponse(r.getCode(), "Internal server error", r.getResponse(), req, resp, null);
                }
            } else {
                req.setHandled(true);
                resp.forceClose();
            }
        } else {
            throw new Exception("No rules r process defined for action: " + soapaction);
        }
    }

    protected void synchronousAck(HttpRequest req, HttpResponse resp)
            throws Exception
    {
        if (asyncHandler.getAckLoadException() != null) {
            throw asyncHandler.getAckLoadException();
        }
        if (asyncHandler.getSyncAckTemplate() == null) {
            synchronousResponse(202, "Accepted", "", req, resp, null);
            return;
        }
        String ackTime = ISO8601FORMATDATE.format(new Date());
        String ackID = UUIDGenerator.getInstance().generateTimeBasedUUID().toString().toUpperCase();

        StringBuilder ack = new StringBuilder(asyncHandler.getSyncAckTemplate());
        substitute(ack, MESSAGEID, ackID);
        substitute(ack,TIMESTAMP, ackTime);
        substitute(ack, TO, replyTo);
        substitute(ack, ORIGINALMESSAGEID, messageId);
        synchronousResponse(200, "OK", ack.toString(), req, resp, null);
    }
/*
    private void substitute(StringBuffer sb, String tag, String content)
    {
        if (content == null) {
            return;
        }
        int tLength = tag.length();
        int tagPoint = -1;
        while ((tagPoint = sb.indexOf(tag)) != -1 ) {
            sb.replace(tagPoint, tagPoint + tLength, content);
        }
    }
*/
    protected String getAsyncWrapper(ServiceResponse ruleresponse)
            throws Exception
    {
        StringBuilder sb = new StringBuilder(asyncHandler.getAsyncWrapper());
        Date now = new Date();
        String ts = ISO8601FORMATDATE.format(now);
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.SECOND, asyncHandler.getAsyncTTL());
        Date expires = cal.getTime();
        String exp = ISO8601FORMATDATE.format(expires);
        substitute(sb, MESSAGEID, UUIDGenerator.getInstance().generateTimeBasedUUID().toString().toUpperCase());
        if ((faultTo != null) && (faultTo.trim().length() != 0)) {
            if ((ruleresponse.getAction() != null) && (ruleresponse.getAction().contentEquals("http://schemas.xmlsoap.org/ws/2004/08/addressing/fault"))) {
                substitute(sb, TOADDRESS, faultTo);
            } else {
                if ((replyTo != null) && (replyTo.trim().length() != 0)) {
                    substitute(sb, TOADDRESS, replyTo);
                } else {
                    substitute(sb, TOADDRESS, from);
                }
            }
        } else {
            if ((replyTo != null) && (replyTo.trim().length() != 0)) {
                substitute(sb, TOADDRESS, replyTo);
            } else {
                substitute(sb, TOADDRESS, from);
            }
        }
        if (ruleresponse.getAction() == null) {
            substitute(sb, ACTION, soapaction + "Response");
        } else{
            substitute(sb, ACTION, ruleresponse.getAction());
        }
        substitute(sb, ORIGINALMESSAGEID, messageId);
        if (substitute(sb, TIMESTAMP, ts) && substitute(sb, EXPIRES, exp)) {
            String rm = sb.toString();
            //
            // See if we're signing
            //
            ToolkitService svc = asyncHandler.getToolkit().getService("Signer");
            if (svc != null) {
                try {
                    ServiceResponse r = svc.execute("sign", rm);
                    return r.getResponse();
                }
                catch (Exception e) {
                    Logger.getInstance().log("Sender", "Error signing message : " + e.getMessage());
                    throw e;
                }
            }
        }

        return sb.toString();
    }

    protected void asynchronousResponse(ServiceResponse ruleresponse)
            throws Exception
    {
        ToolkitService svc = null;
        String rm = ruleresponse.getResponse();
        ServiceResponse r = ruleresponse;
        String s = getAsyncWrapper(ruleresponse);
        svc = asyncHandler.getToolkit().getService("Sender");
        if (svc == null) {
            throw new Exception("Asked to send asynchronous response but no Sender service included");
        }
        SenderRequest sreq = null;
        if (r.getCode() < 300) {
            if ((faultTo != null) && (faultTo.length() == 0)) {
                sreq = new SenderRequest(rm, s, replyTo);
            } else {
                if ((ruleresponse.getAction() != null) && (ruleresponse.getAction().contentEquals("http://schemas.xmlsoap.org/ws/2004/08/addressing/fault"))) {
                    sreq = new SenderRequest(rm, s, faultTo);
                } else {
                    sreq = new SenderRequest(rm, s, replyTo);
                }
            }
            sreq.setRelatesTo(messageId);
            sreq.setAction(ruleresponse.getAction());
            svc.execute(sreq);
        } else {
            if (faultTo == null) {
                sreq = new SenderRequest(rm, s, replyTo);
                sreq.setAction(ruleresponse.getAction());
                sreq.setRelatesTo(messageId);
                svc.execute(sreq);
            } else {
                sreq = new SenderRequest(rm, s, faultTo);
                sreq.setAction(ruleresponse.getAction());
                sreq.setRelatesTo(messageId);
                svc.execute(sreq);
            }
        }
    }

}
