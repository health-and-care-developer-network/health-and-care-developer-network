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
import org.warlock.util.Logger;
import org.warlock.tk.internalservices.testautomation.ScriptParser;
import org.warlock.tk.internalservices.testautomation.Script;

/**
 * INCOMPLETE. Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class AutoTestService 
    implements org.warlock.tk.boot.ToolkitService
{
    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;

    @Override
    public Properties getBootProperties() { return bootProperties; }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception
    {
        bootProperties = p;
        serviceName = s;
        simulator = t;
        if (bootProperties.getProperty("tks.signer.keystore") != null) {
            System.setProperty("tks.internal.autotest.dosigntureheader", "true");
        }
        if (bootProperties.getProperty("tks.Toolkit.default.asyncttl") != null) {
            System.setProperty("tks.Toolkit.default.asyncttl", bootProperties.getProperty("tks.Toolkit.default.asyncttl"));
        }
        
    }
    
    @Override
    public ServiceResponse execute(Object param)
            throws Exception
    {
        String testscript = (String)param;
        ScriptParser p = new ScriptParser(bootProperties);
        Script script = p.parse(testscript);
        script.execute(simulator);
        return null;
    }

    @Override
    public ServiceResponse execute(String type, String param)
            throws Exception
    {
        return new ServiceResponse(200, "");
    }

    @Override
    public ServiceResponse execute(String type, Object param)
            throws Exception
    {
        return new ServiceResponse(200, "");
    }
    
}
