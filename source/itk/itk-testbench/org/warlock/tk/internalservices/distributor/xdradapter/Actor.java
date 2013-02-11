/*
  Copyright 2012 Damian Murphy <murff@warlock.org>

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

package org.warlock.tk.internalservices.distributor.xdradapter;

/**
 * Coded sender or recipient from XDR metadata.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class Actor {
    private String type = null;
    private String uri = null;

    public Actor() {}
    public Actor(String t, String u) {
        type = t;
        uri = u;
    }

    public void setType(String t) { type = t; }
    public void setUri(String u) { uri = u; }
    
    public String getType() { return type; }
    
    public String getURI() { return uri; }
    
}
