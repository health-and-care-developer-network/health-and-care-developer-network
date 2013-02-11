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
package org.warlock.validator;

import javax.xml.transform.stream.StreamSource;
import java.io.Writer;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import javax.xml.transform.sax.SAXSource;
import org.apache.xerces.jaxp.validation.XMLSchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
/**
 * SAX-based schema validator used for Spine messages.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class SaxValidator 
    implements XmlValidatorErrorReporter
{
    
    private StreamSource messageStream = null;
    private InputSource messageSource = null;
    private StreamSource schema = null;
    private ErrorHandler errorHandler = null;
    private boolean errorsDetected = false;
    private String errorString = null;
    
    /**
     * Creates a new instance of SaxValidator
     */
    public SaxValidator(InputSource val, StreamSource sch) {
        messageSource = val;
        schema = sch;
    }


    public SaxValidator(StreamSource val, StreamSource sch) {
        messageStream = val;
        schema = sch;
    }

    @Override
    public String getErrorString() { return errorString; }

    @Override
    public boolean getErrorDetected() { return errorsDetected; }

    public void validate(ValidatorServiceErrorHandler v)
        throws XmlValidatorException
    {
        errorHandler = v;
        doValidation();
    }

    public void validate(Writer out) 
        throws XmlValidatorException
    {
        errorHandler = new ReportingErrorHandler(out);
        doValidation();
    }
    
    public boolean validate() 
        throws XmlValidatorException
    {
        errorHandler = new CheckingErrorHandler(this);
        doValidation();
        return errorsDetected;
    }

    @Override
    public void setErrorDetected() { errorsDetected = true; }

    @Override
    public void setErrorString(String s) { errorString = s; }
    
    private void doValidation() 
        throws XmlValidatorException
    {
        Schema s = null;
        Validator v = null;
//        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        XMLSchemaFactory schemaFactory = new XMLSchemaFactory();
        try {
            s = schemaFactory.newSchema(schema);
            v = s.newValidator();
            v.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
            v.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
            v.setErrorHandler(this.errorHandler);
        }
        catch (Exception e) {
            throw new XmlValidatorException("Failed to initialise: " + e.getMessage());
        }
        try {
            if (messageStream != null) {
                v.validate(messageStream);
            } else {
                if (messageSource != null) {
                    v.validate(new SAXSource(messageSource));
                } else {
                    throw new Exception("SaxValidator: No input given");
                }
            }
        }
        catch (Exception e) {
            throw new XmlValidatorException("Validation failed: " + e.getMessage());
        }
    }
}
