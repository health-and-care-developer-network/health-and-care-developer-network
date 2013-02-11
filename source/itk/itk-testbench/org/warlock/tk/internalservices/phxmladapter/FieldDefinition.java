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
/**
 * Definition of the make-up of an HL7v2 field.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 

import java.util.ArrayList;
import java.util.Iterator;
public class FieldDefinition {
    private ArrayList<Part>parts = new ArrayList<Part>();
    private String fieldName = null;
    private String type = null;
    private String variableFieldType = null;
    private String namespace = null;

    private static final String MULTIPART = " CE CD ED XAD XON ";
    private boolean isMultipart = false;

    public FieldDefinition(String n, String t) {
        fieldName = n;
        type = t;
    }

    void setNamespace(String s) {
        if ((s != null) && (s.trim().length() > 0)) {
            namespace = s;
        } else {
            namespace = null;
        }
    }

    void setVariableFieldType(String s) {
        variableFieldType = s;
        isMultipart = MULTIPART.contains(s);
    }

    public String getName() { return fieldName; }
    public String getType() { return type; }
    public String getNamespace() { return namespace; }
    
    public Iterator<Part> getParts() { return parts.iterator(); }
    public boolean isMultiPart() { return isMultipart; }

    public void addPart(String p, String t) { parts.add(new Part(p, t)); }

    public void addPart(String p, String t, String n) { parts.add(new Part(p, t, n)); }
    
    public int fieldCount() { return parts.size(); }

    public Part getPart(int i) {
        try {
            return parts.get(i);
        }
        catch (Exception e) {
            return null;
        }
    }

    public String getPartName(int i) {
        try {
            Part p = parts.get(i);
            if (variableFieldType != null) {
                if (isMultipart) {
                    return new String(variableFieldType + "." + Integer.toString(i + 1));
                }
                return null;
            }
            return p.getName();
        }
        catch (Exception e) {
            return null;
        }
    }

}
