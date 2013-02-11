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
import java.util.HashMap;
import java.io.StringReader;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.boot.ToolkitSimulator;
import org.warlock.tk.internalservices.queue.QueueItem;
import org.warlock.util.Logger;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import org.xml.sax.InputSource;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;
import org.warlock.tk.internalservices.queue.DeliveryQueue;
import org.warlock.util.CfHNamespaceContext;
 /**
 * Provides access to the internal message queues for asynchronous responses,
 * and the ITK "Queue Collection" service. Queues are configured in a file
 * referenced by the property "tks.queues.configfile" in the TKW properties file.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class QueueManagerService
    implements org.warlock.tk.boot.ToolkitService
{
    private static final String QUEUECONFIGFILE = "tks.queues.configfile";
    private static final String MSGID = "/SOAP:Envelope/SOAP:Header/wsa:MessageID";
    private static final long DEFAULTTIMEOUT = 1000;
    private XPathExpression getMessageId = null;

    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;

    private HashMap<String,DeliveryQueue> queues = null;

    public QueueManagerService() {}

    @Override
    public Properties getBootProperties() { return bootProperties; }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception
    {
        bootProperties = p;
        serviceName = s;
        simulator = t;

        String qcfile = p.getProperty(QUEUECONFIGFILE);
        if ((qcfile == null) || (qcfile.trim().length() == 0)) {
            throw new Exception("Error initialising queue service, queue config file property " + QUEUECONFIGFILE + "not given");
        }
        queues = new HashMap<String,DeliveryQueue>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(qcfile));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || (line.trim().length() == 0)) {
                    continue;
                }
                StringTokenizer st = new StringTokenizer(line);
                String name = st.nextToken();
                String queueClass = st.nextToken();
                long timeout = DEFAULTTIMEOUT;
                if (st.hasMoreTokens()) {
                    String t1 = st.nextToken();
                    try {
                        timeout = Integer.parseInt(t1);
                    }
                    catch (Exception e) {
                        Logger.getInstance().log("QueueManagerService.boot", "Error in timeout for queue " + name + ", should be a number in milliseconds: " + t1);
                    }
                }
                DeliveryQueue dq = (DeliveryQueue)Class.forName(queueClass).newInstance();
                dq.start(name, timeout);
                queues.put(name, dq);
            }
            br.close();
        }
        catch (Exception e) {
            throw new Exception("Error reading queue config file " + qcfile + " : " + e.getMessage());
        }
        XPath m = XPathFactory.newInstance().newXPath();
        m.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
        getMessageId = m.compile(MSGID);
        Logger.getInstance().log("QueueManagerService started");
    }

    private String getMessageId(QueueItem q)
        throws Exception
    {
        if (q.getMessageId() != null) {
            return q.getMessageId();
        }
        InputSource is = new InputSource(new StringReader(q.getMessage()));
        String id = null;
        synchronized(this) {
            id = getMessageId.evaluate(is);
        }
        q.setMessageId(id);
        return id;
    }

    /**
     * "Put" a message on a queue. Message and queue details are populated by the 
     * caller in a org.warlock.tk.internalservices.queue.QueueItem instance. The method
     * is also used for the "Confirm" operation of queue collection.
     * 
     * @param param org.warlock.tk.internalservices.queue.QueueItem specifying the message and queue
     * @return ServiceResponse with a response value of zero on success, 1 when the requested queue does not exist
     * @throws Exception on "confirm" operations when no message with the given id is found.
     */
    @Override
    public ServiceResponse execute(Object param)
            throws Exception
    {
        // This is a "put", and param is a QueueItem. 
        //
        // Also used to "confirm delete" for reliable queues where the queue item
        // has a message id only.
        //
        QueueItem q = (QueueItem)param;
        DeliveryQueue dq = getQueue(q.getQueueName());
        if (dq == null){
            return new ServiceResponse(1, "No such queue: " + q.getQueueName());
        }
        if (q.getMessage() == null) {
            if (q.getConfirmIds() == null) {
                if (q.getMessageId() == null) {
                    throw new Exception("No message id(s) given for confirmation");
                }
                dq.confirm(q.getMessageId());
                return new ServiceResponse(0, null);
            } else {
                dq.batchConfirm(q.getConfirmIds());
                return new ServiceResponse(0, null);
            }
        }
        dq.add(q);
        return new ServiceResponse(0, null);
    }

    /**
     * Check that a queue of the given name exists.
     * @param type Not used.
     * @param param Queue name
     * @return ServiceResponse with zero response value if the queue exists, null otherwise.
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, String param)
            throws Exception
    {
        // Used for a check on whether we have a queue of the name given in "param"
        if (queues.containsKey(param)) {
            return new ServiceResponse(0, null);
        }
        return null;
    }

    /**
     * Returns an instance of the queue with the given name.
     * @param n Queue name
     * @return Queue instance, or null if it does not exist.
     * @throws Exception if a null queue name is given
     */
    public DeliveryQueue getQueue(String n)
            throws Exception
    {
        if (n == null) {
            throw new Exception("No queue name given");
        }
        DeliveryQueue dq = queues.get(n);
        return dq;
    }
    
    /**
     * This is used to query, with "param" being a QueueItem containing
     * queue name, and either message id or address or action. What actually
     * happens depends on the sort of queue it is, but typically it will
     * return a service response with an array of matches. A response code of
     * zero means success, non-zero means failure and the "response string"
     * will contain some explanatory text.
     * 
     * @param type Not used
     * @param param QueueItem with queue name, address or action.
     * @return ServiceResponse with zero response code and message(s) on success, 1 response code and some error text if not found
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, Object param)
            throws Exception
    {
        ServiceResponse s = null;
        QueueItem q = (QueueItem)param;
        DeliveryQueue dq = getQueue(q.getQueueName());
        if (dq == null){
            return new ServiceResponse(1, "No such queue: " + q.getQueueName());
        }
        if (q.getMessageId() != null) {
            QueueItem r = dq.getSingle(q);
            if (r == null) {
                return new ServiceResponse(1, "Not found: " + q.getMessageId());
            }
            s = new ServiceResponse(0, null);
            s.setScalar(r);
        } else {
            if (q.getAddress() == null) {
                return new ServiceResponse(1, "Null recipeint");
            }
            QueueItem[] r = dq.getBatch(q);
            s = new ServiceResponse(0, null);
            s.setArray(r);
        }
        return s;
    }
}
