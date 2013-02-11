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
import java.io.InputStream;
import org.xml.sax.InputSource;
import org.warlock.tk.internalservices.phxmladapter.phxmlconverter;
import org.warlock.tk.internalservices.PHAdapter;
import org.apache.commons.codec.binary.Base64;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import org.warlock.util.CfHNamespaceContext;
/**
 *
 * @author Damian Murphy <murff@warlock.org>
 */
public class PHSynchronousXPathAssertionPassFailCheck 
    extends SynchronousXPathAssertionPassFailCheck 
{
    // This assumes that there is only one ITK payload element with base64 content
    // in it. As of MArch 2012 this assumption is good for all ITK message definitions
    // but if that assumption changes, this code will need replacing with something 
    // that can handle picking specific v2 payloads out of the distribution
    // envelope.
    //
    private static final String XPATH = "//itk:payloads/itk:payload/text()";
    private static XPathExpression v2extractor = null;
    
    public PHSynchronousXPathAssertionPassFailCheck() 
            throws Exception
    {
        XPath x = XPathFactory.newInstance().newXPath();
        x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
        v2extractor = x.compile(XPATH);
    }
    
    @Override
    public boolean passed(Script s, InputStream in)
            throws Exception
    {
        String responseBody = getResponseBody(in);
        String hl7v2response = getHL7v2(responseBody);
        InputSource is = new InputSource(new StringReader(hl7v2response));
        return doChecks(s, is);
    }
    
    protected String getHL7v2(String rb)
            throws Exception
    {
        // Dig the base64 ph out of the responseBody, turn it into xml,
        // then make the input source and call doChecks()
        //
        StreamSource s = new StreamSource(new StringReader(rb));
        String b64v2 = v2extractor.evaluate(s);
        Base64 b64 = new Base64();
        byte[] in = b64v2.getBytes();
        byte[] out = b64.decode(in);
        String hl7v2 = new String(out);        
        String mtype = PHAdapter.findMessageType(hl7v2);        
        return phxmlconverter.getInstance().convert(mtype, hl7v2);
    }
}
