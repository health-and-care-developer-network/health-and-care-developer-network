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

package org.warlock.tk.internalservices;
import org.safehaus.uuid.UUIDGenerator;
import java.util.Collections;
import java.util.Properties;
import java.util.HashMap;
import java.util.ArrayList;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.boot.ToolkitSimulator;
import org.warlock.util.CfHNamespaceContext;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.io.FileInputStream;
import org.xml.sax.InputSource;
import java.io.CharArrayReader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dom.DOMStructure;
import java.io.StringWriter;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.warlock.util.dsig.SimpleKeySelector;
import org.apache.commons.codec.binary.Base64;
import java.io.ByteArrayInputStream;
import org.warlock.util.dsig.DOMURIdereferencer;
/**
 * Service handler for signing and verifying ITK WS-Sec SOAP header signatures.
 * 
 *     public ServiceResponse execute(Object param) does nothing
 *     public ServiceResponse execute(String type, Object param) does nothing
 *     public ServiceResponse execute(String type, String param) signs when type == "sign" otherwise verifies.
 *
 * In either case, param contains the serialises XML as a string.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class SignerService
    implements org.warlock.tk.boot.ToolkitService
{
    private static final String PASSPROPERTY = "tks.signer.storepassword";
    private static final String KEYPASS = "tks.signer.keypassword";
    private static final String STOREFILE = "tks.signer.keystore";
    private static final String KEYALIAS = "tks.signer.keyalias";
    private static final String ALWAYSACCEPT = "tks.signer.alwaysacceptsignature";
    private static final String SHOWRESOLVEDREFERENCE = "tks.signer.showreference";
    private static final String SHOWREFERENCE = "dereferencer.showreference";
    private static final String MUSTUNDERSTANDSECURITY = "tks.signer.mustunderstandsecurity";

    
    private static final String DIGESTALGORITHM = "tks.signer.digestalgorithm";
    private static final String[] ALGORITHMS = {"SHA-1", "SHA-256", "SHA-512"};
    private static final int SHA1 = 0;
    private static final int SHA256 = 1;
    private static final int SHA512 = 2;
    
    private int digestAlgorithm = SHA1;
    
    private static final int B64BUFFERSIZE = 1000;
    private static final int B64CHUNKSIZE = 64;
    private static final int KEYIDMAXSIZE = 1024;

    private static HashMap<String,String> namespaces = null;
    private static XPathFilterParameterSpec TIMESTAMPXPATH = null;
    private boolean alwaysAccept = false;
    private boolean showReference = false;

    static {
        namespaces = new HashMap<String,String>();
        namespaces.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        namespaces.put("dsig", "http://www.w3.org/2000/09/xmldsig#");
        namespaces.put("wsa", "http://schemas.xmlsoap.org/ws/2004/08/addressing");
        namespaces.put("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        namespaces.put("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
        namespaces.put("wss", "http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd");
        TIMESTAMPXPATH = new XPathFilterParameterSpec("//wsu:Timestamp", namespaces);
    }


    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;
    private HashMap<String,String> table = null;
    private KeyStore keystore = null;
    private X509Certificate certificate = null;
    private String storePass = null;
    private String keyalias = null;

    public SignerService() {}

    @Override
    public Properties getBootProperties() { return bootProperties; }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception
    {
        bootProperties = p;
        serviceName = s;
        simulator = t;

        String ref = bootProperties.getProperty(SHOWRESOLVEDREFERENCE);
        if ((ref != null) && (ref.toLowerCase().startsWith("y"))) {
            showReference = true;
            System.setProperty(SHOWREFERENCE, "Y");
        }
        String pass = bootProperties.getProperty(PASSPROPERTY);
        if (pass == null) {
            pass = "";
        }
        String keypass = bootProperties.getProperty(KEYPASS);
        if (keypass == null) {
            keypass = "";
        }
        String kf = bootProperties.getProperty(STOREFILE);
        if (kf == null) {
            throw new Exception("No keystore filename property " + STOREFILE + " in properties file");
        }
        keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream(kf), pass.toCharArray());
        String alias = bootProperties.getProperty(KEYALIAS);
        if (alias == null) {
            throw new Exception("No key alias property " + KEYALIAS + " in properties file");
        }
        KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry)keystore.getEntry(alias, new KeyStore.PasswordProtection(keypass.toCharArray()));
        if (keyEntry == null) {
            throw new Exception("Failed to initialise Signer service: Key alias " + alias + " not found in keystore " + kf);
        }
        certificate = (X509Certificate)keyEntry.getCertificate();

        String aas = bootProperties.getProperty(ALWAYSACCEPT);
        if ((aas != null) && (aas.toUpperCase().startsWith("Y"))) {
            alwaysAccept = true;
        }
        String da = bootProperties.getProperty(DIGESTALGORITHM);
        if ((da != null) && (da.trim().length() != 0)) {
            for (int i = 0; i < ALGORITHMS.length; i++) {
                if (da.toUpperCase().contentEquals(ALGORITHMS[i])) {
                    digestAlgorithm = i;
                    break;
                }
            }
            System.out.println("Signer using Digest Algorithm: " + ALGORITHMS[digestAlgorithm]);        
        }
    }

    @Override
    public ServiceResponse execute(Object param)
            throws Exception
    {
        return new ServiceResponse(0, null);
    }
 
    @Override
    public ServiceResponse execute(String type, String param)
            throws Exception
    {
        if (type.contentEquals("sign")) {
            // This should contain a complete SOAP message body. Sign it.
            try {
                return new ServiceResponse(0, sign(param));
            }
            catch (Exception e) {
                throw new Exception("Exception caught trying to sign :" + e.getMessage());
            }
        } else{
            // When we *verify* we get a DOM Node that contains a SOAP header
            //
            try {
                return new ServiceResponse(((verify(param)) ? 1 : 0), null);
            }
            catch (Exception e) {
                throw new Exception("Exception caught trying to verify :" + e.getMessage());
            }
        }
    }

    @Override
    public ServiceResponse execute(String type, Object param)
            throws Exception
    {
        return new ServiceResponse(0, null);
    }

    private Element getElementByNameNS(Node elem, String nsuri, String lname) {
        Element found = null;
        if (elem == null) {
            return null;
        }
        org.w3c.dom.NodeList nl = elem.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if (n.getLocalName().contentEquals(lname)) {
                    if ((nsuri == null) && (n.getNamespaceURI() == null)) {
                        return (Element)n;
                    } else {
                        if ((nsuri != null) && (n.getNamespaceURI().contentEquals(nsuri))) {
                            return (Element)n;
                        }
                    }
                }
                Element m = getElementByNameNS((Element)n, nsuri, lname);
                if (m != null) {
                    return m;
                }
            }
        }
        return found;
    }

    private String sign(String s)
        throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);
        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new CharArrayReader(s.toCharArray())));
        Node n = doc.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security").item(0);
        XMLSignatureFactory xsf = XMLSignatureFactory.getInstance("DOM");
        String tsid = UUIDGenerator.getInstance().generateTimeBasedUUID().toString().toUpperCase();
        Transform t = xsf.newTransform("http://www.w3.org/2001/10/xml-exc-c14n#" , (TransformParameterSpec)null);
        Reference ref = null;
        switch (digestAlgorithm) {
            case SHA1:
                ref = xsf.newReference("#" + tsid, xsf.newDigestMethod(DigestMethod.SHA1, null), Collections.singletonList(t), null, null);
                break;
            case SHA256:
                ref = xsf.newReference("#" + tsid, xsf.newDigestMethod(DigestMethod.SHA256, null), Collections.singletonList(t), null, null);
                break;
            case SHA512:
                ref = xsf.newReference("#" + tsid, xsf.newDigestMethod(DigestMethod.SHA512, null), Collections.singletonList(t), null, null);
        }
        
        Element ts = (Element)doc.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Timestamp").item(0);
        ts.setAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu:Id", tsid);
        Node un = doc.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "UsernameToken").item(0);

        CanonicalizationMethod cm = xsf.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec)null);
        SignedInfo si = xsf.newSignedInfo(cm, xsf.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));
        KeyInfoFactory kif = xsf.getKeyInfoFactory();
        ArrayList<Object> x509content = new ArrayList<Object>();
        x509content.add(certificate.getSubjectX500Principal().getName());
        x509content.add(certificate);
        X509Data xd = kif.newX509Data(x509content);

        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd), UUIDGenerator.getInstance().generateTimeBasedUUID().toString());

        String alias = bootProperties.getProperty(KEYALIAS);
        if (alias == null) {
            throw new Exception("No key alias property " + KEYALIAS + " in properties file");
        }
        String keypass = bootProperties.getProperty(KEYPASS);
        if (keypass == null) {
            keypass = "";
        }
        KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry)keystore.getEntry(alias, new KeyStore.PasswordProtection(keypass.toCharArray()));
        DOMSignContext dsc = new DOMSignContext(keyEntry.getPrivateKey(), n);
        dsc.setURIDereferencer(new DOMURIdereferencer());
        XMLSignature xs = xsf.newXMLSignature(si, ki);
        xs.sign(dsc);

        Element newSecurity = doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
        String mus = bootProperties.getProperty(MUSTUNDERSTANDSECURITY);
        if ((mus != null) && (mus.toLowerCase().startsWith("y"))) {
            newSecurity.setAttributeNS(CfHNamespaceContext.SOAPENVNAMESPACE, "soap:mustUnderstand", "1");
        }
        newSecurity.appendChild(ts);
        newSecurity.appendChild(un);
        Element bst = doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "BinarySecurityToken");
        bst.setAttribute("EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
        bst.setAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
        bst.setAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu:Id", ki.getId());
        StringWriter certStringWriter = new StringWriter();
        Base64 base64cert = new Base64();
        byte b64cert[] = null;
        byte certbytes[] = certificate.getEncoded();
        b64cert = base64cert.encode(certbytes);
        writeBase64(certStringWriter, b64cert);
        bst.setTextContent(certStringWriter.toString());
        newSecurity.appendChild(bst);
        Element sig = (Element)doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        Element signedInfo = (Element)doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignedInfo").item(0);
        Element sigValue  = (Element)doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "SignatureValue").item(0);
        sig.appendChild(signedInfo);
        sig.appendChild(sigValue);

        Element keyRef = (Element)doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo");
        Element krsc = (Element)doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "SecurityTokenReference");
        Element kr = (Element)doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Reference");
        kr.setAttribute("URI", "#" + ki.getId());
        //
        // DJM: 20111205 (Tested OK ADT+Correspondence 20111206 and released)
        // Added "ValueType" attribute to Reference, as per 
        // http://www.ws-i.org/Profiles/BasicSecurityProfile-1.1.html#SecurityTokenReference_to_X.509_Token
        //
        kr.setAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
        krsc.appendChild(kr);
        keyRef.appendChild(krsc);
        sig.appendChild(keyRef);
        newSecurity.appendChild(sig);
        Node soapHeader = doc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Header").item(0);
        soapHeader.replaceChild(newSecurity, n);
        
        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        Transformer tx = TransformerFactory.newInstance().newTransformer();
        tx.transform(new DOMSource(doc), sr);

//        String check = sw.toString();

        return sw.toString();
    }
    private void writeBase64(StringWriter f, byte[] b64)
        throws Exception
    {
        int j = 0;
        for (int i = 0; i < b64.length; i++) {
            if ((i != 0) && ((i % B64CHUNKSIZE) == 0)) {
                f.write("\n");
            }
            f.write((int)b64[i]);
        }
        f.write("\n");
        f.flush();
    }

    private boolean verify(String s)
        throws Exception
    {

        if (alwaysAccept) {
            return true;
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new CharArrayReader(s.toCharArray())));

        // Get the key from the BinarySecurityToken

        Node bst = doc.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "BinarySecurityToken").item(0);
        Node un = doc.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Username").item(0);
        NodeList bstChildren = bst.getChildNodes();
        String encodedKey = null;
        for (int i = 0; i < bstChildren.getLength(); i++) {
            Node kn = bstChildren.item(i);
            if (kn.getNodeType() == Node.TEXT_NODE) {
                encodedKey = kn.getNodeValue();
                break;
            }
        }
        if (encodedKey == null) {
            throw new Exception("Unable to resolve encoded certificate in <BinarySecurityToken>");
        }
        StringBuilder sb = new StringBuilder("-----BEGIN CERTIFICATE-----\n");
        sb.append(encodedKey);
        if (encodedKey.charAt(encodedKey.length() - 1) != '\n') {
            sb.append("\n");
        }
        sb.append("-----END CERTIFICATE-----");
        String skey = sb.toString();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate x = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(sb.toString().getBytes()));
        SimpleKeySelector sks = new SimpleKeySelector();
        sks.setFixedKey(x.getPublicKey());
        String sname = x.getSubjectX500Principal().getName();
        if (sname == null) {
            throw new Exception("No owner name in message signing certificate!");
        } else {
            // Do NOT strip "CN=" from the front of the principal here. Sender is expected
            // to provide CN= ...
            // sname = sname.substring(3);
            if (un != null) {
                String unfield = un.getTextContent();
                // ... unless the sender *hasn't* provided that, in which case remove the
                // leading CN= to give them a fighting chance.
                //
                if (!unfield.toUpperCase().startsWith("CN=")) {
                    sname = sname.substring(3);
                }
                if (!sname.contentEquals(unfield)) {
                    System.err.println("Rejecting message: Username/certificate owner mismatch: Username: " + unfield + " / Certificate: " + sname);
                    throw new Exception("User name and certificate owner do not match");
                }
            } else {
                throw new Exception("User name not present");
            }
        }

        Node n = doc.getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security").item(0);
        DOMStructure sig = new DOMStructure(doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature").item(0));
        XMLSignatureFactory xsf = XMLSignatureFactory.getInstance("DOM");
        DOMValidateContext dvc = new DOMValidateContext(sks, n);
        dvc.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);
        dvc.setURIDereferencer(new DOMURIdereferencer());
        XMLSignature signature = xsf.unmarshalXMLSignature(sig);
        boolean isvalid = signature.validate(dvc);

        if (!isvalid) {
            boolean sigvalid = signature.getSignatureValue().validate(dvc);
            if (sigvalid) {
                java.util.Iterator it = signature.getSignedInfo().getReferences().iterator();
                for (int j = 0; it.hasNext(); j++) {
                    Reference refr = (Reference)it.next();
                    boolean refValid = refr.validate(dvc);
                    if (!refValid) {
                        System.err.println("Reference " + j + " is invalid");
                        java.io.InputStreamReader sis = new java.io.InputStreamReader(refr.getDigestInputStream());
                        char[] b = new char[10240];
                        sis.read(b);
                        System.err.println(b);
                    }
                }
            } else {
                System.err.println("Signaure validation failed");
                return false;
            }
        }
        
        return isvalid;
    }
}
