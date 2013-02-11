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
package org.warlock.http;
import java.util.HashMap;
import java.io.InputStream;
import java.util.ArrayList;
/**
 * Class to encapsulate the data and operations around an HTTP request.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class HttpRequest 
{
    private InputStream inputStream;
    private String sourceId;
    private String protocol;
    private String requestType;
    private String requestContext;
    private String remoteAddress;
    private HashMap<String,HttpFields> headers;
    private boolean handled;
    private int contentLength;
    private HttpResponse response;
    
    /** Creates a new instance of HttpRequest */
    HttpRequest(String id) {
        sourceId = id;
        handled = false;
        contentLength = -1;
        headers = new HashMap<String,HttpFields>();
    }
    public void readyToRead() 
        throws Exception
    { 
        inputStream.reset(); 
    }
    public InputStream getInputStream() { return inputStream; }
    public String getRemoteAddr() { return remoteAddress; }
    void setResponse(HttpResponse r) {
        response = r;
        r.setRequest(this);
    }
    /**
     * Get the HttpResponse object for returning results from this request.
     * @return 
     */
    public HttpResponse getResponse() { return response; }
    void setRemoteAddress(String a) { remoteAddress = a; }
    void setHeader(String h, String v) 
        throws Exception
    {        
        headers.put(h.toLowerCase(), new HttpFields(h, v));
        if (h.equalsIgnoreCase("content-length")) {
            contentLength = Integer.parseInt(v);
        }
    }

    // Used to set content length from the chunked request reader
    void setContentLength(int c)
            throws Exception
    {
        if (contentLength != -1) {
            throw new Exception("Protocol error: content length already set");
        }
        contentLength = c;
    }

    /**
     * Get the HTTP header names.
     * @return An ArrayList containing the HTTP header names.
     */
    public ArrayList<String> getFieldNames() {
        ArrayList<String> fields = new ArrayList<String>();
        for (String s : headers.keySet()) {
            fields.add(headers.get(s).getActualHeader());
        }
        return fields;
    }
    
    /**
     * Get the value of the given HTTP header.
     * @param The header name, case-insensitive.
     * @return The header value, or null if no such header is present.
     */
    public String getField(String h) {
        if ((h == null) || (h.trim().length() == 0)) {
            return null;
        }
        String key = h.toLowerCase();
        if (!headers.containsKey(key)) {
            return null;
        }
        return headers.get(key).getValue();
    }
    
    String getSourceId() { return sourceId; }
    
    /**
     * Get the context path
     * @return The context path
     */
    public String getContext() { return requestContext; }
    
    /**
     * Called by a handler when processing is finished, to indicate to the server
     * that the response can be written back to the requestor.
     * 
     * @param b 
     */
    public void setHandled(boolean b) { handled = b; }
    
    /**
     * Test whether setHandled() has been called.
     * @return 
     */
    public boolean isHandled() { return handled; }
    public int getContentLength() { return contentLength; }
    boolean contentLengthSet() { return (contentLength != -1); }
    
    /**
     * Gets the HTTP method used for the request.
     * @return 
     */
    public String getMethod() { return requestType; }
    
    /**
     * Gets the HTTP version used for the request.
     * @return 
     */
    public String getVersion() { return protocol; }
    
    void close() {
        try {
            inputStream.close();
        }
        catch (Exception e) {
            System.err.println("Error closing request from " + sourceId + " : " + e.getMessage());
        }
    }
    void setProtocol(String p) 
        throws Exception
    {
        if (!(p.compareTo("HTTP/1.1") == 0)) {
            throw new Exception("Only HTTP/1.1 protocol version accepted, got " + p);
        }
        protocol = p; 
    }
    
    void setRequestType(String r) 
        throws Exception
    {
        if (!(r.compareTo("POST") == 0)) {
            throw new Exception("Only POST request type accepted, got " + r);
        }
        requestType = r; 
    }
    
    void setRequestContext(String c) 
        throws Exception
    {
        if ((c == null) || (c.trim().length() == 0)) {
            throw new Exception("Invalid request, empty context");
        }
        requestContext = c; 
    }
    
    void setInputStream(InputStream h) { inputStream = h; }
}
