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

import java.net.Socket;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.StringTokenizer;
import java.util.ArrayList;
/**
 * Package-private class for reading HTTP headers, and then the input stream.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
class RequestReader 
    extends Thread
{
    private Socket socket;    
    private HttpServer server;
    private String connectionId;
    private Exception exception;

    private byte[] chunkedMessageBuffer = null;

    private static final int MARKSIZE = 1024;
    
    /** Creates a new instance of RequestReader */
    RequestReader(Socket s, HttpServer h, String id) {
        socket = s;
        server = h;
        connectionId = id;
        exception = null;
        start();
    }
    
    Exception getException() { return exception; }
    
    private String getLine() 
        throws Exception
    {
        InputStream in = socket.getInputStream();
        StringBuilder sb = new StringBuilder();
        int r = 0;
        while ((r = in.read()) != (int)'\r') {
            if (r == -1) {
                break;
            }
            sb.append((char)r);
        }
        r = in.read();
        return sb.toString();
    }
    
    /**
     * The class has been instantiated with a reference to the inbound socket, and the
     * server. This thread is responsible for:
     * - Creating the request instance
     * - Creating the buffered input stream that sits on the socket
     * - Reading the header and identifying the request type, context path, and headers
     * - Passing the HttpRequest to the server (the server will ask the request to make the response)
     * - exitting
     */
    @Override
    public void run() {
        try {
            boolean readingHeader = true;
            boolean gotProtocol = false;
            String line = null;
            HttpRequest req = new HttpRequest(connectionId);            
            req.setRemoteAddress(socket.getInetAddress().getHostAddress());
            while (readingHeader) {
                line = getLine();
                if (line != null) {
                    if (line.trim().length() == 0) {
                        readingHeader = false;                    
                    } else {
                        if (!gotProtocol) {
                            StringTokenizer st = new StringTokenizer(line);
                            int protocolCheck = st.countTokens();
                            if (protocolCheck != 3) {
                                throw new Exception("Protocol error in request line: " + line);
                            }
                            int i = 0;
                            while(st.hasMoreTokens()) {
                                switch (i) {
                                    case 0: req.setRequestType(st.nextToken());
                                            break;
                                    case 1: req.setRequestContext(st.nextToken());
                                            break;
                                    case 2: req.setProtocol(st.nextToken());
                                            break;
                                }
                                ++i;
                            }               
                            gotProtocol = true;
                        } else {
                            splitField(line, req);                            
                        }                        
                    }
                }
            }
            if (!req.contentLengthSet()) {
                // See if we've been sent a chunked request, if so read the stream
                // and calculate the actual content length with all the chunking stripped.
                //
                // Read the input into a byte array, and make the request input stream a
                // ByteArrayInputStream. We might as well process the chunking and read the
                // whole thing at this point, because the request handler will anyway, and
                // it means that we can set the content length correctly.
                //
                String enc = req.getField("transfer-encoding");
                if ((enc == null) || (!enc.equalsIgnoreCase("chunked"))) {
                    throw new Exception("Protocol error, content length not set in request");
                }
                // Chunked, need to pre-read the stream, de-chunk, and set the content
                // length to the real value. Also set the request input stream to a
                // ByteArrayInputStream based on the buffered input
                req.setInputStream(bufferChunkedInput(req, socket.getInputStream()));
            } else {
                // Not chunked, proper content length set...
                req.setInputStream(socket.getInputStream());
            }
            HttpResponse resp = new HttpResponse(socket.getOutputStream());
            req.setResponse(resp);
            server.addRequest(req);
        }
        catch (Exception e) {
            exception = e;
            String report = new String("Exception creating request on socket " + connectionId + ", request processing exitting: " + e.getMessage());
            System.err.println(report);
            try {
                LastResortReporter.report(report, socket.getOutputStream());
            }
            catch (Exception e1) {
                System.err.println("... socket output stream dead");
            }
        }
    }

    private ByteArrayInputStream bufferChunkedInput(HttpRequest req, InputStream is)
            throws Exception
    {
        // Skip any leading blank lines, then when not reading chunk get chunk
        // size (hex digits) until first space or CRLF. After CRLF read chunk size
        // bytes and add to content length running total. Then skip CRLF and read
        // new chunk size. Repeat until end.

        int runningTotal = 0;
        int chunksize = 0;
        byte[] currentBuffer = null;
        ArrayList<byte[]> runningBuffer = new ArrayList<byte[]>();

        // Read...
        while ((currentBuffer = readChunk(is)) != null) {
            runningBuffer.add(currentBuffer);
            runningTotal += currentBuffer.length;
        }

        chunkedMessageBuffer = new byte[runningTotal];
        int bpos = 0;
        for (byte[] b : runningBuffer) {
            for (int i = 0; i < b.length; i++) {
                chunkedMessageBuffer[bpos] = b[i];
                bpos++;
            }
        }
        req.setContentLength(runningTotal);
        return new ByteArrayInputStream(chunkedMessageBuffer);
    }

    private byte[] readChunk(InputStream is)
        throws Exception
    {
        int c = 0;
        boolean readingChunkSize = true;
        boolean leader = true;
        StringBuilder sb = new StringBuilder();
        while (readingChunkSize) {
            c = is.read();
            // Skip any leading whitespace (mainly extra blank lines)
            if (leader) {
                if (Character.isLetterOrDigit((char)c)) {
                    sb.append((char)c);
                    leader = false;
                }
            } else {
                if (Character.isLetterOrDigit((char)c)) {
                    sb.append((char)c);
                } else {
                    readingChunkSize = false;
                }
            }
        }
        int chunkSize = Integer.parseInt(sb.toString(), 16);
        if (chunkSize == 0) {
            return null;
        }
        // Skip to the next CRLF, or at least LF
        while ((c = is.read()) != '\n') {}
        byte[] b = new byte[chunkSize];
        int read = 0;
        while (read < chunkSize) {
            int r = is.read(b, read, b.length - read);
            if (r == -1) {
                throw new Exception("Premature EOF reading chunked input. Read: " + read + " Expected: " + chunkSize);
            }
            read += r;
        }
        return b;
    }

    private void splitField(String line, HttpRequest req) 
        throws Exception
    {
        int i;
        int colon = -1;
        String value;
        
        if ((line == null) || (line.trim().length() == 0)) {
            return;
        }
        for (i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ':') {
                colon = i;
                break;
            }
        }
        if (colon == -1) {
            throw new Exception("Invalid header: " + line);
        }
        req.setHeader(line.substring(0, colon), line.substring(colon + 1).trim());
    }
}
