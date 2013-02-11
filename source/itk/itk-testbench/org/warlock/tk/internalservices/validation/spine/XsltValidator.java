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
import org.warlock.xsltransform.TransformManager;
/**
 * SpineMessage version of the XsltValidator from the TKW validate mode for
 * ITK messages. Supports declaration of "where to look" (ebXML, HL7 or SOAP).
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class XsltValidator
    implements ValidationCheck
{
    private String transformFile = null;
    private String checkterm = "";

    private static final int CHECKUNDEFINED = 0;
    private static final int EBXML = 1;
    private static final int HL7 = 2;
    private static final int SOAP = 3;

    private int checkPart = CHECKUNDEFINED;
    private String type = null;

    public XsltValidator() {}

    @Override
    public ValidationReport[] validate(SpineMessage sm)
            throws Exception
    {
        String o = null;
        switch (checkPart) {
            case EBXML:
                o = sm.getEbXmlPart();
                break;

            case HL7:
                o = sm.getHL7Part();
                break;

            case SOAP:
                o = sm.getSoap();
                break;
        }
        if (o == null) {
            ValidationReport ve = new ValidationReport("Failed");
            StringBuilder sb = new StringBuilder(transformFile);
            sb.append(" returned no match because the sample could not be read correctly.");
            ValidationReport[] vreport = new ValidationReport[1];
            vreport[0] = ve;
            return vreport;
        }
        
        ValidationReport report[] = null;
        try {
            StringBuilder sb = new StringBuilder(transformFile);
            TransformManager t = TransformManager.getInstance();
            String x = t.doTransform(transformFile, o);
            if (x.toUpperCase().contains(checkterm.toUpperCase())) {
                ValidationReport v = new ValidationReport("Failed");
                sb.append(" returned:\n");
                sb.append(x);
                v.setTest(sb.toString());
                report = new ValidationReport[1];
                report[0] = v;
            } else {
                ValidationReport v = new ValidationReport("Pass");
                v.setPassed();
                sb.append(" returned:\n");
                sb.append(x);
                v.setTest(sb.toString());
                report = new ValidationReport[1];
                report[0] = v;
            }
        }
        catch (Exception e) {
            ValidationReport ex = new ValidationReport("Exception executing validation " + transformFile + " : " + e.getMessage());
            report = new ValidationReport[1];
            report[0] = ex;
        }
        return report;
    }

    @Override
    public void setResource(String t) { transformFile = t; }

    @Override
    public String getSupportingData() { return null; }
    
    @Override
    public void setData(String d) { 
        if (d != null) {
            checkterm = d;
        }
    }

    @Override
    public void initialise()
            throws Exception
    {
        if (checkterm == null) {
            throw new Exception("No term provided to check for success");
        }
        TransformManager t = TransformManager.getInstance();
        try {
            t.addTransform(transformFile, transformFile);
        }
        catch (Exception e) {
            throw new Exception("Failed to initialise transform " + transformFile + " : " + e.getMessage());
        }
    }

    @Override
    public void setType(String t) {
        type = t;
        String tgt = null;
        int uscore = type.indexOf('_');
        if (uscore == -1) {
            System.err.println("Spine message validation part not given, assuming HL7");
            checkPart = HL7;
        } else {
            tgt = type.substring(0, uscore);
            if (tgt.contentEquals("ebxml")) {
                checkPart = EBXML;
            } else {
                if (tgt.contentEquals("hl7")) {
                    checkPart = HL7;
                } else {
                    if (tgt.contentEquals("soap")) {
                        checkPart = SOAP;
                    }
                }
            }
        }
    }

    @Override
    public ValidatorOutput validate(String o, boolean stripHeader) 
        throws Exception
    {
        throw new Exception("Only call validate(String o, boolean stripHeader) for ITK validations");
    }
}
