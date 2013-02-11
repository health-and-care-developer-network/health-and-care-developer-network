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
package org.warlock.xsltransform;
import java.util.HashMap;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
/**
 * URI resolver for object references in XSL transforms, where the object is
 * packaged in the same jar file as the transform.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class ResourceURIResolver 
    implements javax.xml.transform.URIResolver
{

    private HashMap<String,String> resources = null;
    
    public ResourceURIResolver()
    {
        resources = new HashMap<String,String>();
    }
    
    public void addResource(String name, InputStream is) 
            throws Exception
    {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        resources.put(name, sb.toString());
    }
    
    @Override
    public Source resolve(String href, String base)
            throws TransformerException
    {
        try {
            String r = resources.get(href);
            if (r == null) {
                throw new Exception("No such resource: " + href);
            }
            return new StreamSource(new StringReader(r));
        }
        catch (Exception e) {
            throw new TransformerException(e);
        }
    }
}
