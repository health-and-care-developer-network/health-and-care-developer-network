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
package org.warlock.tk.internalservices.process;
 /**
 * Class to carry a request to the TKW simulator, that is to be passed to a
 * process. This holds fields and boilerplate accessors for the message, context 
 * path, id and SOAP action, and a flag to indicate whether it was received in 
 * a synchronous or asynchronous request.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class ProcessData {

    private String envelope = null;
    private String soapAction = null;
    private String requestId = null;
    private String requestContext = null;
    private boolean asynchronous = false;
    private ProcessorSoapFaultResponse faultResponse = null;

    public ProcessData(String action, String env)
    {
        envelope = env;
        soapAction = action;
    }
    public void setRequestId(String i) { requestId = i; }
    public String getRequestId() { return requestId; }
    
    public void setRequestContext(String c) { requestContext = c; }
    public String getRequestContext() { return requestContext; }
    
    public void setAsynchronous() { asynchronous = true; }
    public boolean isAsynchronous() { return asynchronous; }

    public ProcessorSoapFaultResponse getFaultResponse() { return faultResponse; }
    public void setFaultResponse(ProcessorSoapFaultResponse p) { faultResponse = p; }

    public String getAction() { return soapAction; }
    public String getEnvelope() { return envelope; }
}
