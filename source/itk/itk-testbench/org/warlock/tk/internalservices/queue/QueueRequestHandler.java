/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
/**
 * TKW simulator RequestProcessor for handling Queue Collection messages.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class QueueRequestHandler
    implements org.warlock.tk.internalservices.process.RequestProcessor
{
    private ToolkitSimulator simulator = null;

    private static final String SUBSCRIBERNAMEXPATH = "/soap:Envelope/soap:Body/itk:QueueMessage/itk:QueueName";
    private static final String MSGTYPEXPATH = "/soap:Envelope/soap:Body/itk:QueueMessage/itk:ServiceMessageType";
    private static final String MSGCOUNTXPATH = "/soap:Envelope/soap:Body/itk:QueueMessage/itk:RequestedMessageCount";

    private XPathExpression subscriberNameXpath = null;
    private XPathExpression messageTypeXpath = null;
    private XPathExpression messageCountXpath = null;

    // TODO: Check this for thread safety and whether it ever uses the "Body", and "Header" Nodes

    public QueueRequestHandler() 
        throws Exception
    {
        XPath x = XPathFactory.newInstance().newXPath();
        x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
        subscriberNameXpath = x.compile(SUBSCRIBERNAMEXPATH);
        x = XPathFactory.newInstance().newXPath();
        x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
        messageTypeXpath = x.compile(MSGTYPEXPATH);
        x = XPathFactory.newInstance().newXPath();
        x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
        messageCountXpath = x.compile(MSGCOUNTXPATH);
    }

    @Override
    public void setSimulator(ToolkitSimulator t) { 
        simulator = t;
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
        is = new InputSource(new StringReader(p.getEnvelope()));
        String msgType = null;
        synchronized(this) {
            msgType = messageTypeXpath.evaluate(is);
        }
        if ((msgType == null) || (msgType.trim().length() == 0)) {
            msgType = "ALL";
        }
        is = new InputSource(new StringReader(p.getEnvelope()));
        int mCount = 1;
        try {
            String mc = null;
            synchronized(this) {
                mc = messageCountXpath.evaluate(is);
            }
            if ((mc != null) && (mc.trim().length() > 0)) {
                mCount = Integer.parseInt(mc);
            }
        }
        catch (Exception e) {}
        ServiceResponse s = queryQueue(qname, msgType, mCount);
        if (s.getCode() > 300) {
            ProcessorSoapFaultResponse psfr = new ProcessorSoapFaultResponse(s.getResponse(), "", "1000");
            p.setFaultResponse(psfr);
            s.setProcessorFault(psfr);
        }
        return s;
    }

    private ServiceResponse queryQueue(String name, String type, int count)
        throws Exception
    {
        ToolkitService svc = simulator.getService("QueueManager");
        if (svc == null) {
            throw new Exception("QueueManager not available");
        }
        QueueItem q = new QueueItem(name, null, name, type);
        q.setQueryMaxItems(count);

        ServiceResponse s = svc.execute(null, q);
        if (s == null) {
            s = new ServiceResponse(500, "Internal error querying queue");
        } else {
            if (s.getCode() == 1) {
                s.setCode(500);
            } else {
                s = makeQueueResponse(s);
                s.setAction("GetQueueMessagesResponse");
            }
        }
        return s;
    }

    private ServiceResponse makeQueueResponse(ServiceResponse s) {
                // TODO: We have an array of QueueItem in the ServiceResponse.
                // This needs to be converted into QueueMessageResponse XML
                // for return to the requestor.
        StringBuilder sb = new StringBuilder("<QueueMessageResponse xmlns=\"urn:nhs-itk:ns:201005\">");
        Object[] items = s.getArray();
        sb.append("<MessageCount>");
        sb.append(items.length);
        sb.append("</MessageCount>\n");
        for (Object o : items) {
            QueueItem i = (QueueItem)o;
            sb.append("<Message>\n");
            sb.append("<ServiceMessageType>");
            sb.append(i.getAction());
            sb.append("</ServiceMessageType>\n");
            sb.append("<MessageHandle>");
            sb.append(i.getMessageId());
            sb.append("</MessageHandle>\n");
            if (i.getRefToMessageId() != null) {
                sb.append("<RelatesTo>");
                sb.append(i.getRefToMessageId());
                sb.append("</RelatesTo>\n");
            }
            // Follows re-ordering in schema fix, 25 Aug 2010
            sb.append("<MessagePayload>");
            sb.append(i.getMessage());
            sb.append("</MessagePayload>\n");
            sb.append("</Message>\n");
        }
        sb.append("</QueueMessageResponse>");
        return new ServiceResponse(200, sb.toString());
    }
}
