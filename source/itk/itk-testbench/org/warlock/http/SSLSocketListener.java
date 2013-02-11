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
package org.warlock.http;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.io.FileInputStream;
/**
 * Implementation of Listener which listens for SSL connections.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class SSLSocketListener
        extends Thread
        implements Listener
{

    private HttpServer server = null;
    private int port;
    private String host;
    private String localId;
    private Exception exception;
    private boolean keepGoing;

    private static boolean notUsingSslContext = false;
    
    private SSLContext sslContext = null;
    private ServerSocket serverSocket = null;
    
    /**
     * Property name for the path to the keystore file.
     */
    public static final String USESSLCONTEXT = "org.warlock.http.usesslcontext";
    /**
     * Property name for the keystore password.
     */
    public static final String SSLPASS = "org.warlock.http.sslcontextpass";
    /**
     * Property name only required if the platform default algorithm is not an X509 implementation.
     */
    public static final String SSLALGORITHM = "org.warlock.http.sslalgorithm";
    
    /**
     * By default listen on port 443
     */
    private static final int DEFAULTPORT = 443;
    /**
     * By default listen on localhost only.
     */
    private static final String DEFAULTHOST = "localhost";
    /** Creates a new instance of SocketListener */
    public SSLSocketListener() 
            throws Exception
    {
        port = DEFAULTPORT;
        host = DEFAULTHOST;
        exception = null;
        keepGoing = true;
        localId = host + ":" + Integer.toString(port);
        initSSLContext();
    }

    private void initSSLContext() 
            throws Exception
    {
        if (!notUsingSslContext && (sslContext == null)) {
            String ksf = System.getProperty(USESSLCONTEXT);
            if (ksf == null) {
                notUsingSslContext = true;
                return;
            }
            String p = System.getProperty(SSLPASS);
            if (p == null) p = "";
            String alg = System.getProperty(SSLALGORITHM);
            KeyManagerFactory kmf = null;
            if (alg == null) {
                kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            } else {
                kmf = KeyManagerFactory.getInstance(alg);
            }
            KeyStore ks = KeyStore.getInstance("jks");
            FileInputStream fis = new FileInputStream(ksf);
            ks.load(fis, p.toCharArray());
            kmf.init(ks, p.toCharArray());
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());
        }
    }
    
    @Override
    public void setPort(int p)  {
        if ((p > 0) && (p < 65535)) {
            port = p;
        }
        localId = host + ":" + Integer.toString(port);
    }

    @Override
    public void setHost(String h) {
        if ((h != null) && (h.trim().length() > 0)) {
            host = h.trim();
        }
        localId = host + ":" + Integer.toString(port);
    }

    @Override
    public void stopListening() 
    { 
        keepGoing = false; 
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }            
        }
        catch (Exception e) {
            System.err.println("Exception closing listener socket: " + e.toString());
        }
    }

    @Override
    public void startListening(HttpServer s)
        throws HttpServerException
    {
        server = s;
        start();
    }

    Exception getException() { return exception; }

    @Override
    public void run()
    {
        try {
            ServerSocketFactory ssf = null;
            if (sslContext == null) {
                ssf = SSLServerSocketFactory.getDefault();
            } else {
                ssf = sslContext.getServerSocketFactory();
            }
            serverSocket = null;
            if (host.contentEquals("0.0.0.0")) {
                serverSocket = ssf.createServerSocket(port, 10, (InetAddress)null);
            } else {
                serverSocket = ssf.createServerSocket(port, 10, InetAddress.getByName(host));
            }
            System.out.println("Listening on " + host + ":" + port);
            while(keepGoing) {
                Socket requestSocket = serverSocket.accept();
                if (keepGoing) {
                    StringBuilder sb = new StringBuilder(localId);
                    sb.append(" from ");
                    sb.append(requestSocket.getInetAddress().getHostAddress());
                    new RequestReader(requestSocket, server, sb.toString());
                }
            }
        }
        catch (java.net.SocketException se) {}
        catch (Exception e) {
            e.printStackTrace();
            exception = e;
            System.err.println("Exception in SocketListener " + host + ":" + port + ", listener exitting: " + e.getMessage());
        }

    }
}

