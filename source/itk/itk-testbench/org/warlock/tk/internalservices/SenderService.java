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
import org.warlock.tk.internalservices.send.Sender;
import java.io.File;
import org.warlock.tk.internalservices.send.SenderRequest;
import org.warlock.util.Logger;
 /**
 * Service which acts as a launcher for org.warlock.tk.internalservices.send.Sender 
 * threads to deliver messages either to queues, or via SOAP web service calls.
 * Used for asynchronous responses, and for the TKW transmitter function.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 

public class SenderService
    implements org.warlock.tk.boot.ToolkitService, Reconfigurable
{
    private static final String USETLS = "tks.sendtls";
    private static final String TRANSMITLOG = "tks.sender.destination";

    private static final String TRUSTSTORE = "tks.tls.truststore";
    private static final String TRUSTPASS = "tks.tls.trustpassword";
    private static final String KEYSTORE = "tks.tls.keystore";
    private static final String KEYPASS = "tks.tls.keystorepassword";

    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;
    private boolean useTls = false;
    private String destinationDirectory = null;

    public SenderService() {}

    @Override
    public Properties getBootProperties() { return bootProperties; }

    @Override
    public void reconfigure(Properties p)
            throws Exception
    {
        boot(simulator, p, serviceName);
    }
    
    @Override
    public void reconfigure(String what, String value)
            throws Exception
    {
        if (what.contentEquals("destinationDirectory")) {
            destinationDirectory = value;
        } else {
            throw new Exception("Configuration not supported");
        }
    }
    
    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception
    {
        bootProperties = p;
        serviceName = s;
        simulator = t;
        String tls = p.getProperty(USETLS);
        if ((tls != null) && (tls.toUpperCase().startsWith("Y"))) {
            useTls = true;
        }
        destinationDirectory = bootProperties.getProperty(TRANSMITLOG);
        if ((destinationDirectory == null) || (destinationDirectory.trim().length() == 0)) {
            throw new Exception("Sender: null or empty destination directory " + TRANSMITLOG);
        }
        File f = new File(destinationDirectory);
        if (!f.canWrite()) {
            throw new Exception("Sender: Unable to write to destination directory " + destinationDirectory);
        }
        if (useTls) {
            // Set up the JSSE system properties with the values from our properties file
            // TRUSTSTORE javax.net.ssl.trustStore
            if (p.getProperty(TRUSTSTORE) == null) {
                Logger.getInstance().log("Warning: Property " + TRUSTSTORE + " not set, TLS initialisation may fail");
                System.err.println("Warning: Property " + TRUSTSTORE + " not set, TLS initialisation may fail");
            } else {
                System.setProperty("javax.net.ssl.trustStore", p.getProperty(TRUSTSTORE));
            }
            // TRUSTPASS javax.net.ssl.trustStorePassword
            if (p.getProperty(TRUSTPASS) == null) {
                Logger.getInstance().log("Warning: Property " + TRUSTPASS + " not set, TLS initialisation may fail");
                System.err.println("Warning: Property " + TRUSTPASS + " not set, TLS initialisation may fail");
            } else {
                System.setProperty("javax.net.ssl.trustStorePassword", p.getProperty(TRUSTPASS));
            }
            // KEYSTORE javax.net.ssl.keyStore
            if (p.getProperty(KEYSTORE) == null) {
                Logger.getInstance().log("Warning: Property " + KEYSTORE + " not set, TLS initialisation may fail");
                System.err.println("Warning: Property " + KEYSTORE + " not set, TLS initialisation may fail");
            } else {
                System.setProperty("javax.net.ssl.keyStore", p.getProperty(KEYSTORE));
            }
            // KEYPASS javax.net.ssl.keyStorePassword
            if (p.getProperty(KEYPASS) == null) {
                Logger.getInstance().log("Warning: Property " + KEYPASS + " not set, TLS initialisation may fail");
                System.err.println("Warning: Property " + KEYPASS + " not set, TLS initialisation may fail");
            } else {
                System.setProperty("javax.net.ssl.keyStorePassword", p.getProperty(KEYPASS));
            }
        }
    }

    /**
     * Send a message.
     * 
     * @param param org.warlock.tk.internalservices.send.SenderRequest instance containing message to be sent
     * @return ServiceResponse with zero response code on success.
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception
    {
        SenderRequest sr = (SenderRequest)param;
        new Sender(simulator, sr, useTls, destinationDirectory);
        return new ServiceResponse(0, "Toolkit simulator sender service");        
    }

    /**
     * No operation.
     * 
     * @param type
     * @param param
     * @return Empty ServiceResponse with a 200 response code
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, String param)
            throws Exception
    {
        return new ServiceResponse(200, "");
    }

    /**
     * No operation.
     * 
     * @param type
     * @param param
     * @return Empty ServiceResponse with a 200 response code
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, Object param)
            throws Exception
    {
        return new ServiceResponse(200, "");
    }

}
