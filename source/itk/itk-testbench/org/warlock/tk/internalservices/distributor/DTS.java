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
 
package org.warlock.tk.internalservices.distributor;
import org.warlock.tk.boot.ToolkitSimulator;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.util.Properties;
/**
 * Class implementing a "send" interface to the Data Transfer Service, as part
 * of the TKW implementation of the ITK message routing specfication. Configured through
 * the TKW properties file.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class DTS {

    private static DTS me = new DTS();
    private static Exception bootException = null;
    
    private static final String APPNAME = "tks.transport.dts.appname";
    private static final String SITENAME = "tks.transport.dts.sitename";
    private static final String SEQFILE = "tks.transport.dts.sequencefile";
    private static final String TMPDIR = "tks.transport.dts.tmpdir";
    private static final String DTSOUT = "tks.transport.dts.dtsoutdir";
    private static final String CTLTEMPLATE = "tks.transport.dts.controlfiletemplate";
    
    private static boolean initialised = false;
    
    private static String appName = null;
    private static String siteName = null;
    private static String sequenceFile = null;
    private static String tmpDir = null;
    private static String dtsOutdir = null;
    private static String templateFile = null;
    private static String controlFileTemplate = null;
    
    
    private DTS() {
     }
    
    private static boolean loadTemplate() 
            throws Exception
    {
        try {
            FileReader f = new FileReader(templateFile);
            BufferedReader br = new BufferedReader(f);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            f.close();
            controlFileTemplate = sb.toString();
        }
        catch (Exception e) {
            bootException = new Exception("Fail to load control template file " + templateFile + " : " + e.toString());
            throw bootException;
        }
        return true;
    }
    
    private static void init(ToolkitSimulator t) 
            throws Exception
    {
        Properties prop = t.getService("Toolkit").getBootProperties(); 
        String p = null;
        p = prop.getProperty(APPNAME);
        if (p == null) {
            bootException = new Exception("App Name property " + APPNAME + " not set");
            throw bootException;
        }
        appName = p;
        p = prop.getProperty(CTLTEMPLATE);
        if (p == null) {
            bootException = new Exception("Control file template name property " + CTLTEMPLATE + " not set");
            throw bootException;
        }
        templateFile = p;
        if (!loadTemplate()) {
            return;
        }
        p = prop.getProperty(SITENAME);
        if (p == null) {
            bootException = new Exception("Site Name property " + SITENAME + " not set");
            throw bootException;
        }
        siteName = p;
        p = prop.getProperty(SEQFILE);
        if (p == null) {
            bootException = new Exception("TKW temporary directory property " + SEQFILE + " not set");
            throw bootException;
        }
        sequenceFile = p;
        p = prop.getProperty(TMPDIR);
        if (p == null) {
            bootException = new Exception("TKW temporary directory property " + TMPDIR + " not set");
            throw bootException;
        }
        tmpDir = p;
        p = prop.getProperty(DTSOUT);
        if (p == null) {
            bootException = new Exception("DTS outbound directory property " + DTSOUT + " not set");
            throw bootException;
        }
        dtsOutdir = p;
        initialised = true;
    }
    
    public static DTS getInstance(ToolkitSimulator t) 
            throws Exception
    {
        if (bootException != null)
            throw bootException;
        if (!initialised) {
            init(t);
        }        
        return me; 
    }
    
    /**
     * Intended to be called by the TKW message router to queue a message for DTS. The
     * actual transmission is performed by the DTS client software.
     * 
     * @param msg DistributionEnvelope of the message to be sent.
     * @param address DTS target endpoint address
     * @param service ITK service name
     * @param transmissionid ITK message transmission id
     * @throws Exception 
     */
    public void send(String msg, String address, String service, String transmissionid)
            throws Exception
    {
        int seq = getSequence();
        String fname = makeFileName(seq);
        String dfile = fname + ".dat";
        FileWriter f = new FileWriter(dfile);
        f.write(msg);
        f.flush();
        f.close();
        String cf = makeControl(address, service, transmissionid);
        String cfile = fname + ".ctl";
        f = new FileWriter(cfile);
        f.write(cf);
        f.flush();
        f.close();
    }

    private String makeControl(String address, String service, String transmissionid)
            throws Exception
    {
        StringBuilder sb = new StringBuilder(controlFileTemplate);
        substitute(sb, "__SITE_ID__", siteName);
        substitute(sb, "__ADDRESS__", address);
        substitute(sb, "__TRACKING_ID__", transmissionid);
        substitute(sb, "__SERVICE__", service);
        return sb.toString();
    }
    
    private void substitute(StringBuilder sb, String tag, String o)
            throws Exception
    {
        int tagPoint = -1;
        int tagLength = tag.length();
        while ((tagPoint = sb.indexOf(tag)) != -1 ) {
            sb.replace(tagPoint, tagPoint + tagLength, o);
        }
    }
    
    private String makeFileName(int seq) {
    
        StringBuilder sb = new StringBuilder(tmpDir);
        sb.append(siteName);
        sb.append(appName);
        String s = Integer.toString(seq);
        int l = 8 - s.length();
        for (; l != 0; l--) {
            sb.append("0");
        }
        sb.append(s);
        return sb.toString();
    }
    
    
    private synchronized int getSequence()
            throws Exception
    {
        try {
            File f = new File(sequenceFile);
            if (!f.exists()) {
                FileWriter w = new FileWriter(sequenceFile);
                w.append("1");
                w.flush();
                w.close();
            }
            BufferedReader r = new BufferedReader(new FileReader(f));
            String s = r.readLine();
            if (s == null) {
                throw new Exception("Empty sequence file");
            }
            r.close();
            int snum = Integer.parseInt(s);
            int nnum = snum + 1;
            if (nnum == 100000000) {
                nnum = 1;
            }
            FileWriter w = new FileWriter(sequenceFile, false);
            w.write(Integer.toString(nnum));
            w.flush();
            w.close();
            return snum;
        }
        catch (Exception e) {
            throw new Exception("Failed to get sequence number: " + e.toString());
        }
    }
     
}
