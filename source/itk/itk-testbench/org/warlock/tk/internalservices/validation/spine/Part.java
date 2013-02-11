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

package org.warlock.tk.internalservices.validation.spine;
import java.util.HashMap;
import org.w3c.dom.Document;
 /**
 * Container class for a MIME part, from a multi-part ebXML Spine message.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
class Part {
    private HashMap<String, String> headers = new HashMap<String, String>();
    private String body = null;
    private Document dom = null;

    Part() {}

    void setDocument(Document d) { dom = d; }
    void setBody(String b) { body = b; }
    void addHeader(String hdr, String val) { headers.put(hdr, val); }
    String getBody() { return body; }
    Document getDocument() { return dom; }
}
