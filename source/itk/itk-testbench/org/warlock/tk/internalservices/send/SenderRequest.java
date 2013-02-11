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
package org.warlock.tk.internalservices.send;

/**
 * Encapsulates a request to send a message. The message may be sent either
 * via a SOAP web service or written to a queue.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class SenderRequest {

    private String originalFileName = null;
    private String payload = null;
    private String wrapperTemplate = null;
    private String address = null;
    private String action = null;
    private String relatestomessageid = null;
    private int chunkSize = 0;

    public SenderRequest(String p, String w, String a) {
        payload = p;
        wrapperTemplate = w;
        address = a;
    }

    public void setOriginalFileName(String s) { originalFileName = s; }
    public String getOriginalFileName() { return originalFileName; }
    
    public void setChunkSize(int c) { chunkSize = c; }
    public void setRelatesTo(String r) { relatestomessageid = r; }
    public void setAction(String a) { action = a; }
    public String getAction() { return action; }
    public String getRelatesTo() { return relatestomessageid; }

    public String getAddress() { return address; }
    public String getPayload() { return payload; }
    public String getWrapperTemplate() { return wrapperTemplate; }
    public int chunkSize() { return chunkSize; }
}
