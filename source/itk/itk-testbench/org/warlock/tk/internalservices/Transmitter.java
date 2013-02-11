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
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.boot.ToolkitSimulator;
import org.warlock.tk.boot.ToolkitService;
import java.util.Properties;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import org.safehaus.uuid.UUIDGenerator;
import org.warlock.tk.internalservices.send.SenderRequest;
import org.warlock.util.Logger;
 /**
 * Service to implement the TKW "transmit" mode. 
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class Transmitter
    implements org.warlock.tk.boot.ToolkitService, Reconfigurable
{

    private static final String NOORIGINATE = "tks.system.internal.donotoriginate";

    private static final String TIMESTAMP = "__TIMESTAMP__";
    private static final String EXPIRES = "__EXPIRES__";
    private static final String MESSAGEID = "__MESSAGEID__";

    private static final String TRANSMITDIR = "tks.transmitter.source";
    private static final String TXTTL = "tks.transmitter.timetolive";
    private static final String TXNOSEND = "tks.transmitter.nosend";
    private static final String ADDRESS = "tks.transmitter.send.url";
    private static final String CHUNKXMIT = "tks.transmitter.send.chunksize";

    private static SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static SimpleDateFormat LOGFILEDATE = new SimpleDateFormat("yyyyMMddHHmmss");

    private File sourceDirectory = null;
    private String address = null;
    private boolean nosend = false;
    private int chunkSize = 0;
    private int ttl = -1;
    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;

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
        if (what.contentEquals("sourceDirectory")) {
            sourceDirectory = new File(value);
            return;
        }
        if (what.contentEquals("address")) {
            address = value;
            return;
        }
        throw new Exception("Configuration not supported");
    }
    
    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception
    {
        bootProperties = p;
        serviceName = s;
        simulator = t;

        String prop = null;
        prop = bootProperties.getProperty(TRANSMITDIR);
        if ((prop == null) || (prop.trim().length() == 0)) {
            throw new Exception("Transmitter: null or empty source directory " + TRANSMITDIR);
        }
        sourceDirectory = new File(prop);
        if (!sourceDirectory.canRead()) {
            throw new Exception("Transmitter: Unable to read source directory " + prop);
        }
        prop = bootProperties.getProperty(TXTTL);
        if ((prop == null) || (prop.trim().length() == 0)) {
            throw new Exception("Transmitter: null or empty TTL given " + TXTTL);
        }
        try {
            ttl = Integer.parseInt(prop);
        }
        catch (NumberFormatException e) {
            throw new Exception("Transmitter: Invalid TTL: " + prop);
        }
        prop = bootProperties.getProperty(ADDRESS);
        if ((prop == null) || (prop.trim().length() == 0)) {
            throw new Exception("Transmitter: null or empty address given " + ADDRESS);
        }
        address = prop;
        prop = bootProperties.getProperty(TXNOSEND);
        if ((prop != null) && prop.toUpperCase().startsWith("Y")) {
            nosend = true;
        }
        prop = bootProperties.getProperty(CHUNKXMIT);
        if ((prop != null) && prop.trim().length() > 0) {
            chunkSize = Integer.parseInt(prop);
        }
        System.out.println(serviceName + " started, class: " + this.getClass().getCanonicalName());
    }

    /**
     * Transmit all files in the transmitter source directory as configured in the 
     * TKW properties file. Performs substitutions of timestamps, and SOAP header
     * signing, as required.
     * 
     * @param param Not used.
     * @return Empty ServiceResponse.
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception
    {
        int sent = 0;
        String files[] = sourceDirectory.list();
        for (String f : files) {
            if (sendMessage(sourceDirectory.getPath(), f)) {
                sent++;
            }
        }
        return new ServiceResponse(sent, null);
    }

    /** 
     * Transmit the given file in the given directory.
     * 
     * @param dir Directory
     * @param filename File to send
     * @return
     * @throws Exception 
     */
    private boolean sendMessage(String dir, String filename)
            throws Exception
    {
        File f = new File(dir, filename);
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        StringBuilder msg = new StringBuilder();
        while ((line = br.readLine()) != null) {
            msg.append(line);
        }
        fr.close();
        Date now = new Date();
        String ts = ISO8601FORMATDATE.format(now);
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.SECOND, ttl);
        Date expires = cal.getTime();
        String exp = ISO8601FORMATDATE.format(expires);
        String msgid = UUIDGenerator.getInstance().generateTimeBasedUUID().toString().toUpperCase();
        String ft = LOGFILEDATE.format(now);
        boolean timeStampDone = substitute(msg, TIMESTAMP, ts);
        substitute(msg, TIMESTAMP, ts);
        substitute(msg, EXPIRES, exp);
        substitute(msg, MESSAGEID, msgid);
        String m = msg.toString();

        if (timeStampDone) {
            ToolkitService tksign = simulator.getService("Signer");
            if (tksign != null) {
                ServiceResponse r = tksign.execute("sign", m);
                m = r.getResponse();
            }
        }
        if (nosend) {
            System.setProperty(NOORIGINATE, "Transmitter instructed not to send");
        }
        ToolkitService tksnd = simulator.getService("Sender");
        if (tksnd != null) {
            SenderRequest sr = new SenderRequest(m.toString(), null, address);
            if (System.getProperty("tkw.internal.runningautotest") != null) {
                sr.setOriginalFileName(filename);
            }
            if (chunkSize != 0) {
                sr.setChunkSize(chunkSize);
            }
            tksnd.execute(sr);
        } else {
            return false;
        }
       
        return true;
    }

    boolean substitute(StringBuilder sb, String tag, String content)
            throws Exception
    {
        boolean doneSomething = false;
        int tagPoint = -1;
        int tagLength = tag.length();
        while ((tagPoint = sb.indexOf(tag)) != -1 ) {
            doneSomething = true;
            sb.replace(tagPoint, tagPoint + tagLength, content);
        }
        return doneSomething;
    }

    /**
     * No operation
     * 
     * @param type
     * @param param
     * @return
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, String param)
            throws Exception
    {
        return new ServiceResponse(0, null);
    }

    /**
     * No operation.
     * 
     * @param type
     * @param param
     * @return
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, Object param)
            throws Exception
    {
        return new ServiceResponse(0, null);
    }

}
