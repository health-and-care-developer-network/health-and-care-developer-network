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
/**
 * Subclass of SynchronousSoapRequestHandler to instantiate and call handle()
 * on an SynchronousPHWorker.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class SynchronousSoapPipeAndHatRequestHandler
    extends SynchronousSoapRequestHandler
{
    public SynchronousSoapPipeAndHatRequestHandler()
        throws Exception
    {
        super();
    }

    @Override
    public void handle(String path, String params, HttpRequest req, HttpResponse resp)
        throws HttpException
    {
        SynchronousPHWorker s = new SynchronousPHWorker(this);
        s.handle(path, params, req, resp);
     }

}
