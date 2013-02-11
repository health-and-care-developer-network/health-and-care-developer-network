/*
  Copyright 2012 Damian Murphy <murff@warlock.org>

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

package org.warlock.tk.internalservices.distributor.xdradapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.warlock.util.CfHNamespaceContext;
import org.safehaus.uuid.UUIDGenerator;
import java.io.StringWriter;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.apache.commons.codec.binary.Base64;
import java.net.URL;
/**
 * Implementation of the Transmission interface to load an XDR message, and present its
 * parts for retrieval by an ITK transmission. Supports both XOP and in-line presentation.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 

public class XDRtransmission
    implements Transmission
{
    private static final SimpleDateFormat XDRDATEFORMAT = new SimpleDateFormat("yyyyMMddhhmmss");
    private static final String USE_XOP = "tks.itkxdradapter.serialisetoxop";

    private String id = null;
    private ArrayList<Actor> authors = null;
    private ArrayList<Actor> recipients = null;
    private ArrayList<Payload> payloads = null;
    private MetadataPayload metadata = null;

    private String name = null;
    private String description = null;
    private String sourceId = null;
    private String submissionSetId = null;
    private String submissionDate = null;

    private boolean isXop = false;
        
    public XDRtransmission() {
        recipients = new ArrayList<Actor>();
        authors = new ArrayList<Actor>();
        payloads = new ArrayList<Payload>();        
    }
    
    @Override
    public String getId() { return id; }
    @Override
    public ArrayList<Actor> getAuthors() { return authors; }
    @Override
    public ArrayList<Actor> getRecipients() { return recipients; }
    @Override
    public ArrayList<Payload> getPayloads() { return payloads; }
    @Override
    public MetadataPayload getMetadata() { return metadata; }

    @Override
    public void convert(Transmission t) throws Exception 
    {
        if (t == null) {
            throw new Exception("Invalid source: null transmission given");
        }
        id = t.getId();
        if (id.startsWith("#")) {
            id = id.substring(1);
        }
        authors = t.getAuthors();
        recipients = t.getRecipients();
        payloads = t.getPayloads();
        metadata = t.getMetadata();
    }

    public static byte[] getBinaryContent(String base64) 
    {
        Base64 b64 = new Base64();
        byte[] bytes = b64.decode(base64.getBytes());
        return bytes;
    }

    public static String makeSOAPPartHeader(String boundary)
    {
        StringBuilder sb = new StringBuilder("--");
        sb.append(boundary);
        sb.append("\r\n");
        sb.append("Content-ID: <itk.xdr.xop.request>");
        sb.append("\r\nContent-Transfer-Encoding: 8bit\r\nContent-Type: application/xop+xml;charset=utf-8;type=\"application/soap+xml\"\r\n\r\n");        
        return sb.toString();
    }    
    
    public static String makePartHeader(String id, String boundary)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n--");
        sb.append(boundary);
        sb.append("\r\n");
        sb.append("Content-ID: <");
        sb.append(id);
        sb.append(">\r\nContent-Transfer-Encoding: binary\r\nContent-Type: application/octet-stream\r\n\r\n");        
        return sb.toString();
    }
    
    public static void toXopOutputStream(String soap, String tourl, OutputStream o)
            throws Exception
    {
        int contentLength = 0;
        
        URL u = new URL(tourl);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);
        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(soap)));
        Element de = doc.getDocumentElement();
        
        // Find and replace the Document content
        //
        HashMap<String, byte[]> documents = new HashMap<String,byte[]>();
        HashMap<String, String> partHeaders = new HashMap<String,String>();
        String boundary = UUIDGenerator.getInstance().generateRandomBasedUUID().toString().toLowerCase();
        String soapPartHeader = makeSOAPPartHeader(boundary);
        contentLength += soapPartHeader.length();
        
        NodeList nl = de.getElementsByTagNameNS(CfHNamespaceContext.IHE_XDS_B, "Document");
        for (int i = 0; i < nl.getLength(); i++) {
            Element d = (Element)nl.item(i);
            Element incl = doc.createElementNS(CfHNamespaceContext.XOP, "Include");
            String href = "cid:" + d.getAttribute("id");
            incl.setAttribute("href", href);
            byte[] cntnt = getBinaryContent(d.getTextContent());
            documents.put(href, cntnt);
            String ph = makePartHeader(d.getAttribute("id"), boundary);
            contentLength += ph.length();
            contentLength += cntnt.length;
            partHeaders.put(href, ph);
            d.setTextContent("");
            d.appendChild(incl);
        }
        // Now make the SOAP XML.
        //
        Document soapOut = dbf.newDocumentBuilder().newDocument();
        Element soapenv = soapOut.createElementNS(CfHNamespaceContext.SOAP12, "Envelope");
        soapOut.appendChild(soapenv);
        Element soaphdr = soapOut.createElementNS(CfHNamespaceContext.SOAP12, "Header");
        Element elem = soapOut.createElementNS(CfHNamespaceContext.WSANAMESPACE, "Action");
        elem.setAttributeNS(CfHNamespaceContext.SOAP12, "mustUnderstand", "1");
        elem.setTextContent("urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b");
        soaphdr.appendChild(elem);
        elem = soapOut.createElementNS(CfHNamespaceContext.WSANAMESPACE, "MessageID");
        elem.setTextContent(UUIDGenerator.getInstance().generateRandomBasedUUID().toString().toUpperCase());
        soaphdr.appendChild(elem);        
        soapenv.appendChild(soaphdr);
        elem = soapOut.createElementNS(CfHNamespaceContext.SOAP12, "Body");
        elem.appendChild((Element)soapOut.adoptNode(de));
        soapenv.appendChild(elem);
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        Transformer tx = TransformerFactory.newInstance().newTransformer();
        tx.transform(new DOMSource(soapOut), sr);
        String soapXML = sw.toString();
        contentLength += soapXML.length();
        
        String endBoundary = "\r\n--" + boundary + "--";
        contentLength += endBoundary.length();
        
        String httpHeader = makeHttpHeader(u, boundary, contentLength);
        o.write(httpHeader.getBytes());
        o.write(soapPartHeader.getBytes());
        o.write(soapXML.getBytes());
        for (String id : documents.keySet()) {
            String hdr = partHeaders.get(id);
            o.write(hdr.getBytes());
            o.write(documents.get(id));
        }
        o.write(endBoundary.getBytes());
    }
    
    private static String makeHttpHeader(URL u, String boundary, int contentLength)
    {
        StringBuilder sb = new StringBuilder("POST ");
        sb.append(u.getPath());
        sb.append(" HTTP/1.1\r\nHost: ");
        sb.append(u.getHost());
        if (u.getPort() != -1) {
            sb.append(":");
            sb.append(Integer.toString(u.getPort()));
        }
        sb.append("\r\nContent-Type: multipart/related; type=\"application/xop+xml\";start=\"<itk.xdr.xop.request>\";boundary=\"");
        sb.append(boundary);
        sb.append("\";start-info=\"application/soap+xml\"\r\nMIME-Version: 1.0\r\nConnection: close\r\nContent-Length: ");
        sb.append(Integer.toString(contentLength));
        sb.append("\r\n\r\n");
        return sb.toString();
    }
    
    @Override
    public String serialise() 
            throws Exception 
    {         
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document d = dbf.newDocumentBuilder().newDocument();
        Element req = d.createElementNS(CfHNamespaceContext.IHE_XDS_B, "ProvideAndRegisterDocumentSetRequest");
        Element sor = d.createElementNS(CfHNamespaceContext.EB_REG_LCM, "SubmitObjectsRequest");
        req.appendChild(sor);
        Element reg = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "RegistryObjectList");
        sor.appendChild(reg);
        if ((metadata != null) && (metadata.getContent() != null)) {
            // Copy...
            Element ext = (Element)d.adoptNode((Element)metadata.getContent());
            reg.appendChild(ext);
        } else {
            // Make one...
            Element ext = makeMetadata(d);    
            reg.appendChild(ext);
        }
        // Make the registry package and add it
        Element r = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "RegistryPackage");
        String rpid = uuidToOID(UUIDGenerator.getInstance().generateRandomBasedUUID().toString().toUpperCase());
        r.setAttribute("id", rpid);
        Element rt = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Slot");
        rt.setAttribute("name", "submissionTime");
        Element rvl = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "ValueList");
        rt.appendChild(rvl);
        Element rv =  d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Value");
        rv.setTextContent(XDRDATEFORMAT.format(new Date()));
        rvl.appendChild(rv);
        r.appendChild(rt);
        Element rs = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Slot");
        rs.setAttribute("name","intendedRecipient");
        rvl = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "ValueList");
        rs.appendChild(rvl);
        for (Actor a : recipients) {
            rv = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Value");
            rv.setTextContent(a.getType() + "^" + a.getURI());
            rvl.appendChild(rv);
        }
        r.appendChild(rs);
        Element n = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Name");
        r.appendChild(n);
        Element l = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "LocalizedString");
        l.setAttribute("value", "ITK-XDR Correspondence message");
        l.setAttribute("xml:lang", "en-GB");
        n.appendChild(l);
        Element desc = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Description");
        l = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "LocalizedString");
        l.setAttribute("value", "ITK-XDR Correspondence message");
        l.setAttribute("xml:lang", "en-GB");
        desc.appendChild(l);
        r.appendChild(desc);
        // And another bunch of classifications
        Element c = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Classification");
        c.setAttribute("classifiedObject", rpid);
        c.setAttribute("id", "urn:uuid:" + UUIDGenerator.getInstance().generateRandomBasedUUID().toString().toLowerCase());
        c.setAttribute("classificationScheme", "urn:uuid:a7058bb9-b4e4-4307-ba5b-e3f0ab85e12d");
        c.setAttribute("nodeRepresentation", "");
        rs = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Slot");
        rs.setAttribute("name", "authorPerson");
        rvl = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "ValueList");
        rs.appendChild(rvl);
        rv = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Value");
        rvl.appendChild(rv);
        rv.setTextContent(authors.get(0).getType() + "^" + authors.get(0).getURI());
        c.appendChild(rs);
        r.appendChild(c);
        
        c = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Classification");
        c.setAttribute("classifiedObject", rpid);
        c.setAttribute("id", "urn:uuid:" + UUIDGenerator.getInstance().generateRandomBasedUUID().toString().toLowerCase());
        c.setAttribute("classificationScheme", "urn:uuid:aa543740-bdda-424e-8c96-df4873be8500");
        c.setAttribute("nodeRepresentation", "Communication");
        rs = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Slot");
        c.appendChild(rs);
        rs.setAttribute("name", "codingScheme");
        rvl = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "ValueList");
        rs.appendChild(rvl);
        rv = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Value");
        rvl.appendChild(rv);
        rv.setTextContent("Connect-a-thon contentTypeCodes");
        n = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Name");
        l = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "LocalizedString");
        n.appendChild(l);
        c.appendChild(n);
        l.setAttribute("value", "Communication");
        l.setAttribute("xml:lang", "en-GB");        
        r.appendChild(c);
        
        // And some ExternalIdentifiers
        Element ext = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "ExternalIdentifier");
        ext.setAttribute("id", "urn:uuid:" + UUIDGenerator.getInstance().generateRandomBasedUUID().toString().toLowerCase());
        ext.setAttribute("registryObject", rpid);
        ext.setAttribute("identificationScheme", "urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446");
        ext.setAttribute("value", getPatientId((Element)metadata.getContent()));
        n = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Name");
        ext.appendChild(n);
        l = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "LocalizedString");
        n.appendChild(l);
        l.setAttribute("value", "XDSSubmissionSet.patientId");
        l.setAttribute("xml:lang", "en-GB");
        r.appendChild(ext);

        ext = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "ExternalIdentifier");
        ext.setAttribute("id", "urn:uuid:" + UUIDGenerator.getInstance().generateRandomBasedUUID().toString().toLowerCase());
        ext.setAttribute("registryObject", rpid);
        ext.setAttribute("identificationScheme", "urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8");
        // TODO: CHange this to an OID - strip off uuid_ from the front if necessary
        ext.setAttribute("value", uuidToOID(id));
        n = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Name");
        ext.appendChild(n);
        l = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "LocalizedString");
        n.appendChild(l);
        l.setAttribute("value", "XDSSubmissionSet.uniqueId");
        l.setAttribute("xml:lang", "en-GB");
        r.appendChild(ext);

        ext = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "ExternalIdentifier");
        ext.setAttribute("id", "urn:uuid:" + UUIDGenerator.getInstance().generateRandomBasedUUID().toString().toLowerCase());
        ext.setAttribute("registryObject", rpid);
        ext.setAttribute("identificationScheme", "urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832");
        // Note: This value is a CFH Source ID
        ext.setAttribute("value", "1.3.6.1.4.1.21367.2011.1.2.1080");
        n = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Name");
        ext.appendChild(n);
        l = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "LocalizedString");
        n.appendChild(l);
        l.setAttribute("value", "XDSSubmissionSet.sourceId");
        l.setAttribute("xml:lang", "en-GB");
        r.appendChild(ext);
        
        
        reg.appendChild(r);
        
        // classification
        //
        c = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Classification");
        c.setAttribute("classifiedObject", rpid);
        c.setAttribute("classificationNode", "urn:uuid:a54d6aa5-d40d-43f9-88c5-b4633d873bdd");
        c.setAttribute("id", "id_10");
        c.setAttribute("objectType", "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification");
        reg.appendChild(c);
        
        // has member association
        //
        Element assoc = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Association");
        assoc.setAttribute("associationType", "urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember");
        assoc.setAttribute("sourceObject", rpid); 
        //assoc.setAttribute("targetObject", "urn:uuid:" + payloads.get(0).getId());
        assoc.setAttribute("targetObject", payloads.get(0).getId());
        assoc.setAttribute("id", "id_11");
        assoc.setAttribute("objectType", "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Association");
        assoc.appendChild(makeSingleSlot(d, "SubmissionSetStatus", "Original", null)); 
        reg.appendChild(assoc);
        
        // Actual payload ... difference here for XOP encapsulated in processContent()
        //
        Element doc = d.createElementNS(CfHNamespaceContext.IHE_XDS_B, "Document");
        // doc.setAttribute("id", "urn:uuid:" + payloads.get(0).getId());
        doc.setAttribute("id", payloads.get(0).getId());
        doc.setTextContent(processContent(payloads.get(0)));
        req.appendChild(doc);
        
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        Transformer tx = TransformerFactory.newInstance().newTransformer();
        tx.transform(new DOMSource(req), sr);
        return sw.toString();
    }
    
    private String getPatientId(Element ext) 
    {    
        NodeList nl = ext.getElementsByTagNameNS(CfHNamespaceContext.EB_REG_RIM, "Slot");
        for (int i = 0; i < nl.getLength(); i++) {
            Element n = (Element)nl.item(i);
            if (n.hasAttribute("name") && n.getAttribute("name").contentEquals("sourcePatientId")) {
                NodeList vl = n.getElementsByTagNameNS(CfHNamespaceContext.EB_REG_RIM, "ValueList");
                if (vl.getLength() > 0) {
                    NodeList v = ((Element)vl.item(0)).getElementsByTagNameNS(CfHNamespaceContext.EB_REG_RIM, "Value");
                    if (v.getLength() > 0) {
                        Element pid = (Element)v.item(0);
                        String s = pid.getTextContent();
                        return s;
                    }
                }
            }
        }
        return "";
    }
    
    private String processContent(Payload p) 
    {        
        if (p.isBase64()) {
            return (String)p.getContent();
        }
        if (p.getMimetype().startsWith("text/xml")) {
            try {
                Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                Element content = (Element)d.adoptNode((Element)p.getContent());
                StringWriter sw = new StringWriter();
                StreamResult sr = new StreamResult(sw);
                Transformer tx = TransformerFactory.newInstance().newTransformer();
                tx.transform(new DOMSource(content), sr);            
                Base64 b64 = new Base64();
                byte[] b64bytes = b64.encode((sw.toString()).getBytes());
                return new String(b64bytes);
            }
            catch (Exception e) {
                return e.toString();
            }
        }
        return "";
    }
    
    private Element makeSingleSlot(Document d, String type, String val, String name) {
        Element slot = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Slot");
        slot.setAttribute("name", type);
        Element valueList = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "ValueList");
        Element value = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Value");
        value.setTextContent(val);
        valueList.appendChild(value);
        slot.appendChild(valueList);
        if (name != null) {
            Element n = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Name");
            Element l = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "LocalizedString");
            l.setAttribute("value", name);
            n.appendChild(l);
            slot.appendChild(n);
        }
        return slot;
    }
    
    private Element makeMetadata(Document d) {
            Element ext = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "ExtrinsicObject");
            ext.setAttribute("objectType", "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
            ext.setAttribute("mimeType", payloads.get(0).getMimetype());
            ext.setAttribute("id", payloads.get(0).getId());
            ext.appendChild(makeSingleSlot(d, "creationTime", XDRDATEFORMAT.format(new Date()), null));
            ext.appendChild(makeSingleSlot(d, "languageCode", "en-gb", null));
            ext.appendChild(makeSingleSlot(d, "sourcePatientId", "Not available - see document", null));
            Element n = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Name");
            Element ls = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "LocalizedString");
            ls.setTextContent("Clinical Document from " + authors.get(0).getURI());
            n.appendChild(ls);
            ext.appendChild(n);
            n = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Description");
            ls = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "LocalizedString");
            ls.setTextContent("Document transmission from ITK");
            n.appendChild(ls);
            ext.appendChild(n);    
            n = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Classification");
            n.setAttribute("classificationScheme", "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d");
            n.setAttribute("classifiedObject", payloads.get(0).getId());
            n.setAttribute("objectType", "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification");
            n.setAttribute("id", "id_1");
            Element s = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Slot");
            Element vs = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "ValueList");
            s.appendChild(vs);
            n.appendChild(s);
            for (Actor a : authors) {
                Element v = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Value");
                v.setTextContent(a.getType() + "^" + a.getURI());
                vs.appendChild(v);
            }
            ext.appendChild(n);
            
            // Note: *THESE* won't work... need replacing with values that will...
            // ... which they *do* get replaced by is work for the ITK/IHE affinity
            // domain to define. In the mean time, these are thre IHE Connectathon
            // set.
            //
            
            ext.appendChild(makeCodingSchemeClassification(d, "urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a", payloads.get(0).getId(), 
                    "id_200", "Connect-a-thon classCodes", "Communication", "Communication"));
            
            // This needs fixing to some more suitable privacy/restriction code
            ext.appendChild(makeCodingSchemeClassification(d, "urn:uuid:f4f85eac-e6cb-4883-b524-f2705394840f", payloads.get(0).getId(), 
                    "id_201", "2.16.840.1.113883.5.25", "Normal", "N"));
            
            // This needs fixing to something to do with CDA
            ext.appendChild(makeCodingSchemeClassification(d, "urn:uuid:a09d5840-386c-46f2-b5ad-9c3699a4309d", payloads.get(0).getId(), 
                    "id_202", "Connect-a-thon formatCodes", "PDF", "PDF"));
            
            // This should be OK pending definition of a proper source
            ext.appendChild(makeCodingSchemeClassification(d, "urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1", payloads.get(0).getId(), 
                    "id_203", "2.16.840.1.113883.5.11", "Hospitals", "HOSP"));

            // This should be OK
            ext.appendChild(makeCodingSchemeClassification(d, "urn:uuid:cccf5598-8b07-4b77-a05e-ae952c785ead", payloads.get(0).getId(), 
                    "id_204", "Connect-a-thon practiceSettingCodes", "General Medicine", "General Medicine"));
            
            ext.appendChild(makeExternalIdentifier(d, "urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427",
                    "Not available - see document", "id_8", "XDSDocumentEntry.patientId"));
            ext.appendChild(makeExternalIdentifier(d, "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab",
                    uuidToOID(id), "id_9", "XDSDocumentEntry.uniqueId"));
            return ext;
    }

    private Element makeCodingSchemeClassification(Document d, String schemeId, String refObjId, String localId, String codingScheme, String csName, String value) {
        Element elem = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Classification");
        elem.setAttribute("classificationScheme", schemeId);
        elem.setAttribute("classifiedObject", refObjId);
        elem.setAttribute("nodeRepresentation", value);
        elem.setAttribute("objectType", "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Classification");
        elem.setAttribute("id", localId);
        elem.appendChild(makeSingleSlot(d, "codingScheme", codingScheme, csName));
        return elem;
    }
    
    private Element makeExternalIdentifier(Document d, String is, String val, String id, String n) {
        Element elem = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "ExternalIdentifier");
        elem.setAttribute("identificationScheme", is);
        elem.setAttribute("value", val);
        elem.setAttribute("id", id);
        elem.setAttribute("registryObject", payloads.get(0).getId());
        Element nm = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "Name");
        Element ls = d.createElementNS(CfHNamespaceContext.EB_REG_RIM, "LocalizedString");
        ls.setAttribute("value", n);
        nm.appendChild(ls);
        elem.appendChild(nm);
        return elem;
    }    
    
    private String uuidToOID(String u) {
        if (u.startsWith("uuid_")) {
            u = u.substring(5);
        }
        String[] parts = u.split("-");
        StringBuilder sb = new StringBuilder("2.25.");
        sb.append(Integer.toString(Integer.parseInt(parts[0].substring(0, 4), 16)));
        sb.append(".");
        sb.append(Integer.toString(Integer.parseInt(parts[0].substring(4), 16)));        
        sb.append(".");
        sb.append(Integer.toString(Integer.parseInt(parts[1], 16)));        
        sb.append(".");
        sb.append(Integer.toString(Integer.parseInt(parts[2], 16)));
        sb.append(".");
        sb.append(Integer.toString(Integer.parseInt(parts[3], 16)));
        sb.append(".");
        sb.append(Integer.toString(Integer.parseInt(parts[4].substring(0, 4), 16)));
        sb.append(".");
        sb.append(Integer.toString(Integer.parseInt(parts[4].substring(4, 8), 16)));
        sb.append(".");
        sb.append(Integer.toString(Integer.parseInt(parts[4].substring(8), 16)));
        return sb.toString();
    }
    
    private String oidToUUID(String o) {
        String[] parts = o.split("\\.");
        StringBuilder sb = new StringBuilder();
        
        sb.append(Integer.toHexString(Integer.parseInt(parts[2])));        
        sb.append(Integer.toHexString(Integer.parseInt(parts[3])));
        sb.append("-");
        sb.append(Integer.toHexString(Integer.parseInt(parts[4])));
        sb.append("-");
        sb.append(Integer.toHexString(Integer.parseInt(parts[5])));
        sb.append("-");
        sb.append(Integer.toHexString(Integer.parseInt(parts[6])));
        sb.append("-");
        sb.append(Integer.toHexString(Integer.parseInt(parts[7])));        
        sb.append(Integer.toHexString(Integer.parseInt(parts[8])));
        sb.append(Integer.toHexString(Integer.parseInt(parts[9])));
        return sb.toString();
    }
    
    
    @Override
    public void load(InputStream in, String contenttype)
            throws Exception
    {        
        ContentType ct = new ContentType(contenttype);
        if (ct.getType().toLowerCase().contentEquals("multipart/related")) {
            loadXop(in, ct);
        } else {
            loadSoap(in);
        }
    }

    private void loadXop(InputStream in, ContentType c)
            throws Exception
    {
        String mimeBoundary = c.getField("boundary");
        if ((mimeBoundary == null) || (mimeBoundary.trim().length() == 0)) {
            throw new Exception("MIME boundary not found in content-type header");
        }
        
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        StringBuilder sb = null;
        XopPart current = null;
        XopPart root = null;
        HashMap<String, XopPart> parts = new HashMap<String, XopPart>();
        while ((line = br.readLine()) != null) {
            if (line.contains(mimeBoundary)) {
                if (sb != null) {
                    current.setContent(sb.toString());
                }
                sb = new StringBuilder();
                current = new XopPart();
            } else {
                if (current != null) {
                    if (current.hasHeader()) {
                        sb.append(line);
                        sb.append("\r\n");
                    } else {
                        if (line.trim().length() == 0) {
                            current.setHeader();
                        } else {
                            String[] h = line.split(":");
                            if (h[0].toLowerCase().contentEquals("content-id")) {
                                String idval = line.substring(h[0].length() + 1).trim();
                                if (idval.startsWith("<")) {
                                    idval = idval.substring(1);
                                }
                                if (idval.endsWith(">")) {
                                    idval = idval.substring(0, idval.length() - 1);
                                }
                                current.setId(idval);
                                parts.put(idval, current);
                            }
                            if (h[0].toLowerCase().contentEquals("content-type")) {
                                current.setContentType(h[1]);
                                String ctype = current.getContentType().getField("type");
                                if ((ctype != null) && (ctype.contains("application/soap+xml"))) {
                                    root = current;
                                }
                            }
                        }
                    }
                }
            }
        }
        // By this point we have a hashmap of XopPart instances, keyed on MIME
        // part content id. We also have the "root" part in root. Parse out the 
        // elements of root ...
        
        Element de = parseMain(new ByteArrayInputStream(root.getContent().getBytes()));
        buildPayloads(de);
        
         // ... and build the payloads from the other XopPart instances
        NodeList nl = de.getElementsByTagNameNS(CfHNamespaceContext.XOP, "Include");
        if ((nl.getLength() == 0) && (!parts.isEmpty())) {
            throw new Exception("No XOP Include elements found, but non-empty XOP parts list");
        }
      
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element)nl.item(i);
                if (elem.getLocalName().contentEquals("Include")) {
                    String href = elem.getAttribute("href");
                    href = java.net.URLDecoder.decode(href, "UTF-8");
                    if (href.startsWith("cid:")) {
                        href = href.substring("cid:".length());
                    }
                    XopPart p = parts.get(href);
                    if (p == null) {
                        throw new Exception("No part found with content id: " + href);
                    }
                    Element document = (Element)elem.getParentNode();
                    if (document.getLocalName().contentEquals("Document")) {
                        String docid = document.getAttribute("id");
                        for (Payload pl : payloads) {
                            if (pl.getId().contentEquals(docid)) {
                                if (pl.getMimetype().startsWith("text")) {
                                    pl.setContent(p.getContent());
                                } else {
                                    // Only base64 if the content type isn't text/*                                
                                    Base64 b64 = new Base64();
                                    byte[] b64bytes = b64.encode((p.getContent()).getBytes());
                                    String b64doc = new String(b64bytes);
                                    pl.setBase64();
                                    pl.setContent(b64doc);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void buildPayloads(Element de)
            throws Exception
    {
        Element ext = getElement("ExtrinsicObject", de, CfHNamespaceContext.EB_REG_RIM);
        String mt = ext.getAttribute("mimeType");
        if (mt.trim().length() == 0) {
            mt = "application/octet-stream";
        }
        Payload p = new Payload();
        p.setMimetype(mt);
        // XDS "Document" element *is* xs:base64Binary
        p.setBase64();
        Element d = getElement("Document", de, CfHNamespaceContext.IHE_XDS_B);
        p.setId(d.getAttribute("id"));
        p.setContent(d.getTextContent());
        payloads.add(p);        
    }
    
    private void loadSoap(InputStream in)
            throws Exception
    {
        // Get the SOAP body contents (if it isn't ProvideAndRegisterDocumentSetRequest,
        // throw an exception), and parse out the various bits.

        Element de = parseMain(in);
        
        // And then the payload from the received XML
        buildPayloads(de);
    }
    
    private Element parseMain(InputStream in)
            throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);
        Document doc = dbf.newDocumentBuilder().parse(new InputSource(in));
        Element de = doc.getDocumentElement();
        Element el = null;
        NodeList registryPackage = de.getElementsByTagNameNS(CfHNamespaceContext.EB_REG_RIM, "RegistryPackage");
        for (int i = 0; i < registryPackage.getLength(); i++) {
            Node rp = registryPackage.item(i);
            if (rp.getNodeType() == Node.ELEMENT_NODE) {
                Element erp = (Element)rp;
                doSlots(erp);
                el = getElement("Name", erp, CfHNamespaceContext.EB_REG_RIM);                
                if (el != null) {
                    name = getLocalizedStringValue(el);
                }
                el = getElement("Description", erp, CfHNamespaceContext.EB_REG_RIM);                
                if (el != null) {
                    description = getLocalizedStringValue(el);
                }
                getExternalIdentifiers(erp);
            }            
        }
        Element m = getElement("ExtrinsicObject", de, CfHNamespaceContext.EB_REG_RIM);
        metadata = new MetadataPayload();
        metadata.setContent(m);
        return de;
    }        
    
    private void getExternalIdentifiers(Element rp) {
        NodeList nl = rp.getElementsByTagNameNS(CfHNamespaceContext.EB_REG_RIM, "ExternalIdentifier");
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element ei = (Element)nl.item(i);
                if (ei.hasAttribute("identificationScheme")) {
                    if (ei.getAttribute("identificationScheme").contentEquals("urn:uuid:554ac39e-e3fe-47fe-b233-965d2a147832")) {
                        sourceId = ei.getAttribute("value");
                    }
                    if (ei.getAttribute("identificationScheme").contentEquals("urn:uuid:96fdda7c-d067-4183-912e-bf5ee74998a8")) {
                        id = ei.getAttribute("value");
                        submissionSetId = ei.getAttribute("registryObject");                        
                    }
                }
            }
        }
            
    }
    
    private Element getElement(String name, Element rp, String ns) {
        NodeList nl = rp.getElementsByTagNameNS(ns, name);
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return (Element)nl.item(i);
            }
        }
        return null;
    }
    
    private void doSlots(Element rp) {
        NodeList slots = rp.getElementsByTagNameNS(CfHNamespaceContext.EB_REG_RIM, "Slot");
        for (int k = 0; k < slots.getLength(); k++) {
            Element slot = (Element)slots.item(k);
            if (slot.hasAttribute("name")) {
                String slotName = slot.getAttribute("name");
                if (slotName != null) {
                    if (slotName.contentEquals("intendedRecipient")) {
                        NodeList vl = slot.getElementsByTagNameNS(CfHNamespaceContext.EB_REG_RIM, "Value");
                        for (int j = 0; j < vl.getLength(); j++) {
                            Node v = vl.item(j);
                            if (v.getNodeType() == Node.ELEMENT_NODE) {
                                Actor a = new Actor(((Element)v).getTextContent(), "2.16.840.1.113883.2.1.3.2.4.18.22");
                                recipients.add(a);
                            }
                        }
                    }
                    if (slotName.contentEquals("submissionTime")) {
                        submissionDate = getSingleSlotValue(slot);
                    }
                    if (slotName.contentEquals("authorPerson")) {
                        Actor a = new Actor("local_person_oid", getSingleSlotValue(slot));
                        authors.add(a);
                    }
                    if (slotName.contentEquals("authorInstitution")) {
                        Actor a = new Actor("local_organisation_oid", getSingleSlotValue(slot));
                        authors.add(a);
                    }
                    if (slotName.contentEquals("authorRole")) {
                        Actor a = new Actor("local_role_oid", getSingleSlotValue(slot));
                        authors.add(a);
                    }
                }
            }
        }
    }
    
    private String getLocalizedStringValue(Element s) {
        NodeList nl = s.getElementsByTagNameNS(CfHNamespaceContext.EB_REG_RIM, "LocalizedString");
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                if (((Element)nl.item(i)).hasAttribute("value")) {
                    return ((Element)nl.item(i)).getAttribute("value");
                } else {
                    return "";
                }
            }
        }
        return "";
    }
    
    private String getSingleSlotValue(Element s) {
        NodeList vl = s.getElementsByTagNameNS(CfHNamespaceContext.EB_REG_RIM, "Value");
        for (int j = 0; j < vl.getLength(); j++) {
            Node v = vl.item(j);
            if (v.getNodeType() == Node.ELEMENT_NODE) {
                return ((Element)v).getTextContent();
            }
        }   
        return "";
    }
}
