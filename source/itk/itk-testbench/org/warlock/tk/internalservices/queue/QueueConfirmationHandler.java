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
package org.warlock.tk.internalservices.queue;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.boot.ToolkitSimulator;
import org.warlock.tk.boot.ToolkitService;
import org.warlock.tk.internalservices.process.ProcessData;
import org.warlock.tk.internalservices.process.ProcessorSoapFaultResponse;
import org.warlock.util.CfHNamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * TKW simulator request processor for queue confirmation messages.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class QueueConfirmationHandler
    implements org.warlock.tk.internalservices.process.RequestProcessor
{
    private ToolkitSimulator simulator = null;

    private static final String SUBSCRIBERNAMEXPATH = "/soap:Envelope/soap:Body/itk:QueueConfirmMessageReceipt/itk:QueueName";
    private static final String MSGIDXPATH = "/soap:Envelope/soap:Body/itk:QueueConfirmMessageReceipt/itk:MessageHandle";

    private XPathExpression subscriberNameXpath = null;
    private XPathExpression messageIdXpath = null;

    public QueueConfirmationHandler()
            throws Exception
    {
        XPath x = XPathFactory.newInstance().newXPath();
        x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
        subscriberNameXpath = x.compile(SUBSCRIBERNAMEXPATH);
        x = XPathFactory.newInstance().newXPath();
        x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
        messageIdXpath = x.compile(MSGIDXPATH);
    }

    @Override
    public void setSimulator(ToolkitSimulator t) { simulator = t; }

    private ArrayList<String> getMessageHandles(String m) {
        ArrayList<String> handles = null;
        try {
            InputSource is = new InputSource(new StringReader(m));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setIgnoringComments(true);
            Document doc = dbf.newDocumentBuilder().parse(is);
            NodeList nl = doc.getElementsByTagNameNS("urn:nhs-itk:ns:201005", "MessageHandle");
            handles = new ArrayList<String>();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    String c = n.getTextContent();
                    if ((c != null) && (c.trim().length() > 0)) {
                        handles.add(c);
                    }
                }
            }
        }
        catch (Exception e) {
            System.err.println(e.getClass().getName() + " thrown getting message handles : " + e.getMessage());
        }
        return handles;
    }

    @Override
    public ServiceResponse process(ProcessData p)
            throws Exception
    {
        InputSource is = new InputSource(new StringReader(p.getEnvelope()));
        String qname = null;
        synchronized(this) {
            qname = subscriberNameXpath.evaluate(is);
        }
        if ((qname == null) || (qname.trim().length() == 0)) {
            ProcessorSoapFaultResponse psfr = new ProcessorSoapFaultResponse("SubscriberName not given", null, "1000");
            p.setFaultResponse(psfr);
            return new ServiceResponse(500, null);
        }
        ArrayList<String> handles = getMessageHandles(p.getEnvelope());
        if ((handles == null) || (handles.size() == 0)) {
            ProcessorSoapFaultResponse psfr = new ProcessorSoapFaultResponse("No messages given for confirmation", null, "1000");
            p.setFaultResponse(psfr);
            return new ServiceResponse(500, null);
        }
        try {
            confirmMessages(qname, handles);
        }
        catch (Exception e) {
            ProcessorSoapFaultResponse psfr = new ProcessorSoapFaultResponse(e.getMessage(), null, "1000");
            p.setFaultResponse(psfr);
            return new ServiceResponse(500, null);
        }
        return new ServiceResponse(200, "<itk:SimpleMessageResponse xmlns:itk=\"urn:nhs-itk:ns:201005\">OK</itk:SimpleMessageResponse>");
    }

    private void confirmMessages(String q, ArrayList<String> m)
        throws Exception
    {
        ToolkitService svc = simulator.getService("QueueManager");
        if (svc == null) {
            throw new Exception("QueueManager not available");
        }
        QueueItem qi = new QueueItem(q, null, null, null);
        String[] handles = new String[m.size()];
        m.toArray(handles);
        qi.setConfirmIds(handles);
        svc.execute(qi);
    }
}
