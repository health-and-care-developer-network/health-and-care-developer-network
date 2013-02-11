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
package org.warlock.tk.experimental.distributor.SpineInjector;
import java.util.StringTokenizer;
/**
 * THIS PACKAGE IS NOT TO BE USED. Development of a Spine message for transmitting
 * ITK requests is underway, but will NOT use the "forward reliable" message used here.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class DirectoryEntry {
    private String asid = null;
    private String cpaid = null;
    private String partykey = null;

    public DirectoryEntry(String line)
        throws Exception
    {
        if ((line == null) || (line.trim().length() == 0)) {
            throw new Exception("Empty line in directory entry");
        }
        StringTokenizer st = new StringTokenizer(line, "\t");
        if (!st.hasMoreTokens()) {
            throw new Exception("Malformed line in directory entry: " + line);
        }
        asid = st.nextToken();
        if (!st.hasMoreTokens()) {
            throw new Exception("Malformed line in directory entry: " + line);
        }
        cpaid = st.nextToken();
        if (!st.hasMoreTokens()) {
            throw new Exception("Malformed line in directory entry: " + line);
        }
        partykey = st.nextToken();
    }

    public String getASID() { return asid; }
    public String getCPAId() { return cpaid; }
    public String getPartyKey() { return partykey; }
}
