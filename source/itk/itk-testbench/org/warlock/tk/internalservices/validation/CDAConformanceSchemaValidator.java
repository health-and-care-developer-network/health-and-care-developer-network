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
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.StringReader;
import java.io.InputStream;
import org.warlock.xsltransform.TransformManager;
import org.warlock.xsltransform.ResourceURIResolver;
import javax.xml.transform.URIResolver;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.warlock.util.CfHNamespaceContext;
//
// Temporary
//
// These imports are included to redirect stderr to prevent the CDA template
// transform reporting ambiguous match errors which don't break the output 
// that the template schema is actually applied to. It is a TEMPORARY
// MEASURE pending the output of ongoing C&M work on improving the CDA template
// process, and its transforms. REMOVE THIS WHEN THE NEW TRANSFORM IS 
// AVAILABLE
//
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import org.warlock.util.Logger;
 /**
 * Subclass of the SchemaValidator to apply the "template CDA" transform to a
 * captured, on-the-wire CDA message before schema validating with the template
 * schema. Since the output of schema validation is written with reference to
 * this transform output, and not the actual validated message, this uses the
 * "supporting data" function of the TKW validator mode to save the templated
 * CDA form.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class CDAConformanceSchemaValidator
    extends SchemaValidator
{
    private String conformanceTransformOutput = null;
    
    public CDAConformanceSchemaValidator()
            throws Exception
    {
        super();
    }

    @Override
    public synchronized void initialise()
            throws Exception
    {
        InputStream is = null;
        TransformManager t = TransformManager.getInstance();
        URIResolver u = t.getFactoryURIResolver();

        ResourceURIResolver r = new ResourceURIResolver();
        is = getClass().getResourceAsStream("TemplateLookup.xml");
        r.addResource("TemplateLookup.xml", is);
        is  = getClass().getResourceAsStream("postTxReorder_v2.xslt");        
        r.addResource("postTxReorder_v2.xslt", is);
        is = getClass().getResourceAsStream("SerialisedCDAModel.xml");
        r.addResource("SerialisedCDAModel.xml", is);
        is = getClass().getResourceAsStream("sortedMifClassAtts.xml");
        r.addResource("sortedMifClassAtts.xml", is);

        t.setFactoryURIResolver(r);
        
        is = getClass().getResourceAsStream("TrueCDAToCDALike_v2.xsl");
        t.addTransform("TrueCDAToCDALike_v2.xsl", is);
        is  = getClass().getResourceAsStream("postTxReorder_v2.xslt");        
        t.addTransform("postTxReorder_v2.xslt", is);

        t.setURIResolver("postTxReorder_v2.xslt", r);
        t.setURIResolver("TrueCDAToCDALike_v2.xsl", r);
        
        t.setFactoryURIResolver(u);
    }

    @Override
    public String getSupportingData() { return conformanceTransformOutput; }

    @Override
    public ValidatorOutput validate(String o, boolean stripHeader)
    {
        // Parent just validates. Run the on-the-wire to "conformance
        // form" transform, and then run the validator on the output.

        try {
            Element docroot = null;
            StreamSource s = new StreamSource(new StringReader(o));
            if (startingXpath == null) {
                docroot = (Element)getValidationRoot(s, stripHeader);
            } else {
                docroot = (Element)getValidationRoot(s, false);
            }
            rootFound = true;
            Element n = (Element)docroot.getElementsByTagNameNS(CfHNamespaceContext.HL7NAMESPACE, "ClinicalDocument").item(0);

            // EVIL HACK. Even though "n" contains a ClinicalDocument in the HL7v3 namespace 
            // there is a bug in Saxon which means that because the parent default namespace
            // was ITK, "n"'s default namespace is ITK even though its declared namespace is
            // HL7... So when we serialise the non-prefixed ClinicalDocument the root element
            // gets the wrong namespace. Gak. Making a new document for it is horrible but it
            // fixes the problem.
            //
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document d = dbf.newDocumentBuilder().newDocument();
            d.adoptNode(n);
            StringWriter sw = new StringWriter();
            StreamResult sr = new StreamResult(sw);
            Transformer tx = TransformerFactory.newInstance().newTransformer();
            tx.transform(new DOMSource(n), sr);
            String root = sw.toString();
            // TEMPORARY FIX
            PrintStream stdout = System.out;
            ByteArrayOutputStream logstream = new ByteArrayOutputStream();
            PrintStream logcapture = new PrintStream(logstream);
            System.setOut(logcapture);
            // END TEMPORARY FIX
            conformanceTransformOutput = TransformManager.getInstance().doTransform("TrueCDAToCDALike_v2.xsl", new StringReader(root));
            // TEMPORARY FIX
            System.setOut(stdout);
            Logger.getInstance().log("CDAConformanceSchemaValidator.validate() TEMPORARY REDIRECTION", logstream.toString());
            // END TEMPORARY FIX
            conformanceTransformOutput = conformanceTransformOutput.trim();
        }
        catch (Exception e) {
            ValidationReport[] ve = new ValidationReport[1];
            ve[0] = new ValidationReport("Error converting to Conformance Form: " + e.toString());
            ValidatorOutput vr = new ValidatorOutput(null, ve);
            return vr;
        }
        return super.validate(conformanceTransformOutput, false);
    }
}
