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
import org.warlock.tk.boot.ToolkitService;
import org.warlock.http.HttpRequest;
import org.warlock.http.HttpResponse;
import org.warlock.http.HttpException;
import org.warlock.tk.internalservices.process.ProcessorSoapFaultResponse;
import org.warlock.tk.internalservices.process.ProcessData;
/**
 * Subclass of SynchronousWorker for handling HL7v2 pipe-and-hat messages. Reads and
 * checks a correctly-presented synchronous message, coverts the payload to HL7v2 XML,
 * then applies simulator rule set or a process to it. Any response other than 
 * SimpleITKResponse or a SOAP fault is expected in HL7v2 XML form, and is converted
 * back to pipe-and-hat before being returned synchronously to the requestor.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
class SynchronousPHWorker
    extends SynchronousWorker
{
    private SynchronousSoapRequestHandler handler = null;

    SynchronousPHWorker(SynchronousSoapRequestHandler h) {
        super(h);
        handler = h;
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
            if ((replyTo != null) && (replyTo.length() > 0) && (!replyTo.contentEquals("http://www.w3.org/2005/08/addressing/anonymous"))) {
                String m = this.makeSoapFault("Invalid message", "Invalid wsa:ReplyTo (not explicit anonymous or none)", "1000");
                synchronousResponse(500, "Internal server error", m, req, resp, null);
                return;
            }
            ToolkitService svc = handler.getToolkit().getService("RulesEngine");
            if (svc != null) {
                // Extract, decode and convert the PH
                String dc = extractPH();
                r = svc.execute(soapaction, dc);
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
                        if (r.getResponse().contains("SimpleMessageResponse")) {
                            synchronousResponse(r.getCode(), hresp, r.getResponse(), req, resp, r.getAction());
                        } else {
                            String phresponse = makePH(r.getResponse());
                            synchronousResponse(r.getCode(), hresp, phresponse, req, resp, r.getAction());
                        }
                    } else {
                        hresp = "Internal server error";
                        synchronousResponse(r.getCode(), hresp, r.getResponse(), req, resp, "http://www.w3.org/2005/08/addressing/soap/fault");
                    }
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

    private String makePH(String x)
            throws Exception
    {
        ToolkitService svc = handler.getToolkit().getService("PipeAndHatAdapter");
        if (svc == null) {
            throw new Exception("PipeAndHat / XML adapter service not available");
        }
        ServiceResponse s = svc.execute(x);
        return s.getResponse();
    }

    private String extractPH()
            throws Exception
    {
        ToolkitService svc = handler.getToolkit().getService("PipeAndHatAdapter");
        if (svc == null) {
            throw new Exception("PipeAndHat / XML adapter service not available");
        }
        ServiceResponse s = svc.execute(soapaction, soapRequest);
        return s.getResponse();
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
    
}
