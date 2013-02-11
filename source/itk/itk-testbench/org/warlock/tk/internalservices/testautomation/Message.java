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
package org.warlock.tk.internalservices.testautomation;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.apache.commons.codec.binary.Base64;
import java.util.zip.Deflater;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
 /**
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */

public class Message 
    implements Linkable
{

    private static SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    private static final int ENV = 0;
    private static final int SOAP = 1;
    private String[] wrappers = new String[2];
    
    private String name = null;
    
    private String templatename = null;
    private Template template = null;
    
    private boolean base64 = false;
    private boolean compress = false;
    private boolean soapwrap = false;
    private boolean envwrap = false;
    private String soapaction = null;
    private String auditIdentity = null;
    private String mimetype = null;
    
    private String messageId = null;
    
    private String datasourcename = null;
    private DataSource datasource = null;
    
    private String recordid = null;
    
    private static String distributionEnvelopeTemplate = null;
    private static String soapEnvelopeTemplate = null;
    private static String wsSecurityHeaderTemplate = null;
        
    private String readResource(String rname)
    {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((getClass().getResourceAsStream(rname))));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        }
        catch (Exception e) {
            System.err.println("Fatal: Error reading messaging template: " + rname);
            System.err.println(e.toString());
            return null;
        }        
        return sb.toString();        
    }
    
    private synchronized void  loadResources() {
        if (distributionEnvelopeTemplate == null) {
            distributionEnvelopeTemplate = readResource("DistributionEnvelope.template");
        }
        if (soapEnvelopeTemplate == null) {
            soapEnvelopeTemplate = readResource("SOAPEnvelope.template");
        }
        if (wsSecurityHeaderTemplate == null) {
            wsSecurityHeaderTemplate = readResource("WSSecurityHeaderELement.template");
        }
    }
    
    
    public Message(String[] line) {
        // TODO: Support flags for BASE64 COMPRESS SOAPWRAP ENVWRAP
        loadResources();        
        name = line[0];
        for (int i = 1; i < line.length - 1; i++) {
            if (line[i].contentEquals("USING")) {
                templatename = line[++i];
                continue;
            }
            if (line[i].contentEquals("SOAPACTION")) {
                soapaction = line[++i];
                continue;
            }
            if (line[i].contentEquals("MIMETYPE")) {
                mimetype = line[++i];
                continue;
            }
            if (line[i].contentEquals("AUDITIDENTITY")) {
                auditIdentity = line[++i];
                continue;
            }
            if (line[i].contentEquals("BASE64")) {
                base64 = true;
                continue;
            }
            if (line[i].contentEquals("COMPRESS")) {
                compress = true;
                continue;
            }
            if (line[i].contentEquals("SOAPWRAP")) {
                soapwrap = true;
                continue;
            }
            if (line[i].contentEquals("ENVELOPEWRAP")) {
                envwrap = true;
                continue;
            }            
            if (line[i].contentEquals("WITH")) {
                datasourcename = line[++i];
                continue;
            }
            if (line[i].contentEquals("ID")) {
                recordid = line[++i];
                continue;    
            }
        }
    }
    
    public String getName() { return name; }
    String getMessageId() { return messageId; }
    
    @Override
    public void link(ScriptParser p)
            throws Exception
    {
        if (base64) {
            if (soapaction == null)
                throw new Exception("Base64 requires that the SOAPACTION be specified");
            if (!envwrap)
                throw new Exception("Base64 requires that the message be distribution envelope wrapped");
        }
        if (compress) {
            if (soapaction == null)
                throw new Exception("Compression requires that the SOAPACTION be specified");
            if (!envwrap)
                throw new Exception("Compression requires that the message be distribution envelope wrapped");
        }
        if (envwrap || soapwrap) {
            if (soapaction == null)
                throw new Exception("SOAP and distribution envelope wrapping requires that the SOAPACTION be specified");            
        }
        // Get anything we need out of the parser (and do the same for the other classes)
        if (templatename == null) {
            throw new Exception("Message " + name + " doesn't specify a template");
        }
        template = p.getTemplate(templatename);
        if (template == null) {
            throw new Exception("Message " + name + " : template " + templatename + " not found");
        }
        if (datasourcename == null) {
            throw new Exception("Message " + name + " doesn't specify a data source");
        }
        if (!datasourcename.contentEquals("NULL")) {
            datasource = p.getDataSource(datasourcename);
            if (datasource == null) {
                throw new Exception("Message " + name + " : data source " + datasourcename + " not found");
            }
        }
    }
    
    String instantiate(String ts, String to, String from, String replyto)
            throws Exception
    {        
        String id = null;
        if (datasource != null) {
            if (recordid == null) {
                id = datasource.getNextId();
            } else {
                id = recordid;
            }
        }
        String msg = template.makeMessage(id, datasource);
        if (base64) {
            msg = base64(msg.getBytes());
        }
        if (compress) {
            msg = compress(msg);
        }
        if (envwrap) {
            msg = distributionEnvelopeWrap(msg);
        }
        if (soapwrap) {
            msg = soapWrap(msg, to, from, replyto);
        }
        
        StringBuilder sb = new StringBuilder(name);
        sb.append("_");
        sb.append((id == null) ? "NO_ID" : id);
        sb.append(".msg");
        String fn = sb.toString();
        File f = new File(ts, fn);
        FileWriter fw = new FileWriter(f);
        fw.write(msg);
        fw.flush();
        fw.close();
        return fn;
    }
    
    private String base64(byte[] m) {
        Base64 b64 = new Base64();
        byte[] b = b64.encode(m);
        return new String(b);
    }
    
    private String compress(String m) {
        Deflater d = new Deflater();
        d.setInput(m.getBytes());
        d.finish();
        // WARNING; DANGEROUS ASSUMPTION that the output is not going to be
        // larger than the input... If it is the byte array will blow and we
        // should catch the exception. If that happens, assume we're better off
        // not compressing, so turn "compress" off and just return what we were
        // given
        try {
            byte[] b = new byte[m.length()];
            int l = d.deflate(b);
            return base64(b);
        }
        catch (Exception e) {
            compress = false;
            return m;
        }
    }

    private String makeEndpointReference(String t, String a) {
        StringBuilder sb = new StringBuilder("<wsa:");
        sb.append(t);
        sb.append("><wsa:Address>");
        sb.append(a);
        sb.append("</wsa:Address></wsa:");
        sb.append(t);
        sb.append(">");
        return sb.toString();
    }
    
    private String soapWrap(String m, String to, String from, String replyto)
            throws Exception
    {
        StringBuilder sb = new StringBuilder(soapEnvelopeTemplate);
        messageId = Template.resolveDataValue("__UCASE_UUID__");
        Template.substitute(sb, "__MESSAGE_ID__", messageId);
        Template.substitute(sb, "__SOAP_ACTION__", soapaction);
        Template.substitute(sb, "__TO_URL__", to);
        Template.substitute(sb, "__FROM_URL__", (from != null) ? makeEndpointReference("From", from) : "");
        Template.substitute(sb, "__REPLY_TO__", (replyto != null) ? makeEndpointReference("ReplyTo", replyto) : "");
        if (System.getProperty("tks.internal.autotest.dosigntureheader") != null) {
            Template.substitute(sb, "__SECURITY__", wsSecurityHeaderTemplate);
        } else {
            Template.substitute(sb, "__SECURITY__", "");
        }
        Template.substitute(sb, "__SOAP_BODY__", m);
        return sb.toString();
    }
    
    private String makeSecurity() 
            throws Exception
    {
        StringBuilder sb = new StringBuilder(wsSecurityHeaderTemplate);
        Date d = new Date();
        
        Template.substitute(sb, "__TIMESTAMP__", ISO8601FORMATDATE.format(d));
        int ttl = 0;
        if (System.getProperty("tks.Toolkit.default.asyncttl") == null) {
            ttl = 30;
        } else {
            ttl = Integer.parseInt(System.getProperty("tks.Toolkit.default.asyncttl"));
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.SECOND, ttl);
        Date expires = cal.getTime();
        String exp = ISO8601FORMATDATE.format(expires);
        Template.substitute(sb, "__EXPIRES__", exp);
        return sb.toString();
    }
    
    private String distributionEnvelopeWrap(String m)
            throws Exception
    {
        StringBuilder sb = new StringBuilder(distributionEnvelopeTemplate);
        Template.substitute(sb, "__SOAP_ACTION__", soapaction);
        Template.substitute(sb, "__TRACKING_ID__", Template.resolveDataValue("__UCASE_UUID__"));
        // Initial version: Address list and Sender address are always empty
        Template.substitute(sb, "__ADDRESS_LIST__", "");
        Template.substitute(sb, "__SENDER_ADDRESS__", "");
        if (auditIdentity == null) {
            Template.substitute(sb, "__OID__", "");
            Template.substitute(sb, "__AUDIT_IDENTITY__", "");
        } else {
            if (auditIdentity.contains("/")) {
                Template.substitute(sb, "__OID__", auditIdentity.substring(0, auditIdentity.indexOf("/")));
                Template.substitute(sb, "__AUDIT_IDENTITY__", auditIdentity.substring(auditIdentity.indexOf("/")));                        
            } else {
                Template.substitute(sb, "__OID__", "");
                Template.substitute(sb, "__AUDIT_IDENTITY__", auditIdentity);                        
            }
        }
        String payloadId = Template.resolveDataValue("__UCASE_UUID__");
        Template.substitute(sb, "__PAYLOAD_ID__", payloadId);
        if (mimetype == null) {
            Template.substitute(sb, "__MIME_TYPE__", "text/xml");
        } else {
            Template.substitute(sb, "__MIME_TYPE__", mimetype);
        }
        Template.substitute(sb, "__BASE64__", base64 ? "base64=\"true\"" : "");
        Template.substitute(sb, "__COMPRESSED__", compress ? "compressed=\"true\"" : "");
        Template.substitute(sb, "__PAYLOAD__", m);
        return sb.toString();        
    }
}
