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
package org.warlock.tk.experimental.distributor.SpineInjector;
import org.safehaus.uuid.UUIDGenerator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.InetAddress;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
/**
 * THIS PACKAGE IS NOT TO BE USED. Development of a Spine message for transmitting
 * ITK requests is underway, but will NOT use the "forward reliable" message used here.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class SpineInjector
    extends Thread
{
    private static SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static SimpleDateFormat HL7FORMATDATE = new SimpleDateFormat("yyyyMMddHHmmss");
    private SpineInjectorFactory factory = null;
    private String asid = null;
    private String msg = null;

    SpineInjector(SpineInjectorFactory f, String m, String a) {
        factory = f;
        asid = a;
        msg = m;
        start();
    }

    private boolean substitute(StringBuilder sb, String tag, String content)
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

    @Override
    public void run() {
        DirectoryEntry d = null;
        try {
            d = factory.getDirectoryEntry(asid);
        }
        catch(Exception e) {
            System.err.println("Invalid ASID: " + asid);
            return;
        }
        StringBuilder sb = new StringBuilder(factory.getCommonContentTemplate());
        String msgid = UUIDGenerator.getInstance().generateTimeBasedUUID().toString().toUpperCase();
        String date = ISO8601FORMATDATE.format(new Date());
        substitute(sb, "__MESSAGE_ID__", msgid);
        substitute(sb, "__CPAID__", d.getCPAId());
        substitute(sb, "__MSGTIME__", date);
        substitute(sb, "__TOPARTY__", d.getPartyKey());
        substitute(sb, "__FROMPARTY__", factory.getLocalPartyKey());
        substitute(sb, "__PAYLOAD__", msg);
        substitute(sb, "__TO_ASID__", asid);
        substitute(sb, "__FROM_ASID__", factory.getLocalAsid());
        substitute(sb, "__CREATION_TIME__", HL7FORMATDATE.format(new Date()));

        StringBuilder message = new StringBuilder("POST ");
        message.append(factory.getSpinePath());
        message.append(" HTTP/1.1\r\n");
        message.append("Host: ");
        message.append(factory.getSpineAddress());
        message.append("\r\nSOAPAction: ");
        message.append("urn:nhs:names:services:cc/COPC_IN000001UK01");
        message.append("\r\nContent-Length: ");
        message.append(sb.length());
        message.append("\r\nContent-Type: multipart/related; boundary=\"--=_MIME-Boundary\"; type=\"text/xml\"; start=\"<ebXMLHeader@spine.nhs.uk>\"");
        message.append("\r\nConnection: close\r\n\r\n");
        message.append(sb);
        String xmit = message.toString();

        Socket s = null;
        BufferedWriter netWriter = null;
        BufferedReader netReader = null;
        try {
            InetAddress addr = InetAddress.getByName(factory.getProxyAddress());
            s = new Socket(addr, factory.getProxyPort());
            s.setSoTimeout(60000);
            netWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            netReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        }
        catch (Exception e) {
            System.err.println("Could not open connection to " + factory.getProxyAddress() + ":" + Integer.toString(factory.getProxyPort()) + " - " + e.getMessage());
        }
        StringBuffer sbrecv = new StringBuffer();
        try {
            String line = null;
            netWriter.write(xmit);
            netWriter.flush();
            while((line = netReader.readLine()) != null) {
                sbrecv.append(line);
                sbrecv.append("\r\n");
            }
            s.close();
        }
        catch (Exception e) {
            System.err.println("Failed to send message " + e.getMessage());
        }

    }
}
