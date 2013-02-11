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
 * Class represnting a MIME part in a XOP message.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 

public class XopPart {

    private ContentType contentType = null;
    private String content = null;
    private String id = null;
    private boolean gotHeader = false;
    
    public XopPart() {}
    
    public void setContentType(String s) {
        contentType = new ContentType(s);
    }
    public void setContent(String s) { content = s; }
    public void setId(String s) { id = s; }
    public void setHeader() { gotHeader = true; }
    
    public ContentType getContentType() { return contentType; }
    public String getContent() { return content; }
    public String getId() { return id; }
    public boolean hasHeader() { return gotHeader; }
}
