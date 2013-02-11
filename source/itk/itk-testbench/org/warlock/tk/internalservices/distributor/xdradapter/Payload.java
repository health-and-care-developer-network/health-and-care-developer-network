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

package org.warlock.tk.internalservices.distributor.xdradapter;

/**
 * Encapsulation of a payload and its associated ITK DistributionEnvelope
 * manifest information.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class Payload {

    protected boolean base64 = false;
    protected boolean encrypted = false;
    protected boolean compressed = false;
    protected boolean metadata = false;
    protected String mimetype = "text/xml";
    protected String id = null;
    protected String filename = null;
    protected Object content = null;

    public Payload() {}

    public void setBase64() { base64 = true; }
    public void setEncrypted() { encrypted = true; }
    public void setCompressed() { compressed = true; }
    public void setMetadata() { metadata = true; }
    public void setMimetype(String m) { mimetype = m; }
    public void setId(String i) { 
        if (i.startsWith("#")) {
            id = i.substring(1);
        } else {
            id = i; 
        }
    }
    public void setFilename(String f) { filename = f; }
    public void setContent(Object o) { content = o; }
    public boolean hasFileanme() { return !(filename == null); }
    public boolean isBase64() { return base64; }
    public boolean isEncrypted() { return encrypted; }
    public boolean isCompressed() { return compressed; }
    public boolean isMetadata() { return metadata; }

    public String getMimetype() { return mimetype; }
    public String getFilename() { return filename; }
    public String getId() { return id; }
    public Object getContent() { return content; }
    
}
