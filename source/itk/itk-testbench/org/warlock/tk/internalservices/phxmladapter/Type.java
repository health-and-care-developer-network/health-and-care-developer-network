
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
package org.warlock.tk.internalservices.phxmladapter;
import java.util.ArrayList;
import java.util.Iterator;
 /**
 * HL7v2 data type 
 * @author Damian Murphy <murff@warlock.org>
 */ 
class Type {
    private ArrayList<String> parts = null;
    private String name = null;

    Type(String n) { name = n; }
    void addPart(String t) {
        if (parts == null) {
            parts = new ArrayList<String>();
        }
        parts.add(t);
    }
    
    Iterator<String> getParts() {
        if (parts == null)
            return null;
        return parts.iterator();
    }
    
    String getName() { return name; }

    boolean subTyped() { return !(parts == null); }

    int getPartCount() {
        if (parts == null) {
            return 0;
        }
        return parts.size();
    }
    
    String getPartType(int i)
    {
        if (parts == null) { return null; }
        try {
            return parts.get(i);
        }
        catch (Exception e) {
            return null;
        }
    }
}
