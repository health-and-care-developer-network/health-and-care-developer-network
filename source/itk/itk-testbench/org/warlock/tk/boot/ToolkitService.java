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
package org.warlock.tk.boot;
import java.util.Properties;
/**
 * Interface implemented by the TKW internal services.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public interface ToolkitService {
    public void boot(ToolkitSimulator t, Properties p, String s) throws Exception;
    public ServiceResponse execute(Object param) throws Exception;
    public ServiceResponse execute(String type, String param) throws Exception;
    public ServiceResponse execute(String type, Object param) throws Exception;
    public Properties getBootProperties();
}
