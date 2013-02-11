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
package org.warlock.tk.boot;
import org.warlock.http.Handler;
import org.warlock.http.HttpRequest;
import org.warlock.http.HttpResponse;
import org.warlock.http.HttpException;
/**
 * Abstract superclass for the TKW HTTP handlers for synchronous and asynchronous
 * SOAP messages. Implementation of org.warlock.http.Handler
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public abstract class ToolkitHttpHandler
    implements Handler
{
    protected Toolkit toolkit = null;
    protected int asyncttl = 10;
    protected String savedMessagesDirectory = null;

    public void setToolkit(Toolkit t) throws Exception { toolkit = t; }
    public void setAsynchronousTTL(int i) { asyncttl = i; }
    
    public void setSavedMessagesDirectory(String s) { savedMessagesDirectory = s; }
    
    @Override
    public abstract void handle(String path, String params, HttpRequest req, HttpResponse resp)
        throws HttpException;
}
