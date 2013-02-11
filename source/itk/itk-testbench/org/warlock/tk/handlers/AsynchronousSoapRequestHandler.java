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
import org.warlock.tk.boot.Toolkit;
import org.warlock.http.HttpException;
import org.warlock.http.HttpRequest;
import org.warlock.http.HttpResponse;

/**
 * Handler implementation for asynchronous ITK SOAP requests. TKW wants a single
 * class as the handler for a request, but this request will have been put in its
 * own thread by the HTTP server. So for thread safety this class' handle() method
 * creates a separate AsynchronousWorker instance to handle this particular request,
 * and calls handle() on that.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class AsynchronousSoapRequestHandler
    extends org.warlock.tk.handlers.SynchronousSoapRequestHandler
{

    private static final String SYNCACKTEMPLATE = "tks.soap.ack.template";
    private static final String ASYNCWRAPPER = "tks.asynchronousreply.wrapper";
    private static String syncAckTemplate = null;
    private static String asyncWrapper = null;

    private Exception ackLoadException = null;

    public AsynchronousSoapRequestHandler() 
        throws Exception
    {
        super();
    }

    @Override
    public void setToolkit(Toolkit t)
        throws Exception
    {
        super.setToolkit(t);
        synchronized(this) {
            try {
                String ackFile = toolkit.getBootProperties().getProperty(SYNCACKTEMPLATE);
                if ((ackFile != null) && (!ackFile.toUpperCase().contentEquals("NONE"))) {
                    syncAckTemplate = load(toolkit.getBootProperties().getProperty(SYNCACKTEMPLATE));
                }
                asyncWrapper = load(toolkit.getBootProperties().getProperty(ASYNCWRAPPER));
            }
            catch (Exception e) {
                ackLoadException = e;
                throw e;
            }
        }
    }

    @Override
    public void handle(String path, String params, HttpRequest req, HttpResponse resp)
        throws HttpException
    {
        AsynchronousWorker a = new AsynchronousWorker(this);
        a.handle(path, params, req, resp);
    }
    Exception getAckLoadException() { return ackLoadException; }
    String getSyncAckTemplate() { return syncAckTemplate; }
    String getAsyncWrapper() { return asyncWrapper; }

}
