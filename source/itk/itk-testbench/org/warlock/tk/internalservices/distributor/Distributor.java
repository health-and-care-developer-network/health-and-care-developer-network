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
import org.warlock.tk.experimental.distributor.SpineInjector.SpineInjectorFactory;
import org.warlock.tk.internalservices.distributor.xdradapter.ITKXDRAdapter;
import org.safehaus.uuid.UUIDGenerator;
import org.warlock.tk.internalservices.send.SenderRequest;
import org.warlock.tk.boot.ToolkitService;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.boot.ToolkitSimulator;
import org.warlock.tk.internalservices.process.ProcessData;
import org.warlock.tk.internalservices.process.ProcessorSoapFaultResponse;
import org.warlock.tk.addressing.DistributionResolver;
import org.warlock.tk.addressing.RoutingSolution;
import org.warlock.tk.addressing.PhysicalAddress;
import java.util.HashSet;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Properties;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.CharArrayReader;
import java.io.StringWriter;

 /**
 * RequestProcessor implementation for calling the TKW internal router under the
 * control of the TKW simulator mode and its simulator configuration file.
 * 
 * Note that this class and the other TKW message router components are test
 * articles and implementors are recommended not to base production code on them.
 * An open source implementation of the ITK routing specification is available
 * separately.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class Distributor
    implements org.warlock.tk.internalservices.process.RequestProcessor
{
    private ToolkitSimulator simulator = null;
    private ProcessData processData = null;
    private DistributionResolver resolver = null;
    private static final String ITKURLWRAPPER = "tks.distributor.urlwrapper";
    private String urlWrapperTemplate = null;
    private String transmissionId = null;
    
    private String inboundContext = null;
    private String outboundContext = null;
    private String relayContext = null;

    public Distributor() {}

    @Override
    public void setSimulator(ToolkitSimulator t) {
        simulator = t;
        try {
            resolver = new DistributionResolver(t);
        }
        catch (Exception e) {
            System.err.println("Failed to boot Distributor resolver");
            e.printStackTrace();
        }
        inboundContext = simulator.getService("Toolkit").getBootProperties().getProperty("tks.addressing.inboundcontext");
        outboundContext = simulator.getService("Toolkit").getBootProperties().getProperty("tks.addressing.outboundcontext");
        relayContext = simulator.getService("Toolkit").getBootProperties().getProperty("tks.addressing.relaycontext");
    }

    @Override
    public ServiceResponse process(ProcessData p)
            throws Exception
    {
        String service = null;
        processData = p;
        if (resolver == null) {
            throw new Exception("DistributionResolver failed to start");
        }
        if (urlWrapperTemplate == null) {
            loadUrlTemplate();
        }
        String msg = p.getEnvelope();
        InputSource is = new InputSource(new StringReader(msg));
        NodeList messageAddresses = null;
        String message = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);
        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new CharArrayReader(msg.toCharArray())));
        Node addressList = doc.getElementsByTagNameNS("urn:nhs-itk:ns:201005", "addressList").item(0);
        messageAddresses = addressList.getChildNodes();
        Node transportPackage = doc.getElementsByTagNameNS("urn:nhs-itk:ns:201005", "DistributionEnvelope").item(0);
        NodeList originalService = doc.getElementsByTagNameNS("urn:nhs-itk:ns:201005", "header");
        if (originalService.getLength() > 0) {
            Node hdr = originalService.item(0);
            if (hdr.hasAttributes()) {
                try {
                    service = ((Node)hdr.getAttributes().getNamedItem("service")).getNodeValue();
                    if (service == null) {
                        service = p.getAction();
                    }
                    transmissionId = ((Node)hdr.getAttributes().getNamedItem("trackingid")).getNodeValue();
                }
                catch (Exception e) {
                    service = p.getAction();
                }
            }
        }

        StringWriter sw = new StringWriter();
        StreamResult sr = new StreamResult(sw);
        Transformer tx = TransformerFactory.newInstance().newTransformer();
        tx.setOutputProperty("omit-xml-declaration", "yes");
        tx.transform(new DOMSource(transportPackage), sr);
        message = sw.toString();

        // If isOutbound we route non authoritative solutions only,
        // If isInbound we route authoritative solutions only
        //  If IsRelay we route both
        boolean isOutbound = p.getRequestContext().contentEquals(outboundContext);
        boolean isInbound = p.getRequestContext().contentEquals(inboundContext);
        boolean isRelay = false;
        if (relayContext != null) {
            isRelay = p.getRequestContext().contentEquals(relayContext);
        }

        // Look up all the addresses and collect the routing solutions... make
        // sure we have a unique list before trying to do anything about it.
        HashSet<PhysicalAddress> routes = new HashSet<PhysicalAddress>();
        StringBuilder sb = new StringBuilder();
        boolean someworked = false;

        // We need to call "filter(service)" on r, once that method has been
        // rewritten to return a physical address, and we need to add that to "routes".

        for (int i = 0; i < messageAddresses.getLength(); i++) {
            Node n = messageAddresses.item(i);
            if (n.hasAttributes()) {
                String a = ((Node)n.getAttributes().getNamedItem("uri")).getNodeValue();
                try {
                    RoutingSolution r = resolver.resolve(a, service);

                    // If this is a redirection, look up the redirected address
                    if (r.getAddress(service).getType() == PhysicalAddress.REDIRECTION) {
                        r = resolver.resolve(r.getAddress(service).getAddress(), service);
                    }
                    PhysicalAddress pa = r.getAddress(service);
                    if (!routes.contains(pa)) {
                        routes.add(pa);
                    }
                    sb.append("Address: ");
                    sb.append(a);
                    sb.append(" resolved\n");
                    someworked = true;
                }
                catch (Exception e) {
                    sb.append("Address: ");
                    sb.append(a);
                    sb.append(" FAILED to resolve: \n");
                    sb.append(e.getMessage());
                    sb.append("\n");
                }
            }
        }
        
        for (PhysicalAddress pa : routes) {
            try {
                if (isRelay) {
                    send(message, pa, service);
                } else {
                    if (isOutbound && !pa.isAuthoritative()) {
                        send(message, pa, service);
                    } else {
                        if (isInbound && pa.isAuthoritative()) {
                            send(message, pa, service);
                        }
                    }
                }
            }
            catch (Exception e) {
                sb.append("Failed to send: ");
                sb.append(pa.getAddress());
            }
        }

        StringBuilder resp = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        resp.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\r\n");
        resp.append("<soap:Header>\r\n<wsa:MessageID>");
        resp.append(UUIDGenerator.getInstance().generateTimeBasedUUID().toString().toUpperCase());
        resp.append("</wsa:MessageID>\r\n</soap:Header>\r\n<soap:Body>\r\n");
        resp.append("<itk:SimpleMessageResponse xmlns:itk=\"urn:nhs-itk:ns:201005\">");
        resp.append(sb);
        resp.append("</itk:SimpleMessageResponse>\r\n</soap:Body>\r\n</soap:Envelope>");
        ServiceResponse s = null;
        if (someworked) {
            s = new ServiceResponse();
            s.setAction("SimpleITKAcknowledgment");
            s.setResponse(resp.toString());
            s.setCode(202);
            return s;
        } else {
            ProcessorSoapFaultResponse psfr = new ProcessorSoapFaultResponse("No addresses resolved", "","1000");
            p.setFaultResponse(psfr);
            return s;
        }
    }

    private void send(String msg, PhysicalAddress p, String action)
            throws Exception
    {
        switch(p.getType()) {
            case PhysicalAddress.ASID:
                sendToASID(msg, p.getAddress());
                break;

            case PhysicalAddress.QUEUE:
                sendToQueue(msg, p.getAddress(), action);
                break;

            case PhysicalAddress.URL:
                sendToURL(msg,p.getAddress(), action, p.isStarService());
                break;
                
            case PhysicalAddress.DTS:
                sendToDTS(msg, p.getAddress(), action);
                break;
                
            case PhysicalAddress.IHEXDR:
                sendToXDR(msg, p.getAddress());
                break;
                
            case PhysicalAddress.REDIRECTION:
                throw new Exception("Redirected");
            case PhysicalAddress.NOLONGERSUPPORTEDBLOCK:
                throw new Exception("Address no longer supported");
            case PhysicalAddress.SECURITYBLOCK:
                throw new Exception("Security alert: illegal address");
            case PhysicalAddress.UNSPECIFIED:
                throw new Exception("Address unspecified");
        }
    }

    private void sendToXDR(String msg, String addr)
            throws Exception
    {
        ITKXDRAdapter x = ITKXDRAdapter.getInstance(simulator);
        x.sendToXDR(msg, addr);
    }
    
    private void sendToASID(String msg, String addr)
            throws Exception
    {
        SpineInjectorFactory sif = SpineInjectorFactory.getInstance();
        sif.runSpineInjector(simulator, msg, addr);
    }

    private void sendToDTS(String msg, String address, String action)
            throws Exception
    {
        DTS dts = DTS.getInstance(simulator);
        dts.send(msg, address, action, transmissionId);
    }
    
    private void sendToURL(String msg, String addr, String action, boolean forwardingService)
            throws Exception
    {
        StringBuilder sb = new StringBuilder(urlWrapperTemplate);
        substitute(sb, "__MESSAGE_ID__", UUIDGenerator.getInstance().generateTimeBasedUUID().toString().toUpperCase());
//        if (forwardingService) {
//            substitute(sb, "__ACTION__", "SendDistributionRequest");
//        } else {
            substitute(sb, "__ACTION__", action);
//        }
        substitute(sb, "__URL__", addr);
        substitute(sb, "__PAYLOAD__", msg);
        //
        // NOTE: Not doing signing for the purposes of this experiment
        //
        SenderRequest sr = new SenderRequest(sb.toString(), null, addr);
//        if (forwardingService) {
//            sr.setAction("SendDistributionRequest");
//        } else {
            sr.setAction(action);
//        }
        ToolkitService svc = simulator.getService("Sender");
        if (svc == null) {
            throw new Exception("Distributor: No Sender service");
        }
        svc.execute(sr);
    }

    private void sendToQueue(String msg, String addr, String action)
            throws Exception
    {
        SenderRequest sr = new SenderRequest(msg, null, addr);
        sr.setAction(action);
        ToolkitService svc = simulator.getService("Sender");
        if (svc == null) {
            throw new Exception("Distributor: No Sender service");
        }
        svc.execute(sr);
    }

   private boolean substitute(StringBuilder sb, String tag, String content)
            throws Exception
    {
        boolean doneAnything = false;
        int tagPoint = -1;
        int tagLength = tag.length();
        while ((tagPoint = sb.indexOf(tag)) != -1 ) {
            sb.replace(tagPoint, tagPoint + tagLength, content);
            doneAnything = true;
        }
        return doneAnything;
    }

    private void loadUrlTemplate()
            throws Exception
    {
        String t = null;
        try {
            Properties p = simulator.getService("Toolkit").getBootProperties();
            t = p.getProperty(ITKURLWRAPPER);
        }
        catch (Exception e) {}
        if (t == null) {
            throw new Exception("No URL delivery wrapper template");
        }
        try {
            FileReader fr = new FileReader(t);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            fr.close();
            urlWrapperTemplate = sb.toString();
        }
        catch (Exception e) {
            throw new Exception("Failed to read URL delivery wrapper: " + e.getMessage());
        }
    }

}
