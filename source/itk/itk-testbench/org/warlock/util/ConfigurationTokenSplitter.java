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
package org.warlock.util;
import java.util.ArrayList;
/**
 * Convenience class wrapping ConfigurationStringTokeniser to return the
 * tokens as a string array.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class ConfigurationTokenSplitter {
    
    private ConfigurationStringTokeniser tokeniser = null;
    
    public ConfigurationTokenSplitter(String line) {
        tokeniser = new ConfigurationStringTokeniser(line);
    }
    
    public String[] split() 
            throws Exception
    {
        ArrayList<String> list = new ArrayList<String>();
        while (tokeniser.hasMoreTokens()) {
            String s = tokeniser.nextToken();
            if (s.endsWith("\"")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.startsWith("\"")) {
                s = s.substring(1);
            }
            list.add(s);
        }
        String[] a = new String[list.size()];
        a = list.toArray(a);
        return a;
    }
}
