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
import org.warlock.tk.internalservices.rules.Engine;
import org.w3c.dom.Node;
 /**
 * Service providing access to the TKW simulator rules.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class RuleService
    implements org.warlock.tk.boot.ToolkitService
{
    private static final String RULEFILE = "tks.rules.configuration.file";
    private static final String RULEENGINE = "tks.rules.engineclass";
    private static final String DEFAULTENGINE = "org.warlock.tk.internalservices.rules.DefaultRulesEngine";
    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Engine rulesEngine = null;
    private Properties bootProperties = null;

    public RuleService() {}

    @Override
    public Properties getBootProperties() { return bootProperties; }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception
    {
        bootProperties = p;
        serviceName = s;
        simulator = t;

        String rulefile = p.getProperty(RULEFILE);
        if (rulefile == null) {
            throw new Exception("No rule file defined in property " + RULEFILE);
        }
        String engineClass = p.getProperty(RULEENGINE);
        if (engineClass == null) {
            engineClass = DEFAULTENGINE;
        }
        load(rulefile, engineClass);
    }

    private void load(String f, String c)
            throws Exception
    {
        rulesEngine = (Engine)Class.forName(c).newInstance();
        rulesEngine.load(f);
    }

    /**
     * No operation
     * 
     * @param param
     * @return Empty ServiceResponse
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception
    {
        return new ServiceResponse(0, "Toolkit Simulator Rules Service");
    }

    /**
     * Calls RulesEngine.execute()
     * 
     * @param type SOAPAction of request message
     * @param param String serialisation of request XML
     * @return ServiceResponse output from the rules engine containing HTTP response code and body
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, String param)
            throws Exception
    {
        return rulesEngine.execute(type, param);
    }

    /**
     * Calls RulesEngine.execute()
     * 
     * @param type SOAPAction of request message
     * @param param DOM Node representing the root of the request message
     * @return ServiceResponse output from the rules engine containing HTTP response code and body
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, Object param)
            throws Exception
    {
        Node n = (Node)param;
        return rulesEngine.execute(type, n);

    }

}
