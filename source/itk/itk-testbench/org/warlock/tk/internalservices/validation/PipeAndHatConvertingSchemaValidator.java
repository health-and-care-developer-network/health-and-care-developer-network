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
package org.warlock.tk.internalservices.validation;
import org.warlock.tk.internalservices.phxmladapter.phxmlconverter;
import org.warlock.tk.internalservices.PHAdapter;
import org.apache.commons.codec.binary.Base64;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Node;
import java.io.StringReader;

 /**
 * Subclass of the SchemaValidator to apply the PH-to-XML transform to a
 * captured, on-the-wire HL7v2 delimited message before schema validating.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class PipeAndHatConvertingSchemaValidator 
    extends SchemaValidator
{

    private String hl7v2xmloutput = null;
    
    public PipeAndHatConvertingSchemaValidator() 
            throws Exception
    {
        super();
    }

    @Override
    public String getSupportingData() { return hl7v2xmloutput; }
        
    @Override
    public ValidatorOutput validate(String o, boolean stripHeader)
    {
        try {
            Node docroot = null;
            StreamSource s = new StreamSource(new StringReader(o));
            if (startingXpath == null) {
                docroot = getValidationRoot(s, stripHeader);
            } else {
                docroot = getValidationRoot(s, false);
            }
            if (docroot.getNodeType() == Node.TEXT_NODE) {
                rootFound = true;
            } else {
                throw new Exception("HL7v4 base64 text node not found");
            }
            String b64msg = docroot.getTextContent();
            String msg = unbase64(b64msg);
            String mtype = PHAdapter.findMessageType(msg);
            hl7v2xmloutput = phxmlconverter.getInstance().convert(mtype, msg);            
        }
        catch (Exception e) {
            ValidationReport[] ve = new ValidationReport[1];
            ve[0] = new ValidationReport("Error converting PH-to-XML: " + e.toString());
            ValidatorOutput vr = new ValidatorOutput(null, ve);
            return vr;
        }        
        return super.validate(hl7v2xmloutput, false);
    }

    private String unbase64(String m)
            throws Exception
    {
        Base64 b64 = new Base64();
        byte[] in = m.getBytes();
        byte[] out = b64.decode(in);
        return new String(out);
    }
        
}
