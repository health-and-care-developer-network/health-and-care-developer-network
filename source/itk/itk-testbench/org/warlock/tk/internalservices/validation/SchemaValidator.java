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
import org.warlock.validator.SaxValidator;
import org.warlock.validator.DomValidator;
import org.warlock.validator.ValidatorServiceErrorHandler;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.warlock.util.CfHNamespaceContext;
import org.warlock.util.XMLNamespaceContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.warlock.tk.internalservices.validation.spine.SpineMessage;
 /**
 * Parameterised with the schema file name and optionally an Xpath expression to
 * identify the validation root. Applies the given schema to the explicit validation
 * root, the document root, or the SOAP body depending on whether the Xpath
 * expression is given, the file to validate is a "bare" body (using the VALIDATE-AS
 * mechanism or by reading a distribution envelope "service" attribute, or is
 * presented as a complete SOAP envelope.
 * 
 * This class does not produce supportingData.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class SchemaValidator
    extends SchemaValidationReporter
    implements ValidationCheck
{
    protected String schemaFile = null;
    protected String startingXpath = null;
    protected static final String HEADERSTRIPPER = "/soap:Envelope/soap:Body/*[1]";
    
    // Subclasses may already have executed the startingXpath, if they have, they'll
    // set this to prevent the getValidationRoot() trying to extract twice
    //
    protected boolean rootFound = false;

    protected static XMLNamespaceContext nsContext = null;
    protected XPathExpression validationRootXpath = null;
    protected XPathExpression headerStripper = null;

    public SchemaValidator()
        throws Exception
    {
      try {
          nsContext = CfHNamespaceContext.getXMLNamespaceContext();
          XPath x = XPathFactory.newInstance().newXPath();
          x = XPathFactory.newInstance().newXPath();
          x.setNamespaceContext(nsContext);
          headerStripper = x.compile(HEADERSTRIPPER);
      }
      catch(Exception e) {
          throw new Exception("Failed to compile validation root Xpath expression: " + HEADERSTRIPPER + " : " + e.getMessage());
      }
    }

    @Override
    public ValidationReport[] validate(SpineMessage o)
            throws Exception
    {
        throw new Exception("ITK validation. Use SpineValidator service");
    }

    @Override
    public String getSupportingData() { return null; }

    @Override
    public void setResource(String s) { schemaFile = s; }

    @Override
    public void setData(String d) 
        throws Exception
    {
//        if ((d != null) && (d.trim().length() > 0)) {
//            System.err.println("Warning: Additional data passed to schema validator check, currently not supported");
//        }
//        return;
        String data = null;
        if ((d != null) && (d.trim().length() > 0)) {
            data = d;
        }
        startingXpath = data;
        if (data == null) return;
        try {
            XPath x = XPathFactory.newInstance().newXPath();
            x.setNamespaceContext(nsContext);
            validationRootXpath = x.compile(startingXpath);
        }
        catch(Exception e) {
            throw new Exception("Failed to compile validation root Xpath expression: " + startingXpath + " : " + e.getMessage());
        }
    }

    @Override
    public void addValidationExceptionDetail(String s) {
        ValidationReport r = new ValidationReport(s);
        validatorExceptions.add(r);
    }

    @Override
    public void setType(String t) {}

    @Override
    public void initialise()
            throws Exception
    {
        
    }

    @Override
    public ValidatorOutput validate(String o, boolean stripHeader) {
        boolean actuallyRemoveSoapBody = stripHeader;
        if (startingXpath != null) {
            actuallyRemoveSoapBody = false;
        }
        if (o == null) {
            ValidationReport[] e = new ValidationReport[1];
            e[0] = new ValidationReport("Error: null content");
            return new ValidatorOutput(null, e);
        }
        try {
            validatorExceptions = new ArrayList<ValidationReport>();
            ValidatorServiceErrorHandler v = new ValidatorServiceErrorHandler(this);
            StringReader subjectReader = new StringReader(o);
            StreamSource subjectSource = new StreamSource(subjectReader);
            File f = new File(schemaFile);
            StreamSource schemaSource = new StreamSource(f);
            if (actuallyRemoveSoapBody) {
                doDomValidation(subjectSource, schemaSource,v, (startingXpath == null));
            } else {
                if ((rootFound) || (startingXpath == null)) {
                    InputSource is = new InputSource(subjectReader);
                    SaxValidator sv = new SaxValidator(is, schemaSource);
                    sv.validate(v);
                } else {
                    doDomValidation(subjectSource, schemaSource,v, false);
                }
            }
        }
        catch (Exception e1) {
            ValidationReport[] e = new ValidationReport[1];
            e[0] = new ValidationReport("Error during validation: " + e1.getMessage());
            return new ValidatorOutput(null, e);
        }
        if (validatorExceptions.isEmpty()) {
            ValidationReport[] e = new ValidationReport[1];
            e[0] = new ValidationReport("Pass");
            e[0].setTest(schemaFile);
            e[0].setPassed();
            return new ValidatorOutput(null, e);
        }
        return new ValidatorOutput(null, (ValidationReport[])validatorExceptions.toArray(new ValidationReport[validatorExceptions.size()]));
    }

    protected void doDomValidation(StreamSource toValidate, StreamSource schema, ValidatorServiceErrorHandler v, boolean stripHeader)
        throws Exception
    {
        Node n = getValidationRoot(toValidate, stripHeader);
        DomValidator dv = new DomValidator(n, schema);
        dv.validate(v);
    }

    protected Node getValidationRoot(StreamSource s, boolean stripHeader)
            throws Exception
    {
        try {
            InputSource is = new InputSource(s.getReader());
            Node n = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setIgnoringComments(true);
            Document doc = dbf.newDocumentBuilder().parse(is);
            if (stripHeader) {
                n = doc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Body").item(0);
                if (n != null) {
                    NodeList nl = n.getChildNodes();
                    for (int i = 0; i < nl.getLength(); i++) {
                        Node x = nl.item(i);
                        if (x.getNodeType() == Node.ELEMENT_NODE) {
                            return x;
                        }
                    }
                } 
                throw new Exception("Error evaluating validation root - cannot find SOAP Body content");
            } else {
                if (validationRootXpath == null) {
                    n = doc.getDocumentElement();
                } else {
                    n = (Node)validationRootXpath.evaluate(doc, XPathConstants.NODE);
                }
            }
            if (n == null) {
                throw new Exception("Failed to evaluate " + startingXpath + " no match");
            }
            return n;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error evaluating validation root: " + e.getMessage());
        }
    }
}
