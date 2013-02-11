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
package org.warlock.tk.handlers;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.StringReader;
import org.warlock.http.HttpRequest;
import org.warlock.http.HttpResponse;
import org.warlock.http.HttpException;
import org.warlock.tk.boot.Toolkit;
import org.warlock.util.CfHNamespaceContext;
import org.warlock.util.XMLNamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
/**
 * Handler implementation for synchronous ITK SOAP requests. TKW wants a single
 * class as the handler for a request, but this request will have been put in its
 * own thread by the HTTP server. So for thread safety this class' handle() method
 * creates a separate SynchronousWorker instance to handle this particular request,
 * and calls handle() on that.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class SynchronousSoapRequestHandler
    extends org.warlock.tk.boot.ToolkitHttpHandler
{
    private static final String SYNCWRAPPER = "tks.synchronousreply.wrapper";
    private static final String FAULTPAYLOAD = "tks.synchronousreply.fault";

    protected String synchronousWrapper = null;
    protected String soapfault = null;

    private static final String SOAPREQUEST = "/";
    private static final String SOAPBODY = "/SOAP:Envelope/SOAP:Body";
    private static final String SOAPHEADER = "/SOAP:Envelope/SOAP:Header";
    private static final String MSGID = "/SOAP:Envelope/SOAP:Header/wsa:MessageID";
    private static final String REPLY = "/SOAP:Envelope/SOAP:Header/wsa:ReplyTo/wsa:Address";
    private static final String FAULT = "/SOAP:Envelope/SOAP:Header/wsa:FaultTo/wsa:Address";

    protected XPathExpression getMessageID = null;
    protected XPathExpression getReplyTo = null;
    protected XPathExpression getFaultTo = null;
    protected XPathExpression getTo = null;
    protected XPathExpression getHeader = null;
    protected XPathExpression getBody = null;
    protected XPathExpression getSoapRequest = null;

    protected static XMLNamespaceContext ns = CfHNamespaceContext.getXMLNamespaceContext();
    
    protected static final String SAVEDMESSAGES = "tks.savedmessages";

    public SynchronousSoapRequestHandler() 
        throws Exception
    {
        XPathFactory x = XPathFactory.newInstance(XPathConstants.DOM_OBJECT_MODEL);
        XPath xp = x.newXPath();
        xp.setNamespaceContext(ns);
        getMessageID = xp.compile(MSGID);
        x = XPathFactory.newInstance(XPathConstants.DOM_OBJECT_MODEL);
        xp = x.newXPath();
        xp.setNamespaceContext(ns);
        getReplyTo = xp.compile(REPLY);
        x = XPathFactory.newInstance(XPathConstants.DOM_OBJECT_MODEL);
        xp = x.newXPath();
        xp.setNamespaceContext(ns);
        getFaultTo = xp.compile(FAULT);
        x = XPathFactory.newInstance(XPathConstants.DOM_OBJECT_MODEL);
        xp = x.newXPath();
        xp.setNamespaceContext(ns);
        getHeader = xp.compile(SOAPHEADER);
        x = XPathFactory.newInstance(XPathConstants.DOM_OBJECT_MODEL);
        xp = x.newXPath();
        xp.setNamespaceContext(ns);
        getBody = xp.compile(SOAPBODY);
        x = XPathFactory.newInstance(XPathConstants.DOM_OBJECT_MODEL);
        xp = x.newXPath();
        xp.setNamespaceContext(ns);
        getSoapRequest = xp.compile(SOAPREQUEST);
        x = XPathFactory.newInstance(XPathConstants.DOM_OBJECT_MODEL);
    }
    int getAsyncTTL() { return super.asyncttl; }

    synchronized Node extractHeader(Document d)
            throws Exception
    {
        return (Node)getHeader.evaluate(d, XPathConstants.NODE);
    }

    synchronized Node extractBody(Document d)
            throws Exception
    {
        return (Node)getBody.evaluate(d, XPathConstants.NODE);
    }

    Node extractSoapRequest(Document d)
            throws Exception
    {
        return (Node)getSoapRequest.evaluate(d, XPathConstants.NODE);
    }

    String extractMessageId(String m)
        throws Exception
    {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getMessageID, s);
    }
    String extractReplyTo(String m)
        throws Exception
    {
        InputSource s = new InputSource(new StringReader(m));
        return extractStringXpath(getReplyTo, s);
    }
    String extractFaultTo(String m)
        throws Exception
    {
        InputSource s = new InputSource(new StringReader(m));
        String ft= extractStringXpath(getFaultTo, s);
        return (ft.length() == 0) ? null : ft;
    }

    synchronized Node extractNodeXpath(XPathExpression x, InputSource m)
        throws Exception
    {
        return (Node)x.evaluate(m, XPathConstants.NODE);
    }


    synchronized String extractStringXpath(XPathExpression x, InputSource m)
        throws Exception
    {
        return (String)x.evaluate(m, XPathConstants.STRING);
    }


    String getSoapFault() { return soapfault; }
    String getSynchronousWrapper() { return synchronousWrapper; }
    String getSavedMessagesDirectory() { return savedMessagesDirectory; }

    @Override
    public void setToolkit(Toolkit t)
        throws Exception
    {
        super.setToolkit(t);
        synchronousWrapper = load(toolkit.getBootProperties().getProperty(SYNCWRAPPER));
        soapfault = load(toolkit.getBootProperties().getProperty(FAULTPAYLOAD));
        savedMessagesDirectory = toolkit.getBootProperties().getProperty(SAVEDMESSAGES);
    }

    Toolkit getToolkit() { return super.toolkit; }

    protected String load(String file)
            throws Exception
    {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        br.close();
        return sb.toString();
    }

    @Override
    public void handle(String path, String params, HttpRequest req, HttpResponse resp)
        throws HttpException
    {
        SynchronousWorker s = new SynchronousWorker(this);
        s.handle(path, params, req, resp);
    }


}
