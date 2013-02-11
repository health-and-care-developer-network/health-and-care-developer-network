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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
/**
 * Class to encapsulate data and operations on the response to an HTTP request.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class HttpResponse {
    
    private HttpRequest request;
    private OutputStream outputStream;
    private String status;
    private String statusText;
    private static final String SERVER = "Server: CfH NIC Message Test Harness embedded server v1.1-20071012\r\n";
    private NotifyOnFlushOutputStream outbuffer;
    private String contentType;
    private HashMap<String,String> headers;
  
    HttpResponse(OutputStream o) {
        request = null;
        outputStream = o;
        status = null;
        statusText = "";
        contentType = null;
        headers = null;
        outbuffer = new NotifyOnFlushOutputStream(this);
    }
  
    /**
     * Set the MIME content type for the response.
     * @param c 
     */
    public void setContentType(String c) { contentType = c; }
    void setRequest(HttpRequest r) { request = r; }
    
    /**
     * Get the output stream.
     * @return 
     */
    public OutputStream getOutputStream() { return outbuffer; }

    public String getContentType() {
        return contentType;
    }
    
    
    
    /**
     * Set an HTTP response field. This should only be used for "other" response headers that are not handled
     * explicitly, for example SOAPaction (MTH calling code needs to know about this).
     * @param f Header name
     * @param v Header value
     */
    public void setField(String f, String v) {
        if ((f == null) || (f.trim().length() == 0) || (v == null) || (v.trim().length() == 0)) {
            return;
        }
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put(f,v);
    }

    void canWrite() {
        // Write the buffer and HTTP headers to the outputStream
        //
        // HTTP/1.1 status statusText
        // Content-Length
        // Content Type
        // Any other headers
        // blank line
        // response
        StringBuilder sb = new StringBuilder("HTTP/1.1 ");
        if (status != null) {
            sb.append(status);
            sb.append(" ");
        }
        sb.append(statusText);
        sb.append("\r\n");
        sb.append("Content-Length: ");
        sb.append(Long.toString(outbuffer.size()));
        sb.append("\r\n");
        sb.append("Connection: close\r\n");
        if ((contentType != null) && (contentType.trim().length() > 0)) {
            sb.append("Content-type: ");
            sb.append(contentType);
            sb.append("\r\n");
        }
        if (headers != null) {
            for (String h : headers.keySet()) {
                String v = headers.get(h);
                sb.append(h);
                sb.append(": ");
                sb.append(v);
                sb.append("\r\n");
            }
        }
        sb.append("\r\n");
        try {
            
            OutputStreamWriter o = new OutputStreamWriter(outputStream);
            o.write(sb.toString());
            o.flush();
            outputStream.write(outbuffer.toByteArray());
            outputStream.flush();
            if (request.isHandled()) {
                request.close();
            }
            outputStream.close();
        }
        catch (Exception e) {
            String s = new String("Error writing response: " + e.getClass().toString() + " : " + e.getMessage());
            System.err.println(s);
            LastResortReporter.report(s, outputStream);                               
        }
        
    }
    public void flush() {
        this.canWrite();
    }
    
    /**
     * Sets the HTTP response text.
     * @param s 
     */
    public void setStatusText(String s) { statusText = s; }

    /**
     * Provided to allow the response to be aborted, forcing the connection closed
     * as a protocol violation for test purposes.
     */
    public void forceClose() {
        try {
            outputStream.close();
        }
        catch (Exception e) {
            String s = new String("Error closing connection on null response: " + e.getClass().toString() + " : " + e.getMessage());
            System.err.println(s);                                           
        }
    }
    /**
     * Sets the HTTP response code.
     * @param s 
     */
    public void setStatus(int s) {
        if ((s < 100) || (s > 600))
            return;
        status = Integer.toString(s);
    }
    
    /**
     * Set both HTTP response code and the status text.
     * @param i
     * @param s 
     */
    public void setStatus(int i, String s) {
        setStatusText(s);
        setStatus(i);
    }
}
