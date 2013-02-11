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

package org.warlock.tk.internalservices.testautomation;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.warlock.util.CfHNamespaceContext;
import java.io.FileReader;
import java.io.BufferedReader;
import org.warlock.util.ConfigurationTokenSplitter;
 /**
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class SingleRecordXpathResponseExtractor 
    implements ResponseExtractor
{
    private String name = null;
    private String configFile = null;
    private ArrayList<DataSource> registeredDataSources = new ArrayList<DataSource>();
    private HashMap<XPathExpression,String> extracts = new HashMap<XPathExpression,String>();
    private XPathExpression recordid = null;
    
    public SingleRecordXpathResponseExtractor() {}

    @Override
    public void init(String[] line)
            throws Exception
    {
        name = line[0];
        configFile = line[2];
        load();
    }
  
    @Override
    public void extract(String in)
            throws Exception
    {
        // This takes a String because whilst it is expected to be called by
        // the pass/fail check (which gets an InputStream), the check will have
        // already consumed the InputStream - so if it wants to run an extractor
        // it has to save the stream to a string, and pass that string here.
        //
        // When extracts extract something, they call setValue() on any registered
        // data sources.
        //
        // The expression "recordid" is used to determine the record id. If there
        // isn't one, use "". Otherwise, cycle through them.
        
        String recordIdentity = null;
        if (recordid == null) {
            recordIdentity = "";
        } else {
            InputSource is = new InputSource(new StringReader(in));
            recordIdentity = recordid.evaluate(is);
        }
        for (XPathExpression xpe : extracts.keySet()) {
            String tag = extracts.get(xpe);
            InputSource is = new InputSource(new StringReader(in));
            String value = xpe.evaluate(is);
            for (DataSource d : registeredDataSources) {
                if (!d.isReadOnly()) {
                    d.setValue(recordIdentity, tag, value);
                }
            }
        }
    }
    
    private void load() 
            throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(configFile));
        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            String[] parts = (new ConfigurationTokenSplitter(line)).split();
            XPath x = XPathFactory.newInstance().newXPath();
            x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
            XPathExpression xpe = x.compile(parts[1]);
            if (parts.length == 3) {
                if (parts[2].contentEquals("ID")) {
                    recordid = xpe;
                }
            }
            if (xpe != recordid) {  // Yes, a check for physically the same instance
                extracts.put(xpe, parts[0]);
            }
        }
        br.close();
    }
    
    @Override
    public void registerDatasourceListener(DataSource c) {
        registeredDataSources.add(c);
    }
    
    @Override
    public String getName() { return name; }
    
}
