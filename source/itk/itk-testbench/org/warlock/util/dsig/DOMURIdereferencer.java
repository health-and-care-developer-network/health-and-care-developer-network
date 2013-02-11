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

package org.warlock.util.dsig;
import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import java.io.ByteArrayInputStream;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
 /**
 * Support for digital signatures and verification. De-reference a URI in the
 * current context where the context can be expressed as a DOM.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class DOMURIdereferencer
    implements javax.xml.crypto.URIDereferencer
{
    private static final String SHOWREFERENCE = "dereferencer.showreference";
    private static final int PILENGTH = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").length();
    private static String showReference = System.getProperty(SHOWREFERENCE);

    @Override
    public Data dereference(URIReference uri, XMLCryptoContext context)
            throws URIReferenceException
    {
        DOMSignContext dsc = null;
        DOMValidateContext dvc = null;
        try {
            dsc = (DOMSignContext)context;
            return lookup(uri, dsc.getParent());
        }
        catch (ClassCastException eds) {
            try {
                dvc = (DOMValidateContext)context;
                return lookup(uri, dvc.getNode());
            }
            catch (ClassCastException edv) {
                throw new URIReferenceException("Cannot find dereferencer for context with base: " + context.getBaseURI() + "and default namespace: " + context.getDefaultNamespacePrefix());
            }
        }
    }

    private Data lookup(URIReference uri, Node n)
            throws URIReferenceException
    {
        // Get a reference to the parent document
        Node docElem = n.getOwnerDocument().getDocumentElement();
        if (docElem == null) {
            throw new URIReferenceException("No document element");
        }
        // And the reference URI, minus the leading "#"
        String ref = uri.getURI().substring(1);
        // And then see if we can find a node that has an Id attribute matching
        // the reference. It is an error if we can't.
        Node nres = search(n.getOwnerDocument().getDocumentElement(), ref);
        if (nres == null) {
            throw new URIReferenceException("ID " + ref + " not found in document");
        }
        // Strip off the parent nodes, so we don't get their context that isn't used in the
        // timestamp structure
        //
        nres = nres.cloneNode(true);
        // Now make the output as an octet stream, strip off the PI that the transform adds.
        try {
            java.io.StringWriter sw = new java.io.StringWriter();
            javax.xml.transform.stream.StreamResult sr = new javax.xml.transform.stream.StreamResult(sw);
            javax.xml.transform.Transformer tx = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
            tx.transform(new javax.xml.transform.dom.DOMSource(nres), sr);
            if (showReference != null) {
                System.err.println("Reference: ");
                System.err.println(sw.toString().substring(PILENGTH));
            }
            OctetStreamData oct = new OctetStreamData(new ByteArrayInputStream(sw.toString().substring(PILENGTH).getBytes()));
            return oct;
        }
        catch (Exception e) {
            return new NodeSetDataResult();
        }

// NodeSetDataResult doesn't work with the stock JDK code... which is why we make the
// OctetStreamData above.
//
//        NodeSetDataResult nsdr = new NodeSetDataResult();
//        nsdr.put(nres);
//        if (nres.hasAttributes()) {
//            addAttributes(nsdr, nres);
//        }
//        buildNodeSet(nsdr, nres);
//        return nsdr;
    }

    private void addAttributes(NodeSetDataResult r, Node n) {
        // Add the attributes from the given node, to the NodeSetDataResult, in the
        // Canonical XML order (default ns, ns in lexical uri order, attrib in lexical
        // ns uri then lexical name order)
        NamedNodeMap attribs = n.getAttributes();
        Attr l[] = new Attr[attribs.getLength()];
        for (int i = 0; i < attribs.getLength(); i++) {
            l[i] = (Attr)attribs.item(i);
        }
        java.util.Arrays.sort(l, new org.warlock.util.dsig.CanonicalAttributeComparator());
        for (int i = 0; i < l.length; i++) {
            r.put(l[i]);
        }
    }

    private void buildNodeSet(NodeSetDataResult r, Node n) {
        NodeList nl = n.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node k = nl.item(i);
            r.put(k);
            if (k.hasAttributes()) {
                addAttributes(r, k);
            }
            if (k.hasChildNodes()) {
                buildNodeSet(r, k);
            }
        }
    }
        
    private Node search(Node doc, String id)
            throws URIReferenceException
    {
        NodeList nl = doc.getChildNodes();
        Node nres = null;

        for (int i = 0 ; i < nl.getLength(); i++) {
            Node ncurrent = nl.item(i);
            if (ncurrent.getNodeType() == Node.ELEMENT_NODE) {
                if (ncurrent.hasAttributes()) {
                    NamedNodeMap attribs = ncurrent.getAttributes();
                    for (int j = 0; j < attribs.getLength(); j++) {
                        Attr a = (Attr)attribs.item(j);
                        if (a.getLocalName().contentEquals("Id")) {
                            String ans = a.getNamespaceURI();
                            if ((ans != null) && ans.contentEquals("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd")) {
                                if (a.getValue().contentEquals(id)) {
                                    return nl.item(i);
                                }
                            }
                        }
                    }
                }
                nres = search(ncurrent, id);
                if (nres != null) {
                    return nres;
                }
            }
        }
        return nres;
    }
}
