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
package org.warlock.tk.internalservices.rules;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import org.warlock.tk.boot.ServiceResponse;
/**
 * Class for making simulated responses to requests. Can be based on a template
 * file, or on a class that implements the Responder interface.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
class Response {

    // Currently two types of response supported: "class" and "file"
    private String name = null;
    private String url = null;

    private Responder responseClass = null;
    private String template = null;
    private String action = null;
    private int responseCode = 0;
    private boolean noResponse = false;

    Response(String n, String u)
            throws Exception
    {
        name = n;
        url = u;
        init();
    }
    void setCode(int i) { responseCode = i; }
    void setAction(String a) { action = a; }
    private void init()
            throws Exception
    {
        if (url.contentEquals("NONE")) {
            noResponse = true;
            return;
        }
        String resource = null;
        if (url.startsWith("class:")) {
            resource = url.substring(6);
            responseClass = (Responder)Class.forName(resource).newInstance();
        } else {
            BufferedReader br = new BufferedReader(new FileReader(url));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
            template = sb.toString();
            br.close();
        }
    }

    ServiceResponse instantiate(ArrayList<Substitution> substitutions, String o)
            throws Exception
    {
        if (noResponse) {
            return new ServiceResponse(responseCode, "");
        }
        if (responseClass != null) {
            ServiceResponse s = new ServiceResponse(responseCode, responseClass.makeResponse(o));
            s.setAction(action);
            return s;
        } else {
            ServiceResponse s = new ServiceResponse(responseCode, substituteTemplate(substitutions, o));
            s.setAction(action);
            return s;
        }
    }

    private String substituteTemplate(ArrayList<Substitution> substitutions, String o)
        throws Exception
    {
        if (noResponse) {
            return "";
        }
        StringBuffer sb = new StringBuffer(template);
        for (Substitution s : substitutions) {
            s.substitute(sb, o);
        }
        return sb.toString();
    }
}
