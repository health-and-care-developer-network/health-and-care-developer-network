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
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
/**
 * Implementation of the listener interface to listen for clear-text connections.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class SocketListener 
    extends Thread
    implements Listener
{
    
    private HttpServer server = null;
    private ServerSocket serverSocket = null;
    private int port;
    private String host;
    private String localId;
    private Exception exception;
    private boolean keepGoing;
    
    private static final int DEFAULTPORT = 80;
    private static final String DEFAULTHOST = "localhost";
    /** Creates a new instance of SocketListener */
    public SocketListener() {
        port = DEFAULTPORT;
        host = DEFAULTHOST;
        exception = null;
        keepGoing = true;
        localId = host + ":" + Integer.toString(port);
    }

    /**
     * Set the port for listening if not the default 80
     * @param p 
     */
    @Override
    public void setPort(int p)  {
        if ((p > 0) && (p < 65535)) {
            port = p;
        }
        localId = host + ":" + Integer.toString(port);
    }

    /**
     * Set the host to listen on if not the default localhost. May be 0.0.0.0
     * to listen on all available network interfaces.
     * @param h 
     */
    @Override
    public void setHost(String h) {
        if ((h != null) && (h.trim().length() > 0)) {
            host = h.trim();
        }
        localId = host + ":" + Integer.toString(port);
    }

    @Override
    public void stopListening() { 
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
            serverSocket = new ServerSocket();
            if (host.contentEquals("0.0.0.0.")) {
                serverSocket.bind(new InetSocketAddress((java.net.Inet4Address)null, port));
            } else {
                serverSocket.bind(new InetSocketAddress(host, port));
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
            exception = e;
            System.err.println("Exception in SocketListener " + host + ":" + port + ", listener exitting: " + e.getMessage());
        }
        
    }
}
