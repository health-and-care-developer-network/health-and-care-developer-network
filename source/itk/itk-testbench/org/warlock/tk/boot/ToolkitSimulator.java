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
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.io.FileReader;
import org.warlock.util.Logger;

/**
 * Main class.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class ToolkitSimulator
{
    private static final int OP_NONE = 0;
    private static final int OP_TRANSMIT = 1;
    private static final int OP_VALIDATE = 2;
    private static final int OP_SIMULATOR = 3;
    private static final int OP_SPINEVALIDATE = 4;
    private static final int OP_AUTOTEST = 5;

    public static final String VERSION = "NHS CfH Interoperability Toolkit Testbench v2.0.0-Open";
    private static final String USAGE = "java -jar tkw.jar [-version] -transmit|-validate|-simulator|-spinevalidate propertiesfile";

    private static final String TRANSMITSERVICES  = "Sender DeliveryResolver";
    private static final String AUTOTESTSERVICES  = "Toolkit AutoTest RulesEngine Processor Sender DeliveryResolver Validator";
    private static final String TXDOSIGN = "tks.transmitter.includesigner";

    private static final String VALIDATIONSERVICES = "Validator";
    private static final String SPINEVALIDATIONSERVICES = "SpineValidator";

    private static final String SERVICES = "tks.servicenames";
    private static final String TKSNAME = "tks.configname";
    private static final String CLASSROOT = "tks.classname.";
    private static final String TKSORG = "tks.username";
    private static final String DONTSIGNLOGS = "tks.skipsignlogs";

    private Properties properties = null;
    private HashMap<String, ToolkitService>services = null;
    private int operation = OP_NONE;
    private String configurationName = null;
    private String organisationName = null;

    public ToolkitSimulator(String propertiesfile, int op)
        throws Exception
    {
        operation = op;
        System.setProperty("javax.xml.xpath.XPathFactory", "net.sf.saxon.xpath.XPathFactoryImpl");
        properties = new Properties();
        FileReader f = new FileReader(propertiesfile);
        properties.load(f);
        f.close();
        // Copy any properties needed for the HTTP server's SSL listener.
        if (properties.getProperty(org.warlock.http.SSLSocketListener.USESSLCONTEXT) != null) {
            System.setProperty(org.warlock.http.SSLSocketListener.USESSLCONTEXT, properties.getProperty(org.warlock.http.SSLSocketListener.USESSLCONTEXT));
            System.setProperty(org.warlock.http.SSLSocketListener.SSLPASS, properties.getProperty(org.warlock.http.SSLSocketListener.SSLPASS));
            if (properties.getProperty(org.warlock.http.SSLSocketListener.SSLALGORITHM) != null) {
                System.setProperty(org.warlock.http.SSLSocketListener.SSLALGORITHM, properties.getProperty(org.warlock.http.SSLSocketListener.SSLALGORITHM));
            }
        }
        if (properties.getProperty(DONTSIGNLOGS) != null) {
            System.setProperty(DONTSIGNLOGS, properties.getProperty(DONTSIGNLOGS));
        }
        Logger.getInstance().setAppName("TKS", properties.getProperty("tks.logdir"));
        if (properties.getProperty(TKSNAME) != null) {
            configurationName = properties.getProperty(TKSNAME);
        } else {
            configurationName = "Not given";
        }
        if (properties.getProperty(TKSORG) != null) {
            organisationName = properties.getProperty(TKSORG);
        } else {
            organisationName = "Not given";
        }

    }

    public String getConfigurationName() { return configurationName; }
    public String getVersion() { return VERSION; }
    public String getOrganisationName() { return organisationName; }

    public void initValidate()
            throws Exception
    {
        properties.setProperty(SERVICES, VALIDATIONSERVICES);
        boot();
    }

    public void initSpineValidate()
            throws Exception
    {
        properties.setProperty(SERVICES, SPINEVALIDATIONSERVICES);
        boot();
    }

    public void initAutoTest()
            throws Exception
    {
        String s = AUTOTESTSERVICES;
        String dosign = properties.getProperty(TXDOSIGN);
        if ((dosign != null) && (dosign.toUpperCase().startsWith("Y"))) {
            s = s + " Signer";
            Logger.getInstance().log("Property " + TXDOSIGN + " set - doing signing");
        }
        s = s + " Transmitter";
        properties.setProperty(SERVICES, s);
        boot();        
    }

    public void initTransmit()
            throws Exception
    {
        String s = TRANSMITSERVICES;
        String dosign = properties.getProperty(TXDOSIGN);
        if ((dosign != null) && (dosign.toUpperCase().startsWith("Y"))) {
            s = s + " Signer";
            Logger.getInstance().log("Property " + TXDOSIGN + " set - doing signing");
        }
        s = s + " Transmitter";
        properties.setProperty(SERVICES, s);
        boot();
    }

    public void boot()
        throws Exception
    {
        // 1. Read tks.services to get a list of the services to call
        // 2. For each, look up tks.servicename.class to find the class, and load it
        // 3. Once loaded, call "boot" and pass properties to it

        String sn = properties.getProperty(SERVICES);
        if (sn == null) {
            System.err.println("Error: property " + SERVICES + " not defined");
            return;
        }
        String tksname = properties.getProperty(TKSNAME);
        if (tksname == null) {
            tksname = "Not defined";
        }
        System.out.println(Logger.getDate() + " booting " + tksname);
        Logger.getInstance().log("Boot", tksname);
        services = new HashMap<String,ToolkitService>();
        StringTokenizer st = new StringTokenizer(sn);
        while (st.hasMoreElements()) {
            String t = st.nextToken();
            startService(t);
        }
        System.out.println("ITK Testbench ready");
        Logger.getInstance().log("ITK Testbench ready");
    }

    public Set<String> getServiceNames() { return services.keySet(); }
    public ToolkitService getService(String sname) { return services.get(sname); }

    private void startService(String sname)
        throws Exception
    {
        String serviceClass = properties.getProperty(CLASSROOT + sname);
        if (serviceClass == null) {
            throw new Exception("Class name not given for service " + sname + " : no property: " + CLASSROOT + sname);
        }
        try {
            ToolkitService t = ((ToolkitService)Class.forName(serviceClass).newInstance());
            t.boot(this, properties, sname);
            services.put(sname, t);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to initialise class " + serviceClass + " : " + e.toString());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean showVersion = false;
        int op = OP_NONE;
        String pfile = null;
        String testscript = null;

        for (String s : args) {
            // TODO: This is getting cumbersome - fix
            //
            if (s.compareTo("-version") == 0) {
                showVersion = true;
            } else {
                if (s.compareTo("-transmit") == 0) {
                    op = OP_TRANSMIT;
                } else {
                    if (s.compareTo("-validate") == 0) {
                        op = OP_VALIDATE;
                    } else {
                        if (s.compareTo("-simulator") == 0) {
                                op = OP_SIMULATOR;
                        } else {
                            if (s.compareTo("-spinevalidate") == 0) {
                                op = OP_SPINEVALIDATE;
                            } else {
                                if (s.compareTo("-autotest") == 0) {
                                    op = OP_AUTOTEST;
                                } else {
                                    if (pfile == null) {
                                        pfile = s;
                                    } else {
                                        testscript = s;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (pfile == null) {
            if (showVersion) {
                System.out.println(VERSION);
            } else {
                System.err.println(USAGE);
            }
            System.exit(1);
        }
        if (showVersion) {
            System.out.println(VERSION);
        }
        ToolkitSimulator t = null;
        try {
            t = new ToolkitSimulator(pfile, op);
        }
        catch (Exception e) {
            System.err.println("Failed to load properties file " + pfile + " : " + e.getMessage());
            System.exit(1);
        }
        try {
            ToolkitService tks = null;
            switch (op) {
                case OP_TRANSMIT:
                    t.initTransmit();
                    tks = t.getService("Transmitter");
                    tks.execute(null);
                    break;
                case OP_VALIDATE:
                    t.initValidate();;
                    tks = t.getService("Validator");
                    tks.execute(null);
                    break;
                case OP_SIMULATOR:
                    t.boot();
                    break;
                case OP_SPINEVALIDATE:
                    t.initSpineValidate();;
                    tks = t.getService("SpineValidator");
                    tks.execute(null);
                    break;
                case OP_AUTOTEST:
                    t.initAutoTest();
                    tks = t.getService("AutoTest");
                    tks.execute(testscript);
                    break;
                default:
                    throw new Exception("Unknown mode");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Toolkit boot error: " + e.getMessage());
        }
    }


}
