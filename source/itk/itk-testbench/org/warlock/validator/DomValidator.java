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
import org.apache.xerces.jaxp.validation.XMLSchemaFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.w3c.dom.Node;
 /**
 * DOM- based schema validator used by the Spine validator mode.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class DomValidator 
    implements XmlValidatorErrorReporter
{
    
    private Node message = null;
    private StreamSource schema = null;
    private ErrorHandler errorHandler = null;
    private boolean errorsDetected = false;
    private String errorString = null;
    
    /**
     * Creates a new instance of SaxValidator
     */
    public DomValidator(Node n, StreamSource sch) {
        message = n;
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
        XMLSchemaFactory schemaFactory = new XMLSchemaFactory();
        try {
            s = schemaFactory.newSchema(schema);
            v = s.newValidator();
            v.setErrorHandler(this.errorHandler);
        }
        catch (Exception e) {
            throw new XmlValidatorException("Failed to initialise: " + e.getMessage());
        }
        try {
            v.validate(new DOMSource(this.message));
        }
        catch (Exception e) {
            throw new XmlValidatorException("Validation failed: " + e.getMessage());
        }
    }
}
