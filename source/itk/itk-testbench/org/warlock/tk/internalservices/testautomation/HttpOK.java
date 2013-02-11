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
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class HttpOK  
    extends AbstractPassFailCheck
{
    private static final int BUFFERSIZE = 1024;    
    
    @Override
    public boolean passed(Script s, InputStream in)
            throws Exception
    {        
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String httpResponse = null;
        String line = null;
        boolean rq = false;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("**** END REQUEST ****")) {
                rq = true;
                continue;
            }
            if (rq) {
                if (line.startsWith("HTTP/1.1 ")) {
                    httpResponse = line;
                    break;
                }
            }
        }
        if (httpResponse == null) {
            description = "Null HTTP response";
            return false;
        }
        if (extractor != null) {
            extract(in);
        }
        if (httpResponse.contains("200")) {
            description = "HTTP 200 received";
            return true;
        }
        description = "HTTP 200 expected, received: " + httpResponse;
        return false;
    }
    
    private void extract(InputStream in)
            throws Exception
    {
        // Read into a string, substring to \r\n\r\n and then pass that
        // to the extractor
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[BUFFERSIZE];
        int r = 0;
        while ((r = in.read(buffer, 0, buffer.length)) != -1) {
            if (r > 0) {
                sb.append(new String(buffer, 0, r));
            }
        }
        String body = sb.substring((sb.indexOf("\r\n\r\n" + 4)));
        extractor.extract(body);
    }
}
