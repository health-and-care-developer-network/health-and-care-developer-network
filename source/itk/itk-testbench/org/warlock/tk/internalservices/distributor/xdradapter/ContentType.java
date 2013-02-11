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
import java.util.HashMap;
 /**
 * Processor for HTTP content-type field for handling XDR messages.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class ContentType {
    
    private String type = null;
    private HashMap<String,String> parts = null;
    
    public ContentType(String line) {
        if (!line.contains(";")) {
            type = line;
        } else {
            String[] fields = line.split(";");
            parts = new HashMap<String,String>();
            type = fields[0];

            for (int i = 1; i < fields.length; i++) {
                String[] f = fields[i].split("=");
                if (f[1].startsWith("\"")) {
                    f[1] = f[1].substring(1);
                }
                if (f[1].charAt(f[1].length() - 1) == '\"') {
                    f[1] = f[1].substring(0, f[1].length() - 1);
                }
                parts.put(f[0].toLowerCase().trim(), f[1].trim());
            }
        }
    }
    
    public String getType() { return type; }
    
    public String getField(String f) {
        if (parts == null) {
            return null;
        }
        return parts.get(f.toLowerCase());
    }
}
