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
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Properties;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.boot.ToolkitSimulator;
import org.warlock.util.Logger;
/**
 * Service to provide delivery resolution for asynchronous responses, to determine 
 * whether a "ReplyTo" is intercepted to a queue.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class DeliveryResolverService
    implements org.warlock.tk.boot.ToolkitService
{
    private static final String ROUTINGFILE = "tks.delivery.routingfile";
    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;
    private HashMap<String,String> table = null;

    public DeliveryResolverService() {}

    @Override
    public Properties getBootProperties() { return bootProperties; }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception
    {
        bootProperties = p;
        serviceName = s;
        simulator = t;

        String routingfile = p.getProperty(ROUTINGFILE);
        table = new HashMap<String,String>();
        File f = new File(routingfile);
        if (!f.exists()) {
            Logger.getInstance().log("DeliveryResolverService.boot()", "Routing file " + routingfile + " not found, continuing anyway");
            System.err.println("Warning: DeliveryResolver routing file " + routingfile + " not found");
            return;
        }
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        while ((line = br.readLine()) != null) {
            // Format of the file is tab-delimited, address/act/queuename
            // act may be "ALL", comments start line with "#"
            if (!line.startsWith("#") && (line.trim().length() > 0)) {
                StringTokenizer st = new StringTokenizer(line);
                String addr = st.nextToken();
                String act = st.nextToken();
                String queue = st.nextToken();
                table.put(addr + act, queue);
            }
        }
        fr.close();
    }

    /**
     * No operation.
     * 
     * @param param
     * @return Empty ServiceResponse
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception
    {
        return new ServiceResponse(0, null);
    }

    /**
     * Resolve a queue by name, in param. The queues are defined in the TKW properties
     * files and the queue configuration file.
     * 
     * @param type Not used.
     * @param param Queue name, qualified by "urn:nhs-itk:subscriber:"
     * @return ServiceResponse containing the internally-configured queue name,
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, String param)
            throws Exception
    {
        String queue = null;
        if ((param != null) && (param.startsWith("urn:nhs-itk:subscriber:"))) {
            queue = param.substring(23);
        } else {
            String specificKey = param + type;
            String allKey = param + "ALL";
            queue = table.get(specificKey);
            if (queue == null) {
                queue = table.get(allKey);
            }
        }
        return new ServiceResponse(0, queue);
    }

    /**
     * No operation.
     * 
     * @param type
     * @param param
     * @return Empty ServiceResponse
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, Object param)
            throws Exception
    {
        return new ServiceResponse(0, null);
    }

}
