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
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import org.warlock.http.HttpRequest;
import org.warlock.http.HttpResponse;
import org.warlock.http.HttpException;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.boot.ToolkitService;
import org.warlock.util.CfHNamespaceContext;
import org.warlock.util.XMLNamespaceContext;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.warlock.tk.internalservices.process.ProcessData;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import org.safehaus.uuid.UUIDGenerator;
import java.io.FileWriter;
import org.warlock.tk.internalservices.process.ProcessorSoapFaultResponse;
import org.warlock.itklogverifier.LogVerifier;
/**
 * Class to handle the work of the SynchronousSoapRequestHandler in a thread-safe
 * way.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
class SynchronousWorker {

    private SynchronousSoapRequestHandler handler = null;

    protected static final String TIMESTAMP = "__TIMESTAMP__";
    protected static final String EXPIRES = "__EXPIRES__";
    protected static final String PAYLOAD = "__PAYLOAD_BODY__";
    protected static final String MESSAGEID = "__MESSAGEID__";
    protected static final String ORIGINALMESSAGEID = "__ORIGINAL_MESSAGEID__";
    protected static final String ACTION = "__ACTION__";
    protected static final String TOADDRESS = "__TO_ADDRESS__";

    protected static XMLNamespaceContext ns = CfHNamespaceContext.getXMLNamespaceContext();

    private static SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static SimpleDateFormat FILEDATE = new SimpleDateFormat("yyyyMMddHHmmss");

    protected String request = null;
    protected String soapaction = null;
//    protected Node soapHeader = null;
//    protected Node soapBody = null;
    protected Node soapRequest = null;

    protected String messageId = null;
    protected String replyTo = null;
    protected String faultTo = null;
    protected static final String from = "http://www.w3.org/2005/08/addressing/anonymous";
    protected String to = null;

    protected FileWriter logfile = null;

    SynchronousWorker(SynchronousSoapRequestHandler ssrh) {
        handler = ssrh;
    }

    public void handle(String path, String params, HttpRequest req, HttpResponse resp)
        throws HttpException
    {
        ServiceResponse r = null;
        try {
            if (!doChecks(path, params, req, resp)) {
                return;
            }
            if ((replyTo != null) && (replyTo.length() > 0) && (!replyTo.contentEquals("http://www.w3.org/2005/08/addressing/anonymous"))) {
                String m = this.makeSoapFault("Invalid message", "Invalid wsa:ReplyTo (not explicit anonymous or none)", "1000");
                synchronousResponse(500, "Internal server error", m, req, resp, null);
                return;
            }
            ToolkitService svc = handler.getToolkit().getService("RulesEngine");
            if (svc != null) {
                r = svc.execute(soapaction, request);
                if (r != null) {
                    if (r.getCode() == 0) {
                        doProcessor(req, resp);
                        return;
                    }
                    if (r.getCode() == -1) {
                        String m = makeSoapFault("Rules error", r.getResponse(), "2100");
                        synchronousResponse(500, "Internal server error", m, req, resp, "http://www.w3.org/2005/08/addressing/soap/fault");
                        return;
                    }
                    String hresp = null;
                    if (r.getCode() < 300) {
                        hresp = "OK";
                    } else {
                        hresp = "Internal server error";
                    }
                    synchronousResponse(r.getCode(), hresp, r.getResponse(), req, resp, r.getAction());
                    return;
                } else {
                    req.setHandled(true);
                    resp.forceClose();
                }
            } else {
                doProcessor(req, resp);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                synchronousResponse(500, "Internal server error", "Error reading message: " + e.getMessage(), req, resp, "http://www.w3.org/2005/08/addressing/soap/fault");
            }
            catch (Exception e1) {
                throw new HttpException("Exception reading request: " + e.getMessage() + " : " + e1.getMessage());
            }
            throw new HttpException("Exception reading request: " + e.getMessage());
        }
    }
    protected String makeSoapFault(String errText, String errDetail, String code)
            throws Exception
    {
        StringBuilder sb = new StringBuilder(handler.getSoapFault());
        substitute(sb, "__ERRORID__", UUIDGenerator.getInstance().generateTimeBasedUUID().toString().toUpperCase());
//        substitute(sb, ORIGINALTO, to);
        substitute(sb, "__ERRORTEXT__", errText);
        substitute(sb, "__ERRORDETAIL__", errDetail);
        substitute(sb, "__ERRORCODE__", code);
        return sb.toString();
    }

    private void doProcessor(HttpRequest req, HttpResponse resp)
            throws Exception
    {
        ServiceResponse r = null;
        ToolkitService svc = handler.getToolkit().getService("Processor");
        if (svc != null) {
            ProcessData p = new ProcessData(soapaction, request);
            p.setRequestContext(req.getContext());
            p.setRequestId(messageId);
            r = svc.execute(p);
            if (r != null) {
                String hresp = null;
                if (r.getCode() < 300) {
                    hresp = "OK";
                    synchronousResponse(r.getCode(), hresp, r.getResponse(), req, resp, r.getAction());
                } else {
                    ProcessorSoapFaultResponse psfr = r.getProcessorFault();
                    String m = makeSoapFault(psfr.getErrorMessage(), psfr.getErrorDetail(), psfr.getFaultCode());
                    synchronousResponse(r.getCode(), "Internal server error", m, req, resp, "http://www.w3.org/2005/08/addressing/soap/fault");
                }
                return;
            } else {
                req.setHandled(true);
                resp.forceClose();
            }
        } else {
            throw new Exception("No rules or process defined for action: " + soapaction);
        }
    }

    protected boolean substitute(StringBuilder sb, String tag, String content)
            throws Exception
    {
        boolean doneAnything = false;
        int tagPoint = -1;
        int tagLength = tag.length();
        if (content == null) {
            content = "";
        }
        while ((tagPoint = sb.indexOf(tag)) != -1 ) {
            sb.replace(tagPoint, tagPoint + tagLength, content);
            doneAnything = true;
        }
        return doneAnything;
    }

    protected boolean doChecks(String path, String params, HttpRequest req, HttpResponse resp)
        throws HttpException
    {
        ServiceResponse r = null;
        try {
            readMessage(req);
            // By now we've read the message - first thing to do is to see if
            // we're going to try to verify the signature, and do so.
            ToolkitService svc = null;
            svc = handler.getToolkit().getService("Signer");
            if (svc != null) {
                r = svc.execute("verify", request);
                if (r.getCode() == 0) {
                    String m = makeSoapFault("Access denied", "Invalid signature", "3000");
                    synchronousResponse(500, "Internal server error", m, req, resp, null);
                    return false;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                String m = this.makeSoapFault("Error reading message", "Error reading message: " + e.getMessage(), "1000");
                synchronousResponse(500, "Internal server error", m, req, resp, null);
            }
            catch (Exception e1) {
                throw new HttpException("Exception reading request: " + e.getMessage() + " : " + e1.getMessage());
            }
            throw new HttpException("Exception reading request: " + e.getMessage());
        }
        return true;
    }

    protected StringBuilder makeWrapper(String action)
        throws Exception
    {
        StringBuilder wrapped = new StringBuilder(handler.getSynchronousWrapper());
        substitute(wrapped, MESSAGEID, UUIDGenerator.getInstance().generateTimeBasedUUID().toString().toUpperCase());
        substitute(wrapped, ORIGINALMESSAGEID, messageId);
        if (action != null) {
            substitute(wrapped, ACTION, action);
        } else {
            substitute(wrapped, ACTION, soapaction + "Response");
        }
        return wrapped;
    }

    protected void synchronousResponse(int i, String s, String m, HttpRequest req, HttpResponse resp, String action)
            throws Exception
    {
        if (i == -1) {
            resp.setStatus(200, "OK");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()));
            bw.write(m);
            bw.flush();
            req.setHandled(true);
            return;            
        }
        if (i == 202) {
            resp.setStatus(i, s);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()));
            bw.write(m);
            bw.flush();
            req.setHandled(true);
            return;
        }
        StringBuilder wrapped = makeWrapper(action);
        String tosend = null;
        Date now = new Date();
        String ts = ISO8601FORMATDATE.format(now);
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.SECOND, handler.getAsyncTTL());
        Date expires = cal.getTime();
        String exp = ISO8601FORMATDATE.format(expires);
        substitute(wrapped, PAYLOAD, m);
        if (substitute(wrapped, TIMESTAMP, ts) && substitute(wrapped, EXPIRES, exp)) {
            tosend = wrapped.toString();
            ToolkitService tks = handler.getToolkit().getService("Signer");
            if (tks != null) {
                ServiceResponse r = tks.execute("sign", tosend);
                tosend = r.getResponse();
            }
        } else {
            tosend = wrapped.toString();
        }
        resp.setStatus(i, s);
        if (tosend.trim().length() != 0) {
            resp.setContentType("text/xml");
        }
        if (action != null) {
            resp.setField("SOAPaction", action);
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()));
        bw.write(tosend);
        bw.flush();
        req.setHandled(true);
        logfile.write("\r\n****\r\n");
        logfile.flush();
        logfile.write(tosend);
        logfile.flush();
        logfile.close();
    }

    protected void readMessage(HttpRequest req)
            throws Exception
    {
        soapaction = req.getField("SOAPaction");
        if (soapaction == null) {
            throw new Exception("No SOAPaction HTTP header found in request");
        }
        StringBuilder qcheck = new StringBuilder(soapaction);
        if (qcheck.length() > 0) {
            if (qcheck.charAt(0) == '"') {
                qcheck = qcheck.deleteCharAt(0);
                if (qcheck.charAt(qcheck.length() - 1) == '"') {
                    qcheck = qcheck.deleteCharAt(qcheck.length() - 1);
                }
                soapaction = qcheck.toString();
            }
        } else {
            throw new Exception("No SOAPaction");
        }
        InputStreamReader isr = new InputStreamReader(req.getInputStream());
        char[] buf = new char[req.getContentLength()];
        int totalRead = 0;
        int dataSize = 0;
        while (totalRead < req.getContentLength()) {
            dataSize = isr.read(buf, totalRead, req.getContentLength() - totalRead);
            if (dataSize == -1) {
                break;
            }
            totalRead += dataSize;
        }
        if (totalRead != req.getContentLength()) {
            throw new Exception("Given data is not the same size as content length: " + totalRead + "/" + req.getContentLength());
        }
        request = new String(buf);
        resolveMessageId();
        String smd = handler.getSavedMessagesDirectory();
        if (smd != null) {
            StringBuilder sb= new StringBuilder(smd);
            sb.append("/");
            sb.append(soapaction);
            int colon = -1;
            while ((colon = sb.indexOf(":", smd.indexOf(":") + 1)) != -1) {
                sb.setCharAt(colon, '_');
            }
            sb.append("_");
            sb.append(FILEDATE.format(new Date()));
            sb.append("_");
            sb.append(messageId);
            sb.append(".log");
            String rmlog = sb.toString();
            logfile = new FileWriter(rmlog);
            logfile.write(req.getMethod());
            logfile.write(" ");
            logfile.write(req.getContext());
            logfile.write(" HTTP/1.1\r\n");
            for (String s: req.getFieldNames()) {
                String v = req.getField(s);
                logfile.write(s);
                logfile.write(": ");
                logfile.write(v);
                logfile.write("\r\n");
            }
            logfile.write("\r\n");
            logfile.write(request);
            logfile.flush();
            logfile.write("\r\n************ END OF INBOUND MESSAGE **************\r\n\r\n");
            logfile.flush();
            
            // logfile.close();
            String dontDoSignature = System.getProperty("tks.skipsignlogs");
            if ((dontDoSignature == null) || (dontDoSignature.toUpperCase().startsWith("Y"))) {
                LogVerifier l = LogVerifier.getInstance();
                l.makeSignature(rmlog);
            }

        }
        resolveReplyAddress();
        getSOAPparts();
    }

    private void getSOAPparts()
            throws Exception
    {
        InputSource is = new InputSource(new StringReader(request));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        if (!db.isNamespaceAware()) {
            throw new Exception("The implementation of DocumentBuilderFactory used here is unable to be set namespace aware");
        }
        Document doc = db.parse(is);
        doc.normalizeDocument();
        soapRequest = handler.extractSoapRequest(doc);
//        soapHeader = handler.extractHeader(doc);
//        soapBody = handler.extractBody(doc);
    }

    private void resolveMessageId()
            throws Exception
    {
        try {
            messageId = handler.extractMessageId(request);
        }
        catch (Exception e) {
            throw new Exception("Error reading message id: Message is not well formed : " + e.getMessage());
        }
    }

    private void resolveReplyAddress()
            throws Exception
    {
        try {
            replyTo = handler.extractReplyTo(request);
            faultTo = handler.extractFaultTo(request);
        }
        catch (Exception e) {
            throw new Exception("Error resolving reply addresses : Message is not well formed " + e.getMessage());
        }
    }

}
