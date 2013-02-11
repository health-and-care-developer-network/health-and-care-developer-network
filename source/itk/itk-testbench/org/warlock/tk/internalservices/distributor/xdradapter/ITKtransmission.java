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
import java.io.InputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import org.warlock.util.CfHNamespaceContext;
import org.xml.sax.InputSource;
import org.safehaus.uuid.UUIDGenerator;
import java.io.StringWriter;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

/**
 * Implementation of the Transmission interface to load an ITK message, and present its
 * parts for retrieval by an XDR transmission.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class ITKtransmission
    implements Transmission
{
    private static final String SENDER_OID = "tks.itkxdradapter.sender.oid";
    private static final String SENDER_ADDRESS = "tks.itkxdradapter.sender.address";
    
    private String id = null;
    private ArrayList<Actor> authors = null;
    private ArrayList<Actor> recipients = null;
    private ArrayList<Payload> payloads = null;
    private MetadataPayload metadata = null;

    private Actor senderAddress = null;
    private String service = null;

    private static final String ITKNS = "urn:nhs-itk:ns:201005";

    public ITKtransmission() {}

    @Override
    public void load(InputStream in, String contenttype)
            throws Exception
    {
        // Always created from a serialised Distribution Envelope, so contenttype
        // is ignored.
        //
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);
        Document doc = dbf.newDocumentBuilder().parse(new InputSource(in));
        Element de = doc.getDocumentElement();
        Element hdr = getElement(de, ITKNS, "header");
        if (hdr == null) {
            throw new Exception("Element {" + ITKNS + "}:header not found");
        }
        id = hdr.getAttribute("trackingid");
        service = hdr.getAttribute("service");
        Element addresslist = getElement(hdr, ITKNS, "addressList");
        if (addresslist != null) {
            recipients = getActors(addresslist, "address");
        }
        Element auditidentity = getElement(hdr, ITKNS, "auditIdentity");
        if (auditidentity == null) {
            throw new Exception("Element {" + ITKNS + "}:auditIdentity not found");
        }
        authors = getActors(auditidentity, "id");
        Element mfst = getElement(hdr, ITKNS, "manifest");
        if (mfst == null) {
            throw new Exception("Element {" + ITKNS + "}:manifest not found");
        }
        getPayloads(de, mfst);
    }

    private void getPayloads(Element de, Element m)
            throws Exception
    {
        NodeList manifest = m.getElementsByTagNameNS(ITKNS, "manifestItem");
        for (int i = 0; i < manifest.getLength(); i++) {
            Element mi = (Element)manifest.item(i);
            Payload p = null;
            if (mi.getAttribute("metadata").contentEquals("true")) {
                metadata = new MetadataPayload();
                p = metadata;
            } else {
                if (payloads == null) {
                    payloads = new ArrayList<Payload>();
                }
                p = new Payload();
                payloads.add(p);
            }
            String a = null;
            if (mi.getAttribute("mimetype").length() > 0) {
                a = mi.getAttribute("mimetype");
                p.setMimetype(a);                
            }
            a = mi.getAttribute("id");
            p.setId(a);
            a = mi.getAttribute("compressed");
            if (a.contentEquals("true")) {
                p.setCompressed();
            }
            a = mi.getAttribute("base64");
            if (a.contentEquals("true")) {
                p.setBase64();
            }
            a = mi.getAttribute("encrypted");
            if (a.contentEquals("true")) {
                p.setEncrypted();
            }
            getPayloadContent(p, de);
        }
    }

    private void getPayloadContent(Payload p, Element de)
            throws Exception
    {
        Element pset = getElement(de, ITKNS, "payloads");
        if (pset == null) {
            throw new Exception("Element {" + ITKNS + "}:payloads not found");
        }
        NodeList plist = pset.getElementsByTagNameNS(ITKNS, "payload");
        for (int i = 0; i < plist.getLength(); i++) {
            Element e = (Element)plist.item(i);
            String pid = e.getAttribute("id");
            if (pid.length() == 0) {
                throw new Exception("Element {" + ITKNS + "}:payload with no or empty @id");
            }
            if (pid.contentEquals(p.getId())) {
                // See if we have element content. If we don't, treat the whole
                // payload as a single string
                NodeList pcontent = e.getElementsByTagName("*");
                if (pcontent.getLength() == 0) {
                    NodeList content = e.getChildNodes();
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < content.getLength(); j++) {
                        Node n = content.item(j);
                        if (n.getNodeType() == Node.TEXT_NODE) {
                            sb.append(n.getTextContent());
                        }
                    }
                    p.setContent(sb.toString());
                } else {
                    p.setContent((Element)pcontent.item(0));
                }
                return;
            }
        }
        throw new Exception("Manifest item id " + p.getId() + " has no matching payload");
    }

    private ArrayList<Actor> getActors(Element list, String item) 
    {
        NodeList items = list.getElementsByTagNameNS(ITKNS, item);
        if ((items == null) || (items.getLength() == 0)) {
            return null;
        }
        ArrayList<Actor> a = new ArrayList<Actor>();
        for (int i = 0; i < items.getLength(); i++) {
            Element e = (Element)items.item(i);
            String uri = e.getAttribute("uri");
            String type = e.getAttribute("type");
            if ((type == null) || (type.trim().length() == 0)) {
                type = "2.16.840.1.113883.2.1.3.2.4.18.22";
            }
            Actor act = new Actor(type, uri);
            a.add(act);
        }
        return a;
    }

    private Element getElement(Element doc, String ns, String lname) {
        NodeList hdr = doc.getElementsByTagNameNS(ns, lname);
        if ((hdr == null) || (hdr.getLength() == 0)) {
            return null;
        }
        return (Element)hdr.item(0);
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
    public MetadataPayload getMetadata(){ return metadata; }

    @Override
    public void convert(Transmission t)
            throws Exception
    {
        if (t == null) {
            throw new Exception("Invalid source: null transmission given");
        }
        id = t.getId();
        authors = t.getAuthors();
        recipients = t.getRecipients();
        payloads = t.getPayloads();
        metadata = t.getMetadata();

        service = "urn:nhs-itk:ns:201005:SendCDADocument-v2-0";
        
        // The "senders" is set by the adapter, in this case from a property,
        // read the property and create "senders" here.

        String oid = System.getProperty(SENDER_OID);
        String sender = System.getProperty(SENDER_ADDRESS);

        senderAddress = new Actor();
        senderAddress.setType(sender);
        if ((oid != null) && (oid.trim().length() != 0)) {
            senderAddress.setUri(oid);
        }
    }
        
    @Override
    public String serialise()
            throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document d = dbf.newDocumentBuilder().newDocument();
        Element de = d.createElementNS(CfHNamespaceContext.ITK, "DistributionEnvelope");
        Element h = d.createElementNS(CfHNamespaceContext.ITK, "header");
        Element p = d.createElementNS(CfHNamespaceContext.ITK, "payloads");
        de.appendChild(h);
        h.setAttribute("trackingid", id);
        h.setAttribute("service", service);
        Element e1 = null;
        e1 = d.createElementNS(CfHNamespaceContext.ITK, "addressList");
        h.appendChild(e1);
        Element e2 = null;
        for (Actor a : recipients) {
            e2 = d.createElementNS(CfHNamespaceContext.ITK, "address");
            e2.setAttribute("uri", a.getURI());
            if (!a.getType().contentEquals("2.16.840.1.113883.2.1.3.2.4.18.22")) {
                e2.setAttribute("type", a.getType());
            }
            e1.appendChild(e2);
        }
        e1 = d.createElementNS(CfHNamespaceContext.ITK, "auditIdentity");
        h.appendChild(e1);
        for (Actor a : authors) {
            e2 = d.createElementNS(CfHNamespaceContext.ITK, "id");
            e2.setAttribute("uri", a.getURI());
            if (!a.getType().contentEquals("2.16.840.1.113883.2.1.3.2.4.18.27")) {
                e2.setAttribute("type", a.getType());
            }
            e1.appendChild(e2);            
        }
        e1 = d.createElementNS(CfHNamespaceContext.ITK, "manifest");
        h.appendChild(e1);
        
        Element p1 = null;
        if (metadata != null) {
            e1.setAttribute("count", Integer.toString(payloads.size() + 1));
            p.setAttribute("count", Integer.toString(payloads.size() + 1));
            String manifestId = "uuid_" + UUIDGenerator.getInstance().generateRandomBasedUUID().toString().toUpperCase();
            e2 = d.createElementNS(CfHNamespaceContext.ITK, "manifestitem");
            e2.setAttribute("id", manifestId);
            metadata.setId(manifestId);
            e2.setAttribute("metadata", "true");
            e2.setAttribute("mimetype", metadata.getMimetype());
            e1.appendChild(e2);
            p1 = d.createElementNS(CfHNamespaceContext.ITK, "payload");
            p1.setAttribute("id", manifestId);
            Element mp = (Element)d.adoptNode((Element)metadata.getContent());
            p1.appendChild(mp);
            p.appendChild(p1);
        } else {
            e1.setAttribute("count", Integer.toString(payloads.size()));
            p.setAttribute("count", Integer.toString(payloads.size()));
        }
        for (Payload pl : payloads) {
            e2 = d.createElementNS(CfHNamespaceContext.ITK, "manifestitem");
            p1 = d.createElementNS(CfHNamespaceContext.ITK, "payload");
            e2.setAttribute("id", pl.getId());
            p1.setAttribute("id", pl.getId());
            if (pl.hasFileanme()) {
                p1.setAttribute("filename", pl.getFilename());
            }
            e2.setAttribute("mimetype", pl.getMimetype());
            if (pl.isBase64()) {
                e2.setAttribute("base64", "true");
            }
            p1.setTextContent(pl.getContent().toString());
            e1.appendChild(e2);
            p.appendChild(p1);
        }
        e1 = d.createElementNS(CfHNamespaceContext.ITK, "senderAddress");
        h.appendChild(e1);
        e2 = d.createElementNS(CfHNamespaceContext.ITK, "address");
        e2.setAttribute("uri", senderAddress.getURI());
        if (!senderAddress.getType().contentEquals("2.16.840.1.113883.2.1.3.2.4.18.22")) {
            e2.setAttribute("type", senderAddress.getType());
        }
        e1.appendChild(e2);
        de.appendChild(p);

        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        Transformer tx = TransformerFactory.newInstance().newTransformer();
        tx.transform(new DOMSource(de), sr);

        return sw.toString();        
    }

}
