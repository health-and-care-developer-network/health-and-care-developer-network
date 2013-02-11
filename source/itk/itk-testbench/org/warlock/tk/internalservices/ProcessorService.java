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
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.HashMap;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.boot.ToolkitSimulator;
import org.warlock.tk.internalservices.process.ProcessData;
import org.warlock.tk.internalservices.process.RequestProcessor;
/**
 * Service providing access to message processors defined via the TKW properties
 * file, and the processors configuration file. Processors implement the
 * org.warlock.tk.internalservices.process.RequestProcessor interface.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class ProcessorService
    implements org.warlock.tk.boot.ToolkitService
{
    private static final String CONFIGFILE = "tks.processors.configurationfile";
    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;
    private HashMap<String,RequestProcessor>processors = null;

    public ProcessorService(){}

    @Override
    public Properties getBootProperties() { return bootProperties; }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception
    {
        bootProperties = p;
        serviceName = s;
        simulator = t;
        processors = new HashMap<String,RequestProcessor>();
        String cfgfile = p.getProperty(CONFIGFILE);
        if (cfgfile == null) {
            throw new Exception("Property " + CONFIGFILE + " not set");
        }
        FileReader fr = new FileReader(cfgfile);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("#") && (line.trim().length() > 0)) {
                StringTokenizer st = new StringTokenizer(line);
                String soapaction = st.nextToken();
                String classname = st.nextToken();
                RequestProcessor rp = (RequestProcessor)Class.forName(classname).newInstance();
                rp.setSimulator(simulator);
                processors.put(soapaction, rp);
            }
        }
        fr.close();
    }

    /**
     * Lookup a process for the request SOAPaction, and execute it.
     * 
     * @param org.warlock.tk.internalservices.process.ProcessData instance
     * @return ServiceResponse output from the call to RequestProcessor.process()
     * @throws Exception Exception from RequestProcessor.process() or if no process is defined for the SOAPAction
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception
    {
        ProcessData p = (ProcessData)param;
        RequestProcessor rp = processors.get(p.getAction());
        if (rp == null) {
            throw new Exception("No process defined for soapaction " + p.getAction());
        }
        ServiceResponse s = rp.process(p);
        s.setProcessorFault(p.getFaultResponse());
        return s;
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
    public ServiceResponse execute(String type, String param)
            throws Exception
    {
        return new ServiceResponse(0, "Toolkit simulator processor service");
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
        return execute(param);
    }

}
