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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
/**
 * OutputStream for HTTP responses which tells the response when it is ready to be written to the network.
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class NotifyOnFlushOutputStream 
    extends ByteArrayOutputStream
{
    private HttpResponse response;
    /**
     * Creates a new instance of NotifyOnFlushOutputStream
     */
    NotifyOnFlushOutputStream(HttpResponse r) {
        super();
        response = r;
    }
    
    @Override
    public void flush() 
        throws IOException
    {
        super.flush();        
        response.canWrite();
    }
}
