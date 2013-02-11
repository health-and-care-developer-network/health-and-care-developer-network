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

 * */
 
package org.warlock.tk.internalservices.distributor.xdradapter;
import java.util.ArrayList;
import java.io.InputStream;
/**
 * Interface implemented by the ITK and XDR classes for the bidirectional adapter.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public interface Transmission
{
    public String getId();
    public ArrayList<Actor> getAuthors();
    public ArrayList<Actor> getRecipients();
    public ArrayList<Payload> getPayloads();
    public MetadataPayload getMetadata();
    public void convert(Transmission t) throws Exception;
    public String serialise() throws Exception;
    public void load(InputStream in, String contenttype) throws Exception;
}
