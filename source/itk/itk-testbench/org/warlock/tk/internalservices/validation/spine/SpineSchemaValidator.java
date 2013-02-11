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
import org.warlock.tk.internalservices.validation.*;
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
import org.warlock.tk.internalservices.validation.SchemaValidationReporter;
import org.warlock.tk.internalservices.validation.ValidatorOutput;

 /**
 * Schema-validate the HL7 part of the given SpineMessage.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class SpineSchemaValidator
    extends SchemaValidationReporter
    implements ValidationCheck
{
    protected String schemaFile = null;
    protected String startingXpath = null;
    protected static final String HEADERSTRIPPER = "/soap:Envelope/soap:Body/*[1]";

    protected static XMLNamespaceContext nsContext = null;
    protected XPathExpression validationRootXpath = null;
    protected XPathExpression headerStripper = null;

    public SpineSchemaValidator()
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
    public String getSupportingData() { return null; }
    
    
    @Override
    public ValidationReport[] validate(SpineMessage sm)
            throws Exception
    {
        String o = sm.getHL7Part();
        if (o == null) {
            ValidationReport[] e = new ValidationReport[1];
            e[0] = new ValidationReport("Error: null content");
            return e;
        }
        try {
            validatorExceptions = new ArrayList<ValidationReport>();
            ValidatorServiceErrorHandler v = new ValidatorServiceErrorHandler(this);
            StringReader subjectReader = new StringReader(o);
            StreamSource subjectSource = new StreamSource(subjectReader);
            File f = new File(schemaFile);
            StreamSource schemaSource = new StreamSource(f);
            if (startingXpath == null) {
                InputSource is = new InputSource(subjectReader);
                SaxValidator sv = new SaxValidator(is, schemaSource);
                sv.validate(v);
            } else {
                doDomValidation(subjectSource, schemaSource,v, false);
            }
        }
        catch (Exception e1) {
            ValidationReport[] e = new ValidationReport[1];
            e[0] = new ValidationReport("Error during validation: " + e1.getMessage());
            return e;
        }
        if (validatorExceptions.size() == 0) {
            ValidationReport[] e = new ValidationReport[1];
            e[0] = new ValidationReport("Pass");
            e[0].setTest(schemaFile);
            e[0].setPassed();
            return e;
        }
        return (ValidationReport[])validatorExceptions.toArray(new ValidationReport[validatorExceptions.size()]);
    }


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
    public ValidatorOutput validate(String o, boolean stripHeader) 
        throws Exception
    {
        throw new Exception("Only call validate(String o, boolean stripHeader) for ITK validations");
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
                n = (Node)validationRootXpath.evaluate(doc, XPathConstants.NODE);
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
