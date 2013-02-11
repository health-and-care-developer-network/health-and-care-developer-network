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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import org.warlock.http.HttpContext;
import org.warlock.http.HttpServer;
import org.warlock.http.Listener;
import org.warlock.http.SocketListener;
import org.warlock.http.SSLSocketListener;
import org.warlock.util.Logger;
/**
 * Core TKW service.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class Toolkit
    implements ToolkitService, 
        org.warlock.tk.internalservices.Reconfigurable, 
        org.warlock.tk.internalservices.Stoppable
{
    private static final String PROOT = "tks.";
    private static final String PORTNAMES = ".namelist";
    private static final String LISTENADDR = ".listenaddr";
    private static final String LISTENPORT  = ".listenport";
    private static final String USETLS = "tks.receivetls";

    private static final String TRUSTSTORE = "tks.tls.truststore";
    private static final String TRUSTPASS = "tks.tls.trustpassword";
    private static final String KEYSTORE = "tks.tls.keystore";
    private static final String KEYPASS = "tks.tls.keystorepassword";

    private static final String NAMEPATH = ".path";
    private static final String NAMECLASS = ".class";
    private static final String NAMETTL = ".ttl";
    private static final int DEFAULTLISTENPORT = 4848;
    private static final int DEFAULTTLSLISTENPORT = 443;

    private static final String DEFAULTASYNCTTLPROPERTY = "tks.Toolkit.default.asyncttl";
    private static final int DEFAULTASYNCTTL = 10;

    private boolean tls = false;
    private ToolkitSimulator simulator = null;
    private String serviceName = null;
    private HttpServer server = null;
    private Properties bootProperties = null;
    private ArrayList<ToolkitHttpHandler> handlers = null;
    
    public Toolkit() {}

    @Override
    public Properties getBootProperties() { return bootProperties; }

    @Override
    public void stop()
            throws Exception
    {
        server.stop();
    }
    
    @Override
    public void reconfigure(Properties p)
            throws Exception
    {
        throw new Exception("Cannot restart from properties in this version");
    }
    
    @Override
    public void reconfigure(String what, String value)
            throws Exception
    {
        if (what.contentEquals("savedMessagesDirectory")) {
            for (ToolkitHttpHandler t : handlers) {
                t.setSavedMessagesDirectory(value);
            }
        } else {
            throw new Exception("Cannot reconfigure " + what);
        }
    }
    
    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception
    {
        bootProperties = p;
        simulator = t;
        serviceName = s;
        String usetls = p.getProperty(USETLS);
        if (usetls != null) {
            if (usetls.toUpperCase().charAt(0) == 'Y') {
                tls = true;
            }
        }
        String listenAddr = p.getProperty(PROOT + serviceName + LISTENADDR);
        int listenPort  = Integer.parseInt(p.getProperty(PROOT + serviceName + LISTENPORT));
        if (listenPort == 0) {
            if (tls) {
                listenPort = DEFAULTTLSLISTENPORT;
            } else {
                listenPort = DEFAULTLISTENPORT;
            }
        }
        server = new HttpServer();

        String opnames = p.getProperty(PROOT + serviceName + PORTNAMES);
        if (opnames == null) {
            throw new Exception("ToolkitHandler booted with no service ports: " + PROOT + serviceName + PORTNAMES + " property not defined");
        }
        startHandlers(p, opnames);
        Listener l = null;
        if (tls) {
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
            l = new SSLSocketListener();
        } else {
            l = new SocketListener();
        }
        l.setHost(listenAddr);
        l.setPort(listenPort);
        server.addListener(l);
        server.start();
        System.out.println("Toolkit service ready");
        Logger.getInstance().log("Toolkit service ready");
    }

    public boolean isUsingTLS() { return tls; }

    public ToolkitService getService(String name) {
        return simulator.getService(name);
    }

    public Set<String> getServiceNames() { return simulator.getServiceNames(); }

    private void startHandlers(Properties p, String n)
            throws Exception
    {
        // Modify this so that it keeps references to the ToolkitHttpHandler instances,
        // if it gets a request for the same one, just make the HttpContext and add the
        // same reference to it
        HashMap<String,ToolkitHttpHandler> cache = new HashMap<String,ToolkitHttpHandler>();
        handlers = new ArrayList<ToolkitHttpHandler>();
        StringTokenizer st = new StringTokenizer(n);
        while (st.hasMoreTokens()) {
            String pname = st.nextToken();
            StringBuilder sbpath = new StringBuilder(PROOT);
            sbpath.append(serviceName);
            sbpath.append(".");
            sbpath.append(pname);
            StringBuilder sbclass = new StringBuilder(sbpath.toString());
            StringBuilder sbttl = new StringBuilder(sbpath.toString());
            sbpath.append(NAMEPATH);
            sbclass.append(NAMECLASS);
            sbttl.append(NAMETTL);
            String path = p.getProperty(sbpath.toString());
            String classname = p.getProperty(sbclass.toString());
            String attl = p.getProperty(sbttl.toString());
            int asyncttl = 0;
            if (attl == null) {
                attl = p.getProperty(DEFAULTASYNCTTLPROPERTY);
                if (attl == null) {
                    asyncttl = DEFAULTASYNCTTL;
                } else {
                    try {
                        asyncttl = Integer.parseInt(attl);
                    }
                    catch (Exception e) {
                        asyncttl = DEFAULTASYNCTTL;
                    }
                }
            } else {
                try {
                    asyncttl = Integer.parseInt(attl);
                }
                catch (Exception e) {
                    asyncttl = DEFAULTASYNCTTL;
                }
            }
            try {
                ToolkitHttpHandler h = null;
                if (cache.containsKey(classname)) {
                    h = cache.get(classname);
                } else {
                    h = (ToolkitHttpHandler)Class.forName(classname).newInstance();
                    h.setToolkit(this);
                }
                h.setAsynchronousTTL(asyncttl);
                HttpContext c = new HttpContext();
                c.addHandler(h);
                handlers.add(h);
                c.setContextPath(path);
                server.addContext(c);
            }
            catch (Exception e) {
                throw new Exception("ToolkitHandler boot failed: Loading handler " + classname + " : " + e.getMessage());
            }
        }

    }

    @Override
    public ServiceResponse execute(Object param)
            throws Exception
    {
        return new ServiceResponse(0, "Toolkit simulator");
    }

    @Override
    public ServiceResponse execute(String type, String param)
            throws Exception
    {
        return new ServiceResponse(0, "Toolkit simulator");
    }

    @Override
    public ServiceResponse execute(String type, Object param)
            throws Exception
    {
        return new ServiceResponse(0, "Toolkit simulator");
    }

}
