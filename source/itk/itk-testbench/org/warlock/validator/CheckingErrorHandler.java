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

package org.warlock.validator;

import org.xml.sax.SAXParseException;
 /**
 * Exception reporting class for the Spine schema validator.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class CheckingErrorHandler 
        implements org.xml.sax.ErrorHandler 
{
    private XmlValidatorErrorReporter caller = null;    
    /** Creates a new instance of CheckingErrorHandler */
    CheckingErrorHandler(XmlValidatorErrorReporter c) {
        caller = c;
    }

    @Override
    public void error(SAXParseException e) {
        if (!caller.getErrorDetected()) {
            caller.setErrorDetected();
            makeErrorString("ERROR", e);
        }
    }

    @Override
    public void fatalError(SAXParseException e) {
        if (!caller.getErrorDetected()) {
            caller.setErrorDetected();
            makeErrorString("FATAL ERROR", e);
        }
    }

    @Override
    public void warning(SAXParseException e) {
        makeErrorString("Warning", e);
    }
    
    private void makeErrorString(String type, SAXParseException e) {
        StringBuilder sb = new StringBuilder(type);
        if (e.getLineNumber() != -1) {
            sb.append(" at line  ");
            sb.append(e.getLineNumber());
        }
        if (e.getColumnNumber() != -1) {
            sb.append(", column ");
            sb.append(e.getColumnNumber());
        }
        if (e.getSystemId() != null) {
            sb.append(" in ");
            sb.append(e.getSystemId());
        }
        if (e.getPublicId() != null) {
            sb.append(":");
            sb.append(e.getPublicId());
            sb.append(".");
        }
        sb.append("Reason: ");
        sb.append(e.getMessage());
        caller.setErrorString(sb.toString());
    }        

}
