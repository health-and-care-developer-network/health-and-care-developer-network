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
 * Class to allow a simulator process to return error information to the
 * simulator request handler with which it can construct a SOAP fault. The
  * class contains fields mapping to the ITK Toolkit Error Structure, and
  * "get" accessors for them.
  * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class ProcessorSoapFaultResponse {

    private String errorMessage = null;
    private String errorDetail = null;
    private String faultCode = null;

    public ProcessorSoapFaultResponse(String emsg, String edetail, String fc) {
        // TODO: Implement. Make. If asynchronous, include "RelatesTo" and call signer
        // if necessary
        errorMessage = emsg;
        errorDetail = edetail;
        faultCode = fc;
    }
    public String getErrorMessage() { return errorMessage; }
    public String getErrorDetail() { return (errorDetail == null) ? "" : errorDetail; }
    public String getFaultCode() { return faultCode; }
}
