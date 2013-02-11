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
package org.warlock.tk.internalservices.process;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.boot.ToolkitSimulator;

/**
 *
 * @author DAMU2
 */
public class EmptyOK 
    implements RequestProcessor
{
    @Override
    public ServiceResponse process(ProcessData p)
            throws Exception
    {
        return new ServiceResponse(-1, "");
    }

    @Override
    public void setSimulator(ToolkitSimulator t) {}
    
}
