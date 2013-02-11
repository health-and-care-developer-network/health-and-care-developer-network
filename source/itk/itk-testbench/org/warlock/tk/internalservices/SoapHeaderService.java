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
package org.warlock.tk.internalservices;
import java.util.Properties;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.boot.ToolkitSimulator;
import org.w3c.dom.Node;
 /**
 * Placeholder service for interrogating SOAP headers.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class SoapHeaderService
    implements org.warlock.tk.boot.ToolkitService
{
    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;

    public SoapHeaderService() {}

    @Override
    public Properties getBootProperties() { return bootProperties; }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception
    {
        bootProperties = p;
        serviceName = s;
        simulator = t;
        System.out.println(serviceName + " started, class: " + this.getClass().getCanonicalName());
    }

    @Override
    public ServiceResponse execute(Object param)
            throws Exception
    {
        // Return some XML containing a SOAP error if we're unhappy about
        // what we get, otherwise return null. As we're not doing any
        // processing on this at the moment, just return null.
        //
        Node hdr = (Node)param;
        return new ServiceResponse(200, null);
    }

    @Override
    public ServiceResponse execute(String type, String param)
            throws Exception
    {
        return new ServiceResponse(0, "Toolkit simulator SOAP header service");
    }

    @Override
    public ServiceResponse execute(String type, Object param)
            throws Exception
    {
        return execute(param);
    }

}
