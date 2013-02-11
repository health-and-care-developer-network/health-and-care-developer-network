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


package org.warlock.tk.internalservices.validation.spine;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

 /**
 * Encapsulation of a captured Spine message for validation. Provides parser
 * facilities that handle the various ways such a message may be presented,
 * including base-HL7, the TKW "VALIDATE-AS" mechanism, an ebXML MIME package,
 * or a Spine web-services query.
 * 
 * Once the message is parsed, accessor methods are provided for ebXML headers,
 * SOAP envelopes and HL7 bodies.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class SpineMessage {

    private static final byte[] UTF32BEBOM = {(byte)0x0, (byte)0x0, (byte)0xFE, (byte)0xFF};
    private static final byte[] UTF32LEBOM = {(byte)0xFF, (byte)0xFE, (byte)0x0, (byte)0x0};
    private static final byte[] UTF16BEBOM = {(byte)0xFE, (byte)0xFF};
    private static final byte[] UTF16LEBOM = {(byte)0xFF, (byte)0xFE};
    private static final byte[] UTF8BOM = {(byte)0xEF, (byte)0xBB, (byte)0xBF};

    public static final int NOT_SET = -1;
    public static final int VALIDATE_AS = 0;
    public static final int HTTP_POST = 1;
    public static final int EBXML_MIME = 2;
    public static final int SPINE_SOAP = 3;
    public static final int BARE_HL7 = 4;

    public static final int HTTP_HEADER = 1;
    public static final int EBXML_MIME_HEADER = 2;
    public static final int EBXML_PART = 3;
    public static final int HL7_MIME_HEADER = 4;
    public static final int HL7_PART = 5;
    public static final int SOAP_ENVELOPE = 6;
    public static final int SOAP_BODY = 7;
    
    private String filename = null;
    private String service = null;
    private String presentedFile = null;
    private int presentationType = NOT_SET;

    private HashMap<String, String> httpHeaders = null;
    private Part ebXMLpart = null;
    private Part hl7part = null;
    private ArrayList<Part> attachments = null;
    
    public SpineMessage(String sd, String f)
        throws Exception
    {
        filename = f;
        File valFile = new File(sd, f);
        FileReader fr = new FileReader(valFile);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        boolean firstLine = true;
        String line = null;
        while ((line = br.readLine()) != null) {
            if (firstLine) {
                firstLine = false;
                if (line.startsWith("VALIDATE-AS:")) {
                    service = line.substring("VALIDATE-AS:".length());
                    presentationType = VALIDATE_AS;
                    if ((service == null) || (service.trim().length() == 0)) {
                        throw new Exception("Malformed VALIDATE-AS directive, should be VALIDATE-AS: servicename");
                    }
                    service = service.trim();
                    continue;
                }
            }
            sb.append(line);
            sb.append("\n");
        }
        // Having read the input, we need to determine what we have in terms of
        // structure, and to split it up into the various parts.

        presentedFile = sb.toString().trim();
        setPresentationType();
        parse();
        if (service == null) {
            setService();
        }
        if (ebXMLpart != null) {
            ebXMLpart.setDocument(parseXml(ebXMLpart.getBody()));
        }
        if (hl7part != null) {
            hl7part.setDocument(parseXml(hl7part.getBody()));
        }
    }

    private Document parseXml(String s)
            throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setValidating(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(s);
        InputSource is = new InputSource(sr);
        Document d = db.parse(is);
        return d;
    }

    public Document getEbXmlDoc() { return ebXMLpart.getDocument(); }
    public Document getHL7Doc() { return hl7part.getDocument(); }
    public Document getSOAPDoc() { return hl7part.getDocument(); }
    
    public String getEbXmlPart() {
        if (ebXMLpart == null) {
            return null;
        }
        return stripBOM(ebXMLpart.getBody());
    }

    public String getHL7Part() {
        if (hl7part == null) {
            return null;
        }
        return stripBOM(hl7part.getBody());
    }

    public String getSoap() {
        return stripBOM(getHL7Part());
    }

    private String stripBOM(String l) {
        if ((l == null) || (l.length() == 0))
            return l;

        byte[] b = l.getBytes();
        if (checkBOM(b, UTF8BOM)) {
            return new String(b, UTF8BOM.length, b.length - UTF8BOM.length);
        }
        if (checkBOM(b, UTF16LEBOM)) {
            return new String(b, UTF16LEBOM.length, b.length - UTF16LEBOM.length);
        }
        if (checkBOM(b, UTF16BEBOM)) {
            return new String(b, UTF16BEBOM.length, b.length - UTF16BEBOM.length);
        }
        if (checkBOM(b, UTF32LEBOM)) {
            return new String(b, UTF32LEBOM.length, b.length - UTF32LEBOM.length);
        }
        if (checkBOM(b, UTF32BEBOM)) {
            return new String(b, UTF32BEBOM.length, b.length - UTF32BEBOM.length);
        }
        return l;
    }

    private boolean checkBOM(byte[] t, byte[]b) {
        if (t.length < b.length)
            return false;
        for (int i = 0; i < b.length; i++) {
            if (t[i] != b[i]) {
                return false;
            }
        }
        return true;
    }
    
    private void setService()
        throws Exception
    {
        // By now we should have an hl7Part populated.... Try to pull out the
        // interaction id or allow to fail.
        //
        String h = hl7part.getBody();
        int ext1 = h.indexOf("interactionId");
        ext1 = h.indexOf("extension", ext1);
        ext1 = h.indexOf('"', ext1);
        int ext2 = h.indexOf('"', ext1 + 1);
        service = h.substring(ext1 + 1, ext2);
    }

    private void parse()
            throws Exception
    {
        switch(presentationType) {
            case BARE_HL7:
            case SPINE_SOAP:
                hl7part = new Part();
                hl7part.setBody(presentedFile);
                break;

            case HTTP_POST:
                String boundary = parseHttpHeaders();
                if (boundary == null) {
                    hl7part = new Part();
                    hl7part.setBody(loadSoapBody());
                } else {
                    parseMime(boundary);
                }
                break;

            case VALIDATE_AS:
            case EBXML_MIME:
                parseMime("");
                break;

            case NOT_SET:
            default:
                throw new Exception("Cannot parse unidentified sample type");

        }
    }

    private String loadSoapBody()
            throws Exception
    {
        int ehdr = presentedFile.indexOf("\n\n");
        if (ehdr == -1) {
            throw new Exception("Error reading SOAP message - cannot find end of HTTP header");
        }
        ehdr += 2;
        return presentedFile.substring(ehdr);
    }

    private String parseHttpHeaders()
            throws Exception
    {
        httpHeaders = new HashMap<String, String>();
        StringReader sr = new StringReader(presentedFile);
        BufferedReader br = new BufferedReader(sr);
        String line = null;
        String mimeBoundary = null;
        do {
            line = br.readLine();
            if (line == null) {
                throw new Exception("Unexpected EOF reading HTTP header");
            }
            if (line.trim().length() == 0) {
                break;
            }
            if (!line.startsWith("POST")) {
                int colon = line.indexOf(':');
                String hdr = line.substring(0, colon).toUpperCase();
                String val = line.substring(colon + 1).trim();
                httpHeaders.put(hdr, val);
            }
        } while (true);
        String ctype = httpHeaders.get("CONTENT-TYPE");
        if (ctype == null) {
            throw new Exception("Malformed HTTP headers - no Content-Type found");
        }
        if (ctype.contains("multipart/related")) {
            mimeBoundary = setMimeBoundary(ctype);
        }
        return mimeBoundary;
    }

    private String setMimeBoundary(String ctype)
        throws Exception
    {
        String mb = null;
        int boundary = ctype.indexOf("boundary");
        if (boundary == -1)
            return "";
        boundary += 8; // "boundary".length
        boolean gotBoundary = false;
        int startBoundary = 0;
        int endBoundary = 0;
        while(boundary < ctype.length()) {
            if (startBoundary == 0) {
                if (ctype.charAt(boundary) == '=') {
                    boundary++;
                    if (ctype.charAt(boundary) == '"') {
                        boundary++;
                    }
                    startBoundary = boundary;
                    boundary++;
                    continue;
                }
                throw new Exception("Invalid Content-Type: MIME boundary not properly defined (spaces ?)");
            } else{
                char c = ctype.charAt(boundary);
                switch (c) {
                    case ';':
                    case '"':
                        endBoundary = boundary;
                        break;
                    case ' ':
                        throw new Exception("Invalid Content-Type: MIME boundary not properly defined (spaces ?)");
                    default:
                        break;

                }
            }
            if (endBoundary == 0) {
                boundary++;
            } else {
                break;
            }
        }
        if (endBoundary == 0) {
            mb = ctype.substring(startBoundary);
        } else {
            mb = ctype.substring(startBoundary, endBoundary);
        }
        return mb;
    }

    private void parseMime(String boundary)
        throws Exception
    {
        // If boundary == "" expect to be able to determine the boundary
        // from the first line
        String bnd = null;
        if (boundary.length() == 0) {
            if (!presentedFile.startsWith("--")) {
                throw new Exception("Cannot find MIME boundary");
            }
            bnd = presentedFile.substring(2, presentedFile.indexOf('\n'));
        } else {
            bnd = boundary;
        }
        bnd = "--" + bnd;
        
        // MIME boundary in "bnd", or we've bombed out...
        //
        int startPos = -1;
        int endPos = 0;
        int partCount = 0;
        while((startPos = presentedFile.indexOf(bnd, endPos)) != -1) {
           endPos = startPos + bnd.length();
           switch (partCount) {
               case 0:
                   ebXMLpart = new Part();
                   extractPart(ebXMLpart, bnd, endPos);
                   break;

               case 1:
                   hl7part = new Part();
                   extractPart(hl7part, bnd, endPos);
                   break;

               default:
                   // Catch the case where startPos is actually the beginning of the
                   // closing MIME booundary
                   //
                   if (presentedFile.substring(endPos).startsWith("--")) {
                       break;
                   }
                   if (attachments == null) {
                       attachments = new ArrayList<Part>();
                   }
                   Part p = new Part();
                   extractPart(p, bnd, endPos);
                   attachments.add(p);
                   break;
           }
           partCount++;
        }
    }

    private void extractPart(Part p, String boundary, int pos)
        throws Exception
    {
        // Parse out the MIME part headers, then find the body.
        StringReader sr = new StringReader(presentedFile.substring(pos));
        BufferedReader br = new BufferedReader(sr);
        String line = null;
        boolean subsequentline = false;
        do {
            line = br.readLine();
            if ((line.trim().length() == 0) && subsequentline) {
                break;
            }
            subsequentline = true;
            int colon = line.indexOf(':');
            if (colon != -1) {
                String hdr = line.substring(0, colon).toUpperCase();
                String val = line.substring(colon + 1).trim();
                p.addHeader(hdr, val);
            }
        } while(true);
        StringBuilder sb = new StringBuilder();
        boolean keepgoing = true;
        while (keepgoing) {
            line = br.readLine();
            if (line == null) {
                break;
            }
            if (line.startsWith(boundary)) {
                keepgoing = false;
            } else {
                sb.append(line);
                sb.append("\n");
            }
        }
        p.setBody(sb.toString());
    }

    public String getService() { return service; }

    private void setPresentationType()
        throws Exception
    {
        // We might already know...
        if (presentationType == VALIDATE_AS) {
            return;
        }
        // Otherwise analyse the presentedFile and make a guess:
        //
        // 1. If the first line contains POST and HTTP/1.1, assume it is HTTP_POST

        int nl = presentedFile.indexOf('\n');
        if (nl != -1) {
            String firstLine = presentedFile.substring(0, nl);
            if (firstLine.contains("POST") && firstLine.contains("HTTP/1.1")) {
                presentationType = HTTP_POST;
                return;
            }
        }
        // 2. If "PartyId" and "CPAId" are both present, assume EBXML_MIME

        if ((presentedFile.indexOf("PartyId") != -1) && (presentedFile.indexOf("CPAId") != -1)) {
            presentationType = EBXML_MIME;
            return;
        }

        // 3. If "Header" and "ReplyTo" are both present, assume SPINE_SOAP

        if ((presentedFile.indexOf("Header") != -1) && (presentedFile.indexOf("ReplyTo") != -1)) {
            presentationType = SPINE_SOAP;
            return;
        }

        // 4. If "versionCode" and "interactionId" are both present, assume BARE_HL7

        if ((presentedFile.indexOf("versionCode") != -1) && (presentedFile.indexOf("interactionId") != -1)) {
            presentationType = BARE_HL7;
            return;
        }

        // 5. Throw an exception that we can't decide what sort of file it is.
        throw new Exception("Spine validation: SpineMessage: " + filename + " - cannot recognise file type");

    }
}
