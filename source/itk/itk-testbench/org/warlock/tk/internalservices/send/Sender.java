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
package org.warlock.tk.internalservices.send;
import org.warlock.util.Logger;
import java.net.Socket;
import java.net.URL;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.warlock.tk.boot.ToolkitSimulator;
import org.warlock.tk.boot.ToolkitService;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.internalservices.queue.QueueItem;
import org.warlock.util.CfHNamespaceContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileWriter;
import org.warlock.itklogverifier.LogVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.io.FileInputStream;

/**
 * Thread for transmitting SOAP messages.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class Sender
    extends java.lang.Thread
{
    private static final String SOAPACTION = "/SOAP:Envelope/SOAP:Header/wsa:Action";
    private static final String MESSAGEID = "/SOAP:Envelope/SOAP:Header/wsa:MessageID";
    private static SimpleDateFormat LOGFILEDATE = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
    private static final String PAYLOAD = "__PAYLOAD_BODY__";
    private static final String NOORIGINATE = "tks.system.internal.donotoriginate";

    private static boolean notUsingSslContext = false;
    
    private static SSLContext sslContext = null;

    
    private String action = null;
    private String messageID = null;
    private String  message = null;
    private String  address = null;
    private String wrapper = null;
    private ToolkitSimulator simulator = null;
    private boolean tls = false;
    private int chunkSize = 0;
    private String logDirectory = null;
    private SenderRequest req = null;

    public Sender(ToolkitSimulator tk, SenderRequest what, boolean t, String logdir)
    {
        simulator = tk;
        req = what;
        message = what.getPayload();
        address = what.getAddress();
        wrapper = what.getWrapperTemplate();
        logDirectory = logdir;
        action = what.getAction();

        chunkSize = what.chunkSize();
        tls = t;
        if (tls) {
            try {
                initSSLContext();
            }
            catch (Exception e) {
                System.err.println("Error initialising SSL context for sending: " + e.toString());
            }
        }
        start();
    }

     private void initSSLContext() 
            throws Exception
    {
        if (!notUsingSslContext && (sslContext == null)) {
            String ksf = System.getProperty(org.warlock.http.SSLSocketListener.USESSLCONTEXT);
            if (ksf == null) {
                notUsingSslContext = true;
                return;
            }
            String p = System.getProperty(org.warlock.http.SSLSocketListener.SSLPASS);
            if (p == null) p = "";
            String alg = System.getProperty(org.warlock.http.SSLSocketListener.SSLALGORITHM);
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
    public void run()
    {
        Logger l = Logger.getInstance();
        l.log("Sender", "Sending to " + address);
        if ((message == null) || (message.length() == 0)) {
            l.log("Sender", "Asked to send null or zero-length message");
        }
        if ((address == null) || (address.trim().length() == 0) || (address.contentEquals("http://www.w3.org/2005/08/addressing/none"))) {
            if (address == null) {
                l.log("Sender", "Null address");
            } else {
                l.log("Sender", "Undeliverable address: " + address);
            }
            return;
        }

        // See if we're delivering to a queue for collection

        // We do this here because if we're sending to a queue, the address is
        // a URI which may not be a URL. If we're expected actually to send
        // then the address must be a URL.
        //
        ToolkitService tk = simulator.getService("DeliveryResolver");
        if (tk != null) {
            try {
                ServiceResponse r = tk.execute(action, address);
                if (r.getResponse() != null) {
                    sendToCollect(r.getResponse(), l);
                    return;
                }
            }
            catch (Exception e) {
                l.log("Sender", "Error getting delivery means for address " + address + " soapaction " + action + " : " + e.getMessage());
                return;
            }
        }

        URL u = null;
        try {
            u = new URL(address);
        }
        catch (Exception e) {
            l.log("Sender", "Cannot parse address URL " + address + " : " + e.getMessage());
            return;
        }
        if (wrapper != null) {
            try {
                StringBuilder sb = new StringBuilder(wrapper);
                substitute(sb, PAYLOAD, message);
                message = sb.toString();
            }
            catch (Exception e) {
                l.log("Sender", "Error trying to wrap: " + e.getMessage());
                return;
            }
        }
        try {
            if (action == null) {
                XPath x = XPathFactory.newInstance().newXPath();
                x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
                XPathExpression actionXpath = x.compile(SOAPACTION);
                InputSource is = new InputSource(new StringReader(message));
                action = actionXpath.evaluate(is);
            }
        }
        catch (Exception e) {
            l.log("Sender", "Failed to get SOAPaction: " + e.getMessage());
        }
        try {
            sendDirect(u, l);
        }
        catch (Exception e) {
            l.log("Sender", "Error trying to send: " + e.getMessage());
        }
    }
    private boolean substitute(StringBuilder sb, String tag, String content)
            throws Exception
    {
        boolean doneAnything = false;
        int tagPoint = -1;
        int tagLength = tag.length();
        while ((tagPoint = sb.indexOf(tag)) != -1 ) {
            sb.replace(tagPoint, tagPoint + tagLength, content);
            doneAnything = true;
        }
        return doneAnything;
    }

    private void sendToCollect(String qname, Logger l) {

        try {
            ToolkitService svc = simulator.getService("QueueManager");
            if (svc == null) {
                throw new Exception("QueueManager not available");
            }
            QueueItem item = new QueueItem(qname, message, address, action);
            item.setRefToMessageId(req.getRelatesTo());
            svc.execute(item);
        }
        catch (Exception e) {
            l.log("Sender-queue", "Error queueing message for " + address + " : " + e.getMessage());
        }
    }

    private void sendDirect(URL u, Logger l)
        throws Exception
    {
// extract the MessageID to make the logfile unique
        try {
             XPath x = XPathFactory.newInstance().newXPath();
             x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
             XPathExpression actionXpath = x.compile(MESSAGEID);
             InputSource is = new InputSource(new StringReader(message));
             messageID = actionXpath.evaluate(is);
        }
        catch (Exception e) {
                l.log("Sender", "Failed to get MessageID: " + e.getMessage());
        }

        String header = null;
        if (tls && !u.getProtocol().equalsIgnoreCase("https")) {
            l.log("Sender-direct", "Mismatch: TLS requested but address URL " + address + " does not agree - continuing");
        }
        StringBuilder fnb = new StringBuilder(logDirectory);
        fnb.append(System.getProperty("file.separator"));
        if (req.getOriginalFileName() != null) {
            fnb.append(req.getOriginalFileName());
            fnb.append("_");
        }
        fnb.append(u.getHost());
        fnb.append("_sent_");
        fnb.append(messageID);
        fnb.append("_at_");
        fnb.append(LOGFILEDATE.format(new Date()));
        fnb.append(".log");
        String fname = fnb.toString();
        FileWriter fw = new FileWriter(fname);

        int contentLength = message.length();
        StringBuilder sb = new StringBuilder();
        sb.append("POST ");
        if ((u.getPath() != null) && (u.getPath().trim().length() > 0)) {
            sb.append(u.getPath());
        } else {
            sb.append("/");
        }
        sb.append(" HTTP/1.1\r\nHost: ");
        sb.append(u.getHost());
        sb.append("\r\nSOAPaction: ");
        if (action != null) {
            if (action.trim().charAt(0) == '"') {
                sb.append(action.trim());
            } else {
                sb.append("\"");
                sb.append(action.trim());
                sb.append("\"");
            }
        }
        if (chunkSize == 0) {
            sb.append("\r\nContent-Length: ");
            sb.append(contentLength);
        } else {
            sb.append("\r\nTransfer-Encoding: chunked");
        }
        sb.append("\r\n");
        sb.append("Content-type: text/xml\r\nServer: CfH ITK Test Platform\r\nConnection: close\r\n\r\n");
        // sb.append(message);
        header = sb.toString();
        fw.write(header);
        fw.flush();
        fw.write(message);
        fw.flush();

        if (System.getProperty(NOORIGINATE) != null) {
            fw.write("\n\nSender NOT transmitting due to internal request: ");
            fw.write(System.getProperty(NOORIGINATE));
            fw.flush();
            fw.close();
            String dontDoSignature = System.getProperty("tks.skipsignlogs");
            if ((dontDoSignature == null) || (dontDoSignature.toUpperCase().startsWith("Y"))) {
                LogVerifier lv = LogVerifier.getInstance();
                lv.makeSignature(fname);
            }
            return;
        }

        fw.flush();

        Socket s = null;
        SocketFactory sf = null;
        if (tls) {
            try {
                if (sslContext == null) {
                    sf = SSLSocketFactory.getDefault();
                } else {
                    sf = sslContext.getSocketFactory();
                }
                if (u.getPort() > 0) {
                    s = sf.createSocket(u.getHost(), u.getPort());
                } else {
                    s = sf.createSocket(u.getHost(), 443);
                }
            }
            catch(Exception e) {
                l.log("Sender-direct", "Failed to create outbound SSL socket: " + e.getMessage());
                return;
            }
        } else {
            try {

                sf = SocketFactory.getDefault();
                if (u.getPort() > 0) {
                    s = sf.createSocket(u.getHost(), u.getPort());
                } else {
                    s = sf.createSocket(u.getHost(), 80);
                }
            }
            catch(Exception e) {
                l.log("Sender-direct", "Failed to create outbound socket: " + e.getMessage());
                fw.flush();
                fw.close();
                String dontDoSignature = System.getProperty("tks.skipsignlogs");
                if ((dontDoSignature == null) || (dontDoSignature.toUpperCase().startsWith("Y"))) {
                    LogVerifier lv = LogVerifier.getInstance();
                    lv.makeSignature(fname);
                }
                return;
            }
        }
        String activity = null;
        try {
            activity = "Creating reader";
            InputStreamReader isr = new InputStreamReader(s.getInputStream());
            activity = "Creating header";
            OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
            activity = "Writing header";
            osw.write(header);
            activity = "Flushing header";
            osw.flush();
            activity = "Writing message";
            if (chunkSize == 0) {
                //osw.write(header);
                //activity = "Flushing header";
                osw.flush();
                activity = "Writing message";
                osw.write(message);
                activity = "Flushing message";
                osw.flush();
            } else {
                int bytesWritten = 0;
                int cs = 0;
                while (bytesWritten < contentLength) {
                    cs = ((contentLength - bytesWritten) < chunkSize) ? (contentLength - bytesWritten) : chunkSize;
                    String clen = Integer.toHexString(cs);
                    osw.write(clen);
                    osw.flush();
                    osw.write("\r\n");
                    osw.flush();
                    osw.write(message.substring(bytesWritten, bytesWritten + cs));
                    bytesWritten += cs;
                    osw.flush();
                    osw.write("\r\n");
                    osw.flush();
                }
                if (cs != 0) {
                    osw.write("\r\n0\r\n");
                    osw.flush();
                }
            }
            if (!tls) {
                activity = "Shutting down output";
                s.shutdownOutput();
            }

            // Write the rest of the data back to the log file
            int c = 0;
            activity = "Clearing input data";
            boolean responseReceived = false;
            StringBuilder sbr = new StringBuilder();
            while((c = isr.read()) != -1) {
                responseReceived = true;
                sbr.append(Character.toString((char)c));
            }
            fw.write("\r\n**** END REQUEST ****\r\n");
            fw.write(sbr.toString());
            fw.flush();

            if (!responseReceived) {
                fw.write("\r\nNo HTTP response received - input stream already at EOF\r\n");
                fw.flush();
            }
            
            if (!tls) {
                activity = "Shutting down input";
                s.shutdownInput();
            }
            activity = "Closing";
            s.close();
            fw.flush();
            fw.close();
            String dontDoSignature = System.getProperty("tks.skipsignlogs");
            if ((dontDoSignature == null) || (dontDoSignature.toUpperCase().startsWith("Y"))) {
                LogVerifier lv = LogVerifier.getInstance();
                lv.makeSignature(fname);
            }
        }
        catch (Exception e) {
            l.log("Sender-direct", "Failed to send message to address " + address + " : " + e.getMessage() + " : " + activity);
        }
    }
}
