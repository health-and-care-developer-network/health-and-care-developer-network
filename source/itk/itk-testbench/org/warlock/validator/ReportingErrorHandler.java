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

import java.io.Writer;
import org.xml.sax.SAXParseException;
 /**
 * Exception reporting class for the Spine schema validator.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class ReportingErrorHandler   
    implements org.xml.sax.ErrorHandler
{
    
    private Writer output;
    /** Creates a new instance of ReportingErrorHandler */
    public ReportingErrorHandler(Writer w) {
        output = w;
    }

    public void error(SAXParseException e) {
        makeErrorString("ERROR", e);
    }
    
    public void fatalError(SAXParseException e) {
        makeErrorString("FATAL", e);
    }
    
    public void warning(SAXParseException e) {
        makeErrorString("Warning", e);
    }
    
    private void makeErrorString(String type, SAXParseException e) {
        boolean locationReported = false;
        StringBuilder sb = new StringBuilder("\n");
        sb.append(type);
        sb.append(": ");
        if (e.getLineNumber() != -1) {
            locationReported = true;
            sb.append(" at line  ");
            sb.append(e.getLineNumber());
        }
        if (e.getColumnNumber() != -1) {
            locationReported = true;
            sb.append(", column ");
            sb.append(e.getColumnNumber());
        }
        if (e.getSystemId() != null) {
            locationReported = true;
            sb.append(" in ");
            sb.append(e.getSystemId());
        }
        if (locationReported) {
            sb.append(".\n");
        }
        sb.append("Reason: ");
        sb.append(e.getMessage());
        //sb.append("\n");
        try {
            output.write(sb.toString());
            output.flush();
        }
        catch (Exception io) {
            // Just absorb this and let processing run off the end for now. We probably want to
            // pull in the Logger for it at some point.
        }
    }        
}
