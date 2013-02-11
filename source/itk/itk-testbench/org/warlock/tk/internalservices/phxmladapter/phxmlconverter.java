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
package org.warlock.tk.internalservices.phxmladapter;
 /**
 * Singleton class to convert HL7v2 pipe-and-hat and XML forms bi-directionally. 
 * Boots from the phxmlmap.xml and v2datatypes.xml files in this package.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
import java.io.InputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.HashMap;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.warlock.util.Logger;
import java.util.StringTokenizer;
import java.io.StringReader;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class phxmlconverter {

    private static final String HL7V2 = "urn:hl7-org:v2xml";

    private BufferedReader infile = null;
    private StringWriter outfile = null;
    private HashMap<String, SegmentDefinition>phxmlmap = new HashMap<String, SegmentDefinition>();
    private HashMap<String, Type>typemap = new HashMap<String, Type>();
    private String messageType = null;

    private HashMap<String, String[]> clusterMap = new HashMap<String, String[]>();

    private String outputNamespace = null;

    private static final String TRAILINGHATS = "\\^+\\|";
    private static final String TRAILINGSUBFIELDTILDES = "~+\\^";
    private static final String TRAILINGFIELDTILDES = "~+\\|";
    private Pattern stripTrailingHats = null;
    private Pattern stripTrailingFieldTildes = null;
    private Pattern stripTrailingSubFieldTildes = null;

    private static phxmlconverter me = new phxmlconverter();
    private static Exception bootException = null;

    private phxmlconverter()
    {
        try {
            initMap();
            initDataTypes();
            loadClusterMap();
            if (System.getProperty("tks.phxmlconverter.outputnamespace") != null) {
                outputNamespace = System.getProperty("tks.phxmlconverter.outputnamespace");
            } else {
                outputNamespace = HL7V2;
            }
            if (System.getProperty("tks.phxmlconverter.clustermap") == null) {
                Logger.getInstance().log("phxmlconverter - constructor", "tks.phxmlconverter.clustermap not set - no repeat clusters");
            } else {
                loadClusterMap();
            }
            stripTrailingHats = Pattern.compile(TRAILINGHATS);
            stripTrailingSubFieldTildes = Pattern.compile(TRAILINGSUBFIELDTILDES);
            stripTrailingFieldTildes = Pattern.compile(TRAILINGFIELDTILDES);
        }
        catch (Exception e) {
            bootException = e;
        }
    }

    private String getClusterStart(String root) {
        if (!clusterMap.containsKey(root)) {
            return null;
        }
        return (clusterMap.get(root))[0];
    }

    private String getClusterEnd(String root) {
        if (!clusterMap.containsKey(root)) {
            return null;
        }
        return (clusterMap.get(root))[1];
    }


    private void loadClusterMap() {
        String fname = System.getProperty("tks.phxmlconverter.clustermap");
        if ((fname == null) || (fname.trim().length() == 0)) {
            return;
        }
        try {
            FileReader f = new FileReader(fname);
            BufferedReader br = new BufferedReader(f);
            String line = null;
            while((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                String root = st.nextToken();
                String[] cterm = new String[2];
                cterm[0] = st.nextToken();
                if (!st.hasMoreTokens()) {
                    cterm[1] = null;
                } else {
                    String s = st.nextToken().trim();
                    if (s.length() == 0) {
                        cterm[1] = null;
                    } else {
                        cterm[1] = s;
                    }
                }
                clusterMap.put(root, cterm);
            }
        }
        catch (Exception e) {
            Logger.getInstance().log("phxmlconverter.loadClusterMap()", e.toString());
        }
    }

    public static phxmlconverter getInstance()
            throws Exception
    {
        if (bootException != null) {
            throw bootException;
        }
        return me;
    }

    /**
     * Convert the HL7v2 XML with a root element of x, into pipe-and-hat form.
     * @param x Root element of HL7v2 XML message
     * @return String containing pipe-and-hat form of the message.
     * @throws Exception 
     */
    public String convert(Element x)
            throws Exception
    {
        StringBuilder sb = new StringBuilder();

        Element adtRoot = null;
        NodeList arChildren = x.getChildNodes();
        for (int i = 0; i < arChildren.getLength(); i++) {
            if (arChildren.item(i).getNodeType() == Node.ELEMENT_NODE) {
                adtRoot = (Element)arChildren.item(i);
                break;
            }
        }

        NodeList segments = adtRoot.getChildNodes();
        for (int i = 0; i < segments.getLength(); i++) {
            Node n = segments.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if (n.getLocalName().contains(".QUERY_RESPONSE")) {
                    NodeList rptSegments = ((Element)n).getChildNodes();
                    for (int j = 0; j < rptSegments.getLength(); j++) {
                        if (rptSegments.item(j).getNodeType() == Node.ELEMENT_NODE){
                            convertSegment(sb, (Element)rptSegments.item(j));
                        }
                    }
                } else {
                    convertSegment(sb, (Element)n);
                }
            }
        }
        String s = sb.toString();

        Matcher sft = stripTrailingSubFieldTildes.matcher(s);
        s = sft.replaceAll("^");
        sft = stripTrailingFieldTildes.matcher(s);
        s = sft.replaceAll("|");
        sft = stripTrailingHats.matcher(s);
        s = sft.replaceAll("|");

        x.removeChild(adtRoot);
        return s.trim();
    }

    private void convertSegment(StringBuilder sb, Element seg)
            throws Exception
    {
        String sName = seg.getLocalName();
        sb.append(sName);
        sb.append("|");
        SegmentDefinition sd = getSegmentDefinition(sName);
        NodeList nl = seg.getChildNodes();
        int f = 0;
        Iterator<FieldDefinition>fields = sd.getFields();
        while (fields.hasNext()) {
            FieldDefinition fd = fields.next();
            // Make an entry for each... check to see if there is a corresponding XML element
            // for this, and emit an empty field if not. Otherwise, dig the data out of the
            // XML
            Element fieldElement = getElementInImmediateChildren(seg, fd.getName());
            if (fieldElement != null) {
                Iterator<Part> parts = fd.getParts();
                if (!parts.hasNext()) {
                    sb.append(fieldElement.getTextContent().trim());
                } else {
                    if (fd.isMultiPart()) {
                        // Each of the parts has type-dependent subtypes
                        while (parts.hasNext()) {
                            Element partElement = getElementInImmediateChildren(fieldElement, parts.next().getName());
                            Type t = typemap.get(fd.getType());
                            if (t.subTyped()) {
                                Iterator<String> ts = t.getParts();
                                while (ts.hasNext()) {
                                    String s = ts.next();
                                    Element subtypeElement = getElementInImmediateChildren(partElement, s);
                                    if (subtypeElement != null) {
                                        sb.append(subtypeElement.getTextContent().trim());
                                    }
                                    if (ts.hasNext()) {
                                        sb.append("~");
                                    }
                                }
                            } else {
                                sb.append(partElement.getTextContent());
                            }
                            if (parts.hasNext()) {
                                sb.append("^");
                            }
                        }
                    } else {
                        // Each of the parts has a simple, single-value type
                        while (parts.hasNext()) {
                            Element partElement = getElementInImmediateChildren(fieldElement, parts.next().getName());
                            if (partElement != null) {
                                sb.append(partElement.getTextContent().trim());
                            }
                            if (parts.hasNext()) {
                                sb.append("^");
                            }
                        }
                    }
                }
            }
            if (fields.hasNext()) {
                sb.append("|");
            } else {
                sb.append("\r\n");
            }
        }

    }

    private Element getElementInImmediateChildren(Node n, String s) {
        NodeList nl = n.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                if (s.contentEquals(nl.item(i).getLocalName())) {
                    return(Element)nl.item(i);
                }
            }
        }
        return null;
    }

    /**
     * Convert HL7v2 pipe-and-hat message of type "mtype", to XML.
     * 
     * @param mtype HL7v2 message type (structure type)
     * @param msg Pipe-and-hat message
     * @return String serialisation of HL7v2 XML form of the message
     * @throws Exception 
     */
    public String convert(String mtype, String msg)
        throws Exception
    {
        String clusterStartSegment = getClusterStart(mtype);
        String clusterEndSegment = getClusterEnd(mtype);

        messageType = mtype;
        try {
            infile = new BufferedReader(new StringReader(msg));
        }
        catch (Exception e) {
            throw new Exception("Cannot open message for reading: " + e.getMessage());
        }
        outfile = new StringWriter();
        
        String line = null;

        String[] pipeFields = null;
        int fieldNum = 0;

        // Note: Store in an array first because we don't know what the type is till we read it.
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document outDoc = db.newDocument();
        Element msgRoot = outDoc.createElementNS(outputNamespace, messageType);
        msgRoot.setPrefix("hl7v2");
        outDoc.appendChild(msgRoot);
        boolean inCluster = false;
        boolean endClusterSegmentFound = false;
        Element segmentParent = msgRoot;
        while((line = infile.readLine()) != null) {
            pipeFields = line.split("\\|");

            String sName = pipeFields[0];
            if (clusterStartSegment != null) {
                if (sName.contentEquals(clusterStartSegment)) {
                    segmentParent = outDoc.createElementNS(outputNamespace, messageType + ".QUERY_RESPONSE");
                    segmentParent.setPrefix("hl7v2");
                    msgRoot.appendChild(segmentParent);
                }
            }
            if (clusterEndSegment != null) {
                if (sName.contentEquals(clusterEndSegment)) {
                    segmentParent = msgRoot;
                }
            }
            if (sName.trim().length() == 0) {
                continue;
            }
            Element segmentElement = outDoc.createElementNS(outputNamespace, sName);
            segmentElement.setPrefix("hl7v2");
            segmentParent.appendChild(segmentElement);
            SegmentDefinition mapElement = getSegmentDefinition(sName);
            if (mapElement == null) {
                Logger.getInstance().log("phxmlconverter.conver()", "ERROR: No definition for segment " + sName + " - data will NOT be included in the XML output. This message FAILS ITK ACCREDITATION unless a good explanation is given for the inclusion of " + sName + " in the submitted message");
                continue;
            }

            if (sName.contentEquals("MSH")) {
                FieldDefinition fd = mapElement.getField(fieldNum);
                Element fieldElement = null;
                if (fd.getNamespace() == null) {
                    fieldElement = outDoc.createElementNS(outputNamespace, fd.getName());
                } else {
                    fieldElement = outDoc.createElementNS(fd.getNamespace(), fd.getName());
                }
                fieldElement.setPrefix("hl7v2");
                fieldElement.setTextContent("|");
                segmentElement.appendChild(fieldElement);
                fieldNum = 1;
            } else {
                fieldNum = 0;
            }
            int pipeCount = 1;
            while (pipeCount < pipeFields.length) {
                String pfield = pipeFields[pipeCount];
                if (mapElement.isVariableIndicatorField(fieldNum)) {
                    mapElement.setVariableFieldType(pfield);
                }
                FieldDefinition fd = mapElement.getField(fieldNum);
                if (fd == null) {
                    Logger.getInstance().log("phxmlconverter.convert()", "WARNING: More fields given than supported for segment " + sName);
                    break;
                }
                if (pfield.length() > 0) {
                    if (pfield.contains("~") && !fd.getName().contentEquals("MSH.2")) {
                        String[] repeatingfield = pfield.split("\\~");
                        for (String rp : repeatingfield) {
                            Element fieldElement = null;
                            if (fd.getNamespace() == null) {
                                fieldElement = outDoc.createElementNS(outputNamespace, fd.getName());
                            } else {
                                fieldElement = outDoc.createElementNS(fd.getNamespace(), fd.getName());
                            }
                            fieldElement.setPrefix("hl7v2");
                            segmentElement.appendChild(fieldElement);
                            makeField(outDoc, fd, rp, fieldElement);
                        }
                    } else {
                        Element fieldElement = null;
                        if (fd.getNamespace() == null) {
                            fieldElement = outDoc.createElementNS(outputNamespace, fd.getName());
                        } else {
                            fieldElement = outDoc.createElementNS(fd.getNamespace(), fd.getName());
                        }
                        fieldElement.setPrefix("hl7v2");
                        segmentElement.appendChild(fieldElement);
                        makeField(outDoc, fd, pfield, fieldElement);
                    }
                }
                fieldNum++;
                pipeCount++;
            }
        }

        StreamResult sr = new StreamResult(outfile);
        Transformer tx = TransformerFactory.newInstance().newTransformer();
        tx.transform(new DOMSource(outDoc), sr);
        return outfile.toString();
    }

    private void initDataTypes()
            throws Exception
    {
        Document typesdoc = null;
        try {
            InputStream is = getClass().getResourceAsStream("v2datatypes.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setIgnoringComments(true);
            typesdoc = dbf.newDocumentBuilder().parse(is);
            NodeList nl = typesdoc.getElementsByTagName("v2type");
            for (int i = 0; i < nl.getLength(); i++) {
                Element el = (Element)nl.item(i);
                String t = el.getAttribute("name");
                Type type = new Type(t);
                typemap.put(t, type);
                NodeList nlp = el.getElementsByTagName("part");
                for (int j = 0; j < nlp.getLength(); j++) {
                    Element p = (Element)nlp.item(j);
                    type.addPart(p.getAttribute("type"));
                }
            }
        }
        catch (Exception e) {
            throw new Exception("Error reading internal data types: " + e.getMessage());
        }
    }

    private void getFieldMap(FieldDefinition d, Element f) {
        NodeList nl = f.getElementsByTagName("part");
        for (int i = 0; i < nl.getLength(); i++) {
            Element p = (Element)nl.item(i);
            String n = p.getAttribute("name");
            String t = p.getAttribute("type");
            if (p.getAttributeNode("namespace") != null) {
                String ns = p.getAttribute("namespace");
                d.addPart(n, t, ns);
            } else {
                d.addPart(n, t);
            }
        }
    }

    private void getSegmentMap(SegmentDefinition d, Element s) {
        NodeList nl = s.getElementsByTagName("field");
        for (int i = 0; i < nl.getLength(); i++) {
            Element f = (Element)nl.item(i);
            String n = f.getAttribute("name");
            String t = f.getAttribute("type");
            FieldDefinition fd = new FieldDefinition(n, t);
            if (f.getAttributeNode("namespace") != null) {
                String ns = f.getAttribute("namespace");
                fd.setNamespace(ns);
            }
            d.addField(fd);
            getFieldMap(fd, f);
        }
    }

    private void initMap()
            throws Exception
    {
        Document phxmlmapdoc = null;
        try {
            InputStream is = getClass().getResourceAsStream("phxmlmap.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setIgnoringComments(true);
            phxmlmapdoc = dbf.newDocumentBuilder().parse(is);
            NodeList nl = phxmlmapdoc.getElementsByTagName("segment");
            for (int i = 0; i < nl.getLength(); i++) {
                Element seg = (Element)nl.item(i);
                String n = seg.getAttribute("name");
                SegmentDefinition sd = new SegmentDefinition(n);
                phxmlmap.put(n, sd);
                getSegmentMap(sd, seg);
            }
        }
        catch (Exception e) {
            throw new Exception("Error reading internal map: " + e.getMessage());
        }
    }

    public void convert()
            throws Exception
    {
    }

    private void makeField(Document doc, FieldDefinition fd, String field, Element fieldElement) {

        String[] hatFields = field.split("\\^");
        int subfieldCount = 0;
        while (subfieldCount < hatFields.length) {
            Part p = fd.getPart(subfieldCount);
            if (p == null) {
                if (fd.fieldCount() == 0) {
                    fieldElement.setTextContent(field);
                    return;
                } else {
                    Logger.getInstance().log("phxmlconverter.makeField()", "WARNING: More hat-delimited parts given than supported for " + fd.getName());
                    return;
                }
            }
            String hpart = hatFields[subfieldCount];
            if (hpart.length() > 0) {
                    // Check the type of "p" and see if there are any sub-fields. If there are
                    // they will be delimited by "&". If the sub-field *itself* has multiple
                    // parts, then only the first will be used (because v2 doesn't have a sub-sub-
                    // delimiter.
                    Type t = typemap.get(p.getType());
                    if (t == null) {
                        Logger.getInstance().log("phxmlconverter.makeField()","DEBUG: No type: " + p.getType() + " : " + p.getName());
                    }
                    int c = t.getPartCount();
                    if (c == 0) {
                        Element partElement = null;
                        if (fd.getNamespace() != null) {
                            partElement = doc.createElementNS(fd.getNamespace(), p.getName());
                        } else {
                            if (p.getNamespace() != null) {
                                partElement = doc.createElementNS(p.getNamespace(), p.getName());
                            } else {
                                partElement = doc.createElementNS(outputNamespace, p.getName());
                            }
                        }
                        partElement.setPrefix("hl7v2");
                        partElement.setTextContent(hpart);
                        fieldElement.appendChild(partElement);
                    } else {
                        Element partElement = null;
                        String localNamespace = null;
                        if (fd.getNamespace() != null) {
                            localNamespace = fd.getNamespace();
                        } else {
                            if (p.getNamespace() != null) {
                                localNamespace = p.getNamespace();
                            } else {
                                localNamespace = outputNamespace;
                            }
                        }
                        partElement = doc.createElementNS(localNamespace, p.getName());
                        partElement.setPrefix("hl7v2");
                        if (hpart.contains("&")) {
                            String[] subfield = hpart.split("&");
                            for (int i = 0; i < subfield.length; i++) {
                                if (subfield[i].length() > 0) {
                                    Element typeElement = doc.createElementNS(localNamespace, t.getName() + "." + Integer.toString(i + 1));
                                    typeElement.setTextContent(subfield[i]);
                                    partElement.appendChild(typeElement);
                                }
                            }
                        } else {
                            Element typeElement = doc.createElementNS(localNamespace, t.getName() + ".1");
                            String subtype = t.getPartType(0);
                            Type st = typemap.get(subtype);
                            if (st.getPartCount() > 0) {
                                Element subTypeElement = doc.createElementNS(localNamespace, subtype + ".1");
                                typeElement.appendChild(subTypeElement);
                                subTypeElement.setPrefix("hl7v2");
                                subTypeElement.setTextContent(hpart);
                                partElement.appendChild(typeElement);
                            } else {
                                typeElement.setTextContent(hpart);
                            }
                        }
                        fieldElement.appendChild(partElement);
                    }
            }
            subfieldCount++;
        }
    }
    
    private SegmentDefinition getSegmentDefinition(String s) {
        // Find the description in phxmlmap - BEAR IN MIND that in the first instance
        // this is a simple search, but it MIGHT NOT BE if there are nested segments.

        return phxmlmap.get(s);
    }

    // Usage: msgtype infile outfile [cluster-start-segment] [stop-cluster-segment]
//    public static void main(String[] args) {
//        try {
//            phxmlconverter p = new phxmlconverter(args[0], args[1], args[2]);
//            //
//            // Do this on the command line for now. Might include something more
//            // clever later
//            //
//            if (args.length > 3) {
//                p.setClusterStartSegment(args[3]);
//            }
//            if (args.length > 4) {
//                p.setClusterEndSegment(args[4]);
//            }
//            p.convert();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Error converting: " + e.getMessage());
//        }
//    }

}
