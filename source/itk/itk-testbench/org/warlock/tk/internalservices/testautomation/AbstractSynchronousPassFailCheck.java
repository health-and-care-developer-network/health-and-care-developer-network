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
package org.warlock.tk.internalservices.testautomation;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
/**
 * The PassFailCheck classes read log files and apply tests to them. This abstract
 * subclass provides a method to extract the XML portion of a synchronous ITK response
 * from the "transmitter log file", as a SAX InputSource for use by XPath checkers.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
abstract public class AbstractSynchronousPassFailCheck 
    extends AbstractPassFailCheck
{
    protected String getResponseBody(InputStream in)
            throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String httpResponse = null;
        String line = null;
        boolean rq = false;
        boolean body = false;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            if (body) {
                sb.append(line);
                sb.append("\r\n");
            } else {
                if (line.startsWith("**** END REQUEST ****")) {
                    rq = true;
                    continue;
                }
                if (rq) {
                    if (line.trim().length() == 0) {
                        body = true;
                        continue;
                    }
                }
            }
        }        
        return sb.toString();
    }
}
