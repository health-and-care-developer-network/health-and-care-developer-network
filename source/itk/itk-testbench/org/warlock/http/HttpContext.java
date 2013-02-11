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
import java.util.Iterator;
/**
 * Implements the binding between HTTP request context paths, and the handlers that
 * service requests on those context paths.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class HttpContext {
    
    private String path;
    private ArrayList<Handler> handlers;
    
    /** Creates a new instance of HttpContext */
    public HttpContext() {
        handlers = new ArrayList<Handler>();
    }
    
    public void setContextPath(String p) 
        throws Exception
    {
        if ((p == null) || (p.trim().length() == 0)) {
            throw new Exception("Attempt to set invalid empty or zero-length context path in HttpContext.setContextPath()");
        }
        path = p;
    }
    
    public String getContextPath() { return path; }
    
    public void addHandler(Handler h) { handlers.add(h); }
    Iterator<Handler> getHandlers() { return handlers.iterator(); }
}
