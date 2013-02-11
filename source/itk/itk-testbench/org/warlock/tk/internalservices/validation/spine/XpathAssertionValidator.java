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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import org.warlock.util.CfHNamespaceContext;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.StringReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 * SpineMessage version of the XpathAssertionValidator from the TKW validate mode
 * for ITK messages. Additionally supports case-insensitive assertions.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class XpathAssertionValidator
    implements ValidationCheck
{
    private static final int XPATHNOTDEFINED = 0;
    private static final int XPATHEQUALS = 1;
    private static final int XPATHNOTEQUALS = 2;
    private static final int XPATHCONTAINS = 3;
    private static final int XPATHNOTCONTAINS = 4;
    private static final int XPATHEXISTS = 5;
    private static final int XPATHNOTEXISTS = 6;
    private static final int XPATHMATCHES = 7;
    private static final int XPATHNOTMATCHES = 8;
    private static final int XPATHCOMPARE = 9;
    private static final int XPATHNOTCOMPARE = 10;
    private static final int XPATHEQUALSIGNORECASE = 11;
    private static final int XPATHNOTEQUALSIGNORECASE = 12;
    private static final int XPATHCONTAINSIGNORECASE = 13;
    private static final int XPATHNOTCONTAINSIGNORECASE = 14;

    private static final int CHECKUNDEFINED = 0;
    private static final int EBXML = 1;
    private static final int HL7 = 2;
    private static final int SOAP = 3;

    private static final String[] TYPES = {"xpathequals", "xpathnotequals", "xpathcontains", "xpathnotcontains",
        "xpathexists", "xpathnotexists", "xpathmatches", "xpathnotmatches", "xpathcompare", "xpathnotcompare",
        "xpathequalsignorecase", "xpathnotequalsignorecase","xpathcontainsignorecase", "xpathnotcontainsignorecase"};

    private String xpath = null;
    private String type = null;
    private String value = null;
    private int comparisonType = XPATHNOTDEFINED;

    private int checkPart = CHECKUNDEFINED;

    private Pattern regexPattern = null;
    private XPathExpression checkExpression = null;
    private XPathExpression comparisonExpression = null;

    @Override
    public void setResource(String t) { xpath = t; }

    @Override
    public void setType(String t) { type = t; }

    @Override
    public void setData(String d)
    {
        value = d;
        if ((comparisonType == XPATHEQUALSIGNORECASE) || (comparisonType == XPATHNOTEQUALSIGNORECASE)
                || (comparisonType == XPATHNOTCONTAINSIGNORECASE) || (comparisonType == XPATHCONTAINSIGNORECASE)) {
            value = value.toLowerCase();
            return;
        }
        if ((comparisonType == XPATHCOMPARE) || (comparisonType == XPATHNOTCOMPARE)) {
            if (comparisonExpression != null) {
                return;
            }
            if (value != null) {
                XPath x = XPathFactory.newInstance().newXPath();
                x = XPathFactory.newInstance().newXPath();
                x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
                try {
                    comparisonExpression = x.compile(value);
                }
                catch (Exception e) {
                    System.err.println("Spine XPathAssertionValidator: Failed to compile second XPath expression: " + value);
                }
            }
        }
    }

    @Override
    public String getSupportingData() { return null; }
    
    @Override
    public void initialise()
            throws Exception
    {
        String tgt = null;
        String ctype = null;
        int uscore = type.indexOf('_');
        if (uscore == -1) {
            System.err.println("Spine message validation part not given, assuming HL7");
            ctype = type;
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
            ctype = type.substring(uscore + 1);
        }
        for (int i = 0; i < TYPES.length; i++) {
            if (ctype.contentEquals(TYPES[i])) {
                comparisonType = i + 1;
                break;
            }
        }
        if (comparisonType == XPATHNOTDEFINED) {
            throw new Exception("Unrecognised Xpath check type " + type);
        }
        XPath x = XPathFactory.newInstance().newXPath();
        x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
        try {
            checkExpression = x.compile(xpath);
        }
        catch (Exception e) {
            System.err.println("Error compiling expression: " + xpath);
            throw e;
        }

        if ((comparisonType == XPATHMATCHES) || (comparisonType == XPATHNOTMATCHES)) {
            regexPattern = Pattern.compile(value);
        }
        if ((comparisonType == XPATHCOMPARE) || (comparisonType == XPATHNOTCOMPARE)) {
            if (comparisonExpression == null) {
                if (value != null) {
                    x = XPathFactory.newInstance().newXPath();
                    x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
                    try {
                        comparisonExpression = x.compile(value);
                    }
                    catch (Exception e) {
                        System.err.println("Error compiling expression: " + value);
                        throw e;
                    }
                }
            }
        }
        if ((comparisonType == XPATHEQUALSIGNORECASE) || (comparisonType == XPATHNOTEQUALSIGNORECASE)
                || (comparisonType == XPATHCONTAINSIGNORECASE) || (comparisonType == XPATHNOTCONTAINSIGNORECASE)) {
            if (value != null) {
                value = value.toLowerCase();
            }
        }
    }

    @Override
    public ValidatorOutput validate(String o, boolean stripHeader)
        throws Exception
    {
        throw new Exception("Only call validate(String o, boolean stripHeader) for ITK validations");
    }

    private ValidationReport[] doExistsValidation(SpineMessage sm)
        throws Exception
    {
        Document d = null;
        ValidationReport ve = null;
        StringBuilder sb = new StringBuilder("Xpath ");
        sb.append(xpath);
        switch (checkPart) {
            case EBXML:
                d = sm.getEbXmlDoc();
                break;

            case HL7:
                d = sm.getHL7Doc();
                break;

            case SOAP:
                d = sm.getSOAPDoc();
                break;
        }
        if (d == null) {
            ve = new ValidationReport("Failed");
            sb.append(" returned no match because the sample could not be read correctly.");
            ValidationReport[] vreport = new ValidationReport[1];
            vreport[0] = ve;
            return vreport;
        }
        NodeList nl = (NodeList)checkExpression.evaluate(d, javax.xml.xpath.XPathConstants.NODESET);
        switch (comparisonType) {
            case XPATHEXISTS:
                if (nl.getLength() > 0) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" returned content");
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match");
                }
                break;
            case XPATHNOTEXISTS:
                if (nl.getLength() == 0) {
                    sb.append(" returned no match");
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned content when it was expected to return no match");
                }
                break;

        }
        ve.setTest(sb.toString());
        ValidationReport[] vreport = new ValidationReport[1];
        vreport[0] = ve;
        return vreport;
    }

    @Override
    public ValidationReport[] validate(SpineMessage sm)
        throws Exception
    {
        if ((comparisonType == XPATHEXISTS) || (comparisonType == XPATHNOTEXISTS)) {
            return doExistsValidation(sm);
        }
        ValidationReport ve = null;
        Matcher m = null;
        StringBuilder sb = new StringBuilder("Xpath ");
        sb.append(xpath);
        String o = null;
        // Get the appropriate string out of the SpineMessage depending on what
        // part of the message we're wanting to validate.
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
            ve = new ValidationReport("Failed");
            sb.append(" returned no match because the sample could not be read correctly.");
            ValidationReport[] vreport = new ValidationReport[1];
            vreport[0] = ve;
            return vreport;
        }
        InputSource is = new InputSource(new StringReader(o));
        String r = checkExpression.evaluate(is);
        String v = null;
        if (comparisonExpression != null) {
            is = new InputSource(new StringReader(o));
            v = comparisonExpression.evaluate(is);
        }
        switch(comparisonType) {
            case XPATHCOMPARE:
                if (r == null) {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match, it was expected to return a value \"");
                    sb.append(value);
                    sb.append("\"");
                } else {
                    if (r.contentEquals(v)) {
                        ve = new ValidationReport("Pass");
                        ve.setPassed();
                        sb.append(value);
                        sb.append(" and ");
                        sb.append(checkExpression);
                        sb.append(" returned \"");
                        sb.append(v);
                        sb.append("\"");
                    } else {
                        ve = new ValidationReport("Failed ");
                        sb.append(checkExpression);
                        sb.append(" returned \"");
                        sb.append(r);
                        sb.append("\" when ");
                        sb.append(value);
                        sb.append(" returned \"");
                        sb.append(v);
                        sb.append("\" when a match was expected");
                    }
                }
                break;
            case XPATHNOTCOMPARE:
                if (r == null) {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match, it was expected to return a value \"");
                    sb.append(value);
                    sb.append("\"");
                } else {
                    if (!r.contentEquals(v)) {
                        ve = new ValidationReport("Pass");
                        ve.setPassed();
                        sb.append(value);
                        sb.append(" returned \"");
                        sb.append(r);
                        sb.append("\" and ");
                        sb.append(checkExpression);
                        sb.append(" returned \"");
                        sb.append(v);
                        sb.append("\"");
                    } else {
                        ve = new ValidationReport("Failed ");
                        sb.append(checkExpression);
                        sb.append(" and ");
                        sb.append(value);
                        sb.append(" returned \"");
                        sb.append(v);
                        sb.append("\" when no  match was expected");
                    }
                }
                break;
            case XPATHEQUALSIGNORECASE:
                if (r != null) {
                    r = r.toLowerCase();
                }
            case XPATHEQUALS:
                if (r == null) {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match, it was expected to return a value \"");
                    sb.append(value);
                    sb.append("\"");
                } else {
                    if (r.contentEquals(value)) {
                        ve = new ValidationReport("Pass");
                        ve.setPassed();
                        sb.append(" returned \"");
                        sb.append(value);
                        sb.append("\"");
                    } else {
                        ve = new ValidationReport("Failed");
                        sb.append(" returned \"");
                        sb.append(r);
                        sb.append("\" when \"");
                        sb.append(value);
                        sb.append("\" was expected");
                    }
                }
                break;
            case XPATHNOTEQUALSIGNORECASE:
                if (r != null) {
                    r = r.toLowerCase();
                }
            case XPATHNOTEQUALS:
                if (r == null) {
                    ve = new ValidationReport("Warning");
                    sb.append(" returned no match, it was expected to return a value other than \"");
                    sb.append(value);
                    sb.append("\"");
                } else {
                    if (!r.contentEquals(value)) {
                        ve = new ValidationReport("Pass");
                        ve.setPassed();
                        sb.append(" does not return \"");
                        sb.append(value);
                        sb.append("\"");
                    } else {
                        ve = new ValidationReport("Failed");
                        sb.append(" returned \"");
                        sb.append(value);
                        sb.append("\", it was expected to return something other than that.");
                    }
                }
                break;
            case XPATHCONTAINSIGNORECASE:
                if (r != null) {
                    r = r.toLowerCase();
                }
            case XPATHCONTAINS:
                if (r == null) {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match, it was expected to return a value containing \"");
                    sb.append(value);
                    sb.append("\"");
                } else {
                    if (r.contains(value)) {
                        ve = new ValidationReport("Pass");
                        ve.setPassed();
                        sb.append(" result contains \"");
                        sb.append(value);
                        sb.append("\"");
                    } else {
                        ve = new ValidationReport("Failed");
                        sb.append(" result does not contain \"");
                        sb.append(value);
                        sb.append("\"");
                    }
                }
                break;
            case XPATHNOTCONTAINSIGNORECASE:
                if (r != null) {
                    r = r.toLowerCase();
                }
            case XPATHNOTCONTAINS:
                if (r == null) {
                    ve = new ValidationReport("Warning");
                    sb.append(" expected to return a value not containing \"");
                    sb.append(value);
                    sb.append("\" but returned no match");
                } else {
                    if (r.contains(value)) {
                        ve = new ValidationReport("Failed");
                        sb.append(" contains \"");
                        sb.append(value);
                        sb.append("\" but was expected not to");
                    } else {
                        ve = new ValidationReport("Pass");
                        ve.setPassed();
                        sb.append(" does not contain \"");
                        sb.append(value);
                        sb.append("\"");
                    }
                }
                break;
            case XPATHMATCHES:
                    m = regexPattern.matcher(r);
                    if (m.find()) {
                        ve = new ValidationReport("Pass");
                        ve.setPassed();
                        sb.append(" matches \"");
                        sb.append(value);
                        sb.append("\"");
                    } else {
                        ve = new ValidationReport("Failed");
                        sb.append(" does not match \"");
                        sb.append(value);
                        sb.append("\" when expected");
                    }
                    break;
            case XPATHNOTMATCHES:
                    m = regexPattern.matcher(r);
                    if (m.find()) {
                        ve = new ValidationReport("Failed");
                        sb.append(" matches \"");
                        sb.append(value);
                        sb.append("\" when not expected");
                    } else {
                        ve = new ValidationReport("Pass");
                        ve.setPassed();
                        sb.append(" does not match \"");
                        sb.append(value);
                        sb.append("\"");
                    }
                    break;
            case XPATHNOTDEFINED:
            default:
                throw new Exception("No or invalid comparison type defined");
        }
        ve.setTest(sb.toString());
        ValidationReport[] vreport = new ValidationReport[1];
        vreport[0] = ve;
        return vreport;
    }
}
