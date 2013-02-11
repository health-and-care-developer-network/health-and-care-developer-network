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
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import org.warlock.util.CfHNamespaceContext;
import org.apache.commons.codec.binary.Base64;
import org.warlock.tk.internalservices.phxmladapter.phxmlconverter;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

/**
 * Service to provide conversion to- and from- the "pipe-and-hat" and XML forms
 * of an HL7v2 message.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class PHAdapter
    implements org.warlock.tk.boot.ToolkitService
{
    private Properties bootProperties = null;
    private String serviceName = null;
    private ToolkitSimulator simulator = null;

    private XPathExpression requestExtractorXpath = null;
    private XPathExpression responseExtractorXpath = null;
    private static final String REQUESTEXTRACTOR = "//itk:payloads/itk:payload[1]";
    private static final String RESPONSEEXTRACTOR = "/itk:DistributionEnvelope/itk:payloads/itk:payload[1]";
    
    public PHAdapter() {}

    @Override
    public Properties getBootProperties() { return bootProperties; }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception
    {
        bootProperties = p;
        serviceName = s;
        simulator = t;

        XPath x = XPathFactory.newInstance().newXPath();
        x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
        requestExtractorXpath = x.compile(REQUESTEXTRACTOR);
        x = XPathFactory.newInstance().newXPath();
        x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
        responseExtractorXpath = x.compile(RESPONSEEXTRACTOR);        
    }

    /**
     * Convert from DOM form HL7v2 XML to pipe-and-hat.
     * 
     * @param type Not used.
     * @param param DOM Node containing the root node and children of the HL7v2 message
     * @return ServiceResponse containing the HL7v2 pipe-and-hat as a string.
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, Object param)
            throws Exception
    {
        // Gets a Node, containing the request DOM
        Node rq = (Node)param;
        Element payload = (Element)requestExtractorXpath.evaluate(rq, javax.xml.xpath.XPathConstants.NODE);
        String b64ph = payload.getTextContent();
        byte[] b64content = b64ph.getBytes();
        byte[] ph = Base64.decodeBase64(b64content);
        String phmsg = new String(ph);
        String mtype = findMessageType(phmsg);

        String phxml = phxmlconverter.getInstance().convert(mtype, phmsg);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        InputSource is = new InputSource(new StringReader(phxml));
        Element p = db.parse(is).getDocumentElement();
        Node np = payload.getOwnerDocument().importNode(p, true);
        payload.setTextContent(null);
        payload.appendChild(np);
        // rq.replaceChild(payload, p);
        StringWriter outfile = new StringWriter();
        StreamResult sr = new StreamResult(outfile);
        Transformer tx = TransformerFactory.newInstance().newTransformer();
        tx.transform(new DOMSource(rq), sr);

        ServiceResponse srv = new ServiceResponse(0, outfile.toString());
        return srv;
    }

    public static String findMessageType(String phmsg)
        throws Exception
    {
        int p = 0;
        int q = 0;
        for (int i = 0; i < 7; i++) {
            p = phmsg.substring(q).indexOf("|");
            if (p == -1) {
                throw new Exception("Error reading message - could not resolve MSH.9");
            }
            q += p + 1;
        }
        // mt is the MSH.9 field
        String mt = phmsg.substring(q + 1);
        boolean first = false;
        boolean second = false;
        for (int i = 0; i < 16; i++) {
            if (mt.charAt(i) == '^') {
                if (!first) {
                    first = true;
                } else {
                    second = true;
                    p = i;
                    break;
                }
            }
            if (mt.charAt(i) == '|') {
                throw new Exception("Error reading message - could not resolve MSH.9.3");
            }
        }
        // mt is MSH.9.3 field
        mt = mt.substring(p);
        StringBuilder sb = new StringBuilder();
        q = 1;
        while (mt.charAt(q) != '|') {
            sb.append(mt.charAt(q));
            q++;
        }
        return sb.toString();
    }

    /**
     * No operation.
     * 
     * @param type
     * @param param
     * @return Empty ServiceResponse.
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, String param)
            throws Exception
    {
        return new ServiceResponse(0, "Toolkit Simulator PH / XML Adapter Service");
    }

    /**
     * Convert HL7v2 pipe-and-hat message as a String, into a String serialisation
     * of the XML equivalent. This serialisation is embeddable as text in an ITK
     * message or response as it contains no XML processing instruction.
     * @param param String containing HL7v2 pipe-and-hat message.
     * @return ServiceResponse containing a string with the XML form.
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception
    {

        // HL7v2 responses are stored and processed as XML. The
        // mimetype should be application/hl7-v2+xml on input, and it needs to
        // be changed to application/hl7-v2
        
        String msg = (String)param;
        int mtstart = msg.indexOf("mimetype=\"application/hl7-v2+xml\"");
        if (mtstart != -1) {
            StringBuilder sb = new StringBuilder((String)param);
            sb.replace(mtstart, mtstart + "mimetype=\"application/hl7-v2+xml\"".length(), "mimetype=\"application/hl7-v2\"");
            msg= sb.toString();
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(msg));
        Element de = db.parse(is).getDocumentElement();
        Element adt = (Element)responseExtractorXpath.evaluate(de, javax.xml.xpath.XPathConstants.NODE);

        String ph = phxmlconverter.getInstance().convert(adt);

        byte[] b64content = ph.getBytes();
        byte[] b64ph = Base64.encodeBase64(b64content);

        String phout = new String(b64ph);
        adt.setTextContent(phout);

        StringWriter outfile = new StringWriter();
        StreamResult sr = new StreamResult(outfile);
        Transformer tx = TransformerFactory.newInstance().newTransformer();
        tx.transform(new DOMSource(de), sr);
        StringBuffer sb = outfile.getBuffer();
        int start = sb.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        int end = start + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length();
        sb.replace(start, end, "");
        return new ServiceResponse(0, sb.toString());
    }

}
