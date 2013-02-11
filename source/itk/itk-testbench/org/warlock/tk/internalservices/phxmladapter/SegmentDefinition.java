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
 * Class containing the definition of an HL7v2 message segment.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
 import java.util.ArrayList;
import java.util.Iterator;
public class SegmentDefinition {
    private ArrayList<FieldDefinition> fields = new ArrayList<FieldDefinition>();
    private String segmentName = null;

    // Any segment types known to have "variable" fields, and if so which ones and
    // what field indicates the type ?
    //
    private static final String[] VARIABLES = {"OBX"};
    private static final int[] VARFIELDS = {5};
    private static final int[] VARTYPEFIELDS = {2};

    private boolean hasVariable = false;
    private int varField = -1;
    private int varIndicator = -1;
    private String varType = null;

    public SegmentDefinition(String n) {
        segmentName = n;
        for (int i = 0; i < VARIABLES.length; i++) {
            if (VARIABLES[i].contentEquals(segmentName)) {
                hasVariable = true;
                varField = VARFIELDS[i] - 1;
                varIndicator = VARTYPEFIELDS[i] - 1;
                return;
            }
        }
    }

    public String getName() { return segmentName; }
    public Iterator<FieldDefinition> getFields() { 
        if (!segmentName.contentEquals("MSH")) {
            return fields.iterator(); 
        }
        ArrayList<FieldDefinition> f = new ArrayList<FieldDefinition>();
        for (int i = 1; i < fields.size(); i++) {
            f.add(fields.get(i));
        }
        return f.iterator();
    }
    public void addField(FieldDefinition f) { fields.add(f); }

    public void setVariableFieldType(String s) { varType = s; }
    
    public boolean isVariableIndicatorField(int i) { return (i == varIndicator); }

    public FieldDefinition getField(int i) { 
        try {
            if (hasVariable) {
                if (i == varField) {
                    fields.get(i).setVariableFieldType(varType);
                }
            }
            return fields.get(i);
        }
        catch (Exception e) {
            return null;
        }
    }
}
