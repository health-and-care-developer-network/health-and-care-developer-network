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
import java.io.InputStream;
 /**
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public interface PassFailCheck 
    extends Linkable
{
    public void init(String[] line) throws Exception;
    public String getName();
    public String getDescription();
    /**
     * Implement to provide the check on a response message.
     * @param s Input stream (e.g. from network or file) from which to read the message
     * @return True if passed
     * @throws Exception if something goes wrong
     */
    public boolean passed(Script s, InputStream in) throws Exception;     
   
}
