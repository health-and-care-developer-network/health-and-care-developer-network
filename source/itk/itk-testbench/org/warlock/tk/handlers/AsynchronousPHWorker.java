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

/**
 * Worker class for asynchronous HL7v2 pipe-and-hat messages. Converts the 
 * received pipe-and-hat request to XML, and any XML response other than ITK
 * "SimpleITKResponse" back to pipe-and-hat. This is instantiated by the
 * AsynchronousSoapPipeAndJatRequestHander, which also calls the handle()
 * method on it.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class AsynchronousPHWorker
    extends AsynchronousWorker
{
    AsynchronousPHWorker(AsynchronousSoapPipeAndHatRequestHandler asrh) {
        super(asrh);
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
                    String m = this.makeSoapFault("Invalid message", "Invalid wsa:ReplyTo (neither http nor queue specification given)", "1000");
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
                        String m = this.makeSoapFault("Invalid message", "Invalid wsa:faultTo (neither http nor queue specification given)", "1000");
                        synchronousResponse(500, "Internal server error", m, req, resp, null);
                        return;
                    }
                }
            }
            ToolkitService svc = asyncHandler.getToolkit().getService("RulesEngine");
            if (svc != null) {
                String dc = extractPH();
                r = svc.execute(soapaction, dc);
                if (r != null) {
                    if (r.getCode() < 300) {
                        if (r.getCode() == 0) {
                            doProcess(req, resp);
                            return;
                        }
                        synchronousAck(req, resp);
                        if (!r.getResponse().contains("SimpleMessageResponse")) {
                            String phresponse = makePH(r.getResponse());
                            r.setResponse(phresponse);
                        }
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

    private String makePH(String x)
            throws Exception
    {
        ToolkitService svc = asyncHandler.getToolkit().getService("PipeAndHatAdapter");
        if (svc == null) {
            throw new Exception("PipeAndHat / XML adapter service not available");
        }
        ServiceResponse s = svc.execute(x);
        return s.getResponse();
    }

    private String extractPH()
            throws Exception
    {
        ToolkitService svc = asyncHandler.getToolkit().getService("PipeAndHatAdapter");
        if (svc == null) {
            throw new Exception("PipeAndHat / XML adapter service not available");
        }
        ServiceResponse s = svc.execute(soapaction, soapRequest);
        return s.getResponse();
    }

}
