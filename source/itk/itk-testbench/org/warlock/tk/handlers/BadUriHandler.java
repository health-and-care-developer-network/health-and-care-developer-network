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
import org.warlock.http.HttpRequest;
import org.warlock.http.HttpResponse;
import org.warlock.http.HttpException;
import java.io.OutputStreamWriter;

/**
 * Class to provide the HTTP server with a Handler implementation for those cases
 * where a connected system sends a request to an invalid URI. Returns HTTP 500.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class BadUriHandler
    extends org.warlock.tk.boot.ToolkitHttpHandler
{

    public BadUriHandler() {}

    @Override
    public void handle(String path, String params, HttpRequest req, HttpResponse resp)
        throws HttpException
    {
        try {
            resp.setStatus(500, "Invalid URI for service");
            resp.setContentType("text/plain");
            OutputStreamWriter osw = new OutputStreamWriter(resp.getOutputStream());
            osw.write("Bad path: " + path);
            osw.flush();
            req.setHandled(true);
        }
        catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }
}
