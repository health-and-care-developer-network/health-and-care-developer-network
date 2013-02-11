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
package org.warlock.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.OutputStreamWriter;
/**
 * HTTP Server to support test and reference applications. This class is provided
 * as a container for the other server components. It exists to allow testing of
 * network conditions such as lost connections under programmatic control, unlike
 * a "proper" HTTP server where the primary purpose is to implement the HTTP protocol
 * correctly.
 * 
 * To use, create the server, add listeners and contexts, then start it.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class HttpServer {

    private ArrayList<Listener> listeners;    
    private HashMap<String, HttpContext> contexts;
    
    /** Creates a new instance of HttpServer */
    public HttpServer() 
    {
        listeners = new ArrayList<Listener>();
        contexts = new HashMap<String,HttpContext>();
    }
    
    public void start() 
        throws Exception
    {
        for (Listener s : listeners) {
            s.startListening(this);
        }
        System.out.println("Running...");
    }

    public void stop()
            throws Exception
    {
        for (Listener l : listeners) {
            l.stopListening();
        }
    }
    
    public void addListener(Listener s) 
        throws Exception
    {
        if (s == null) {
            throw new Exception("Attempt to add null socket listener to server");
        }
        listeners.add(s); 
    }
    public void addContext(HttpContext c) 
        throws Exception
    {
        if (c == null) {
            throw new Exception("Attempt to add null context to server");
        }
        contexts.put(c.getContextPath(), c);
    }
    
    void addRequest(HttpRequest r)         
    {
        HttpContext c = null;
        if (r == null) {
            return;
        }
        if (contexts.containsKey(r.getContext())) {
            c = contexts.get(r.getContext());
        } else {
           if (!contexts.containsKey("/")) {
               try {
                   OutputStreamWriter w = new OutputStreamWriter(r.getResponse().getOutputStream());
                   w.write("HTTP/1.1 500 Server configuration error, no default or root context handler found");
                   w.flush();
               }
               catch (Exception e) {
                   String s = new String("Request from " + r.getSourceId() + " server configuration error, no default or root context handler found: " + e.getMessage());
                   System.err.println(s);
                   LastResortReporter.report(s, r.getResponse().getOutputStream());                   
               }
               return;
           } else {
               c = contexts.get("/");
           }
        }
        Iterator<Handler> handlers = c.getHandlers();
        while (handlers.hasNext()) {
            Handler h = handlers.next();
            try {
                h.handle(r.getContext(), null, r, r.getResponse());
                if (r.isHandled())
                    break;
            }
            catch (Exception e) {
                String s = new String("Request from " + r.getSourceId() + " exception in handler chain: " + e.getMessage());
                System.err.println(s);
                LastResortReporter.report(s, r.getResponse().getOutputStream());
                return;
            }
        }
        if (!r.isHandled()) {
            System.err.println("Request from " + r.getSourceId() + " not handled by anything");
            LastResortReporter.report(null, r.getResponse().getOutputStream());
        }
    }
}
