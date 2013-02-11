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
import java.io.StringReader;
import org.xml.sax.InputSource;
/**
 *
 * @author Damian Murphy <murff@warlock.org>
 */
public class AsynchronousXPathAssertionPassFailCheck 
    extends SynchronousXPathAssertionPassFailCheck 
{
    @Override
    public boolean passed(Script s, InputStream in) 
            throws Exception
    {
        String responseBody = getResponseBody(in);
        InputSource is = new InputSource(new StringReader(responseBody));
        boolean p = doChecks(s, is);   
        doExtract(responseBody);
        return p;
    }
    
    @Override
    protected String getResponseBody(InputStream in)
            throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        boolean body = false;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            if (body) {
                if (line.contains("* END OF INBOUND MESSAGE *")) {
                    break;
                } else {
                    sb.append(line);
                    sb.append("\r\n");
                }
            } else {
                if (line.trim().length() == 0) {
                    body = true;
                }
            }
        }        
        return sb.toString();
    }
    
}
