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
package org.warlock.tk.internalservices.testautomation;
import java.io.FileReader;
import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.safehaus.uuid.UUIDGenerator;
 /**
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */

class Template {
    
    private String name = null;
    private String filename = null;
    private String template = null;

    private static SimpleDateFormat HL7FORMATDATE = new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    Template(String[] line)
            throws Exception
    {
        name = line[0];
        filename = line[1];
        load();
    }

    String getName() { return name; }
    
    private void load() 
        throws Exception
    {
        BufferedReader b = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = b.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        b.close();
        template = sb.toString();
    }
                
   String makeMessage(String recordid, DataSource d)
            throws Exception
    {
        if (d == null) {
            return template;
        }
        String id = null;
        StringBuilder sb = new StringBuilder(template);
        if ((recordid == null) || recordid.contentEquals("NEXT")) {
            id = d.getNextId();
            if (id == null) {
                throw new Exception("Data source " + d.getName() + " has no next record");
            }
        } else {
            id = recordid;
        }
        for (String s : d.getTags()) {
            String v = resolveDataValue(d.getValue(id, s));
            substitute(sb, s, v);
        }
        return sb.toString();
    }
    
    static void substitute(StringBuilder sb, String tag, String value)
            throws Exception
    {
        int tagPoint = -1;
        int tagLength = tag.length();
        while ((tagPoint = sb.indexOf(tag)) != -1 ) {
            sb.replace(tagPoint, tagPoint + tagLength, value);
        }
    }
    
    static String resolveDataValue(String v) 
            throws Exception
    {
        if (v == null)
            return v;
        if (!v.startsWith("__")) {
            return v;
        }
        if (v.contentEquals("__HL7_DATE__")) {
            return HL7FORMATDATE.format(new Date());
        }
        if (v.contentEquals("__ISO_8601_DATE__")) {
            return ISO8601FORMATDATE.format(new Date());
        }
        if (v.contentEquals("__UCASE_UUID__")) {
            return UUIDGenerator.getInstance().generateRandomBasedUUID().toString().toUpperCase();
        }
        if (v.contentEquals("__LCASE_UUID__")) {
            return UUIDGenerator.getInstance().generateRandomBasedUUID().toString().toLowerCase();
        }
        if (v.startsWith("__SYSTEM_PROPERTY__:")) {
            String p = v.substring(v.indexOf(":"));
            String q = System.getProperty(p);
            return (q == null) ? "" : q;
        }
        return "";
    }
}
