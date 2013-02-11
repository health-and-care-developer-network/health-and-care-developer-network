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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import org.warlock.util.CfHNamespaceContext;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.warlock.tk.internalservices.validation.spine.SpineMessage;
import org.warlock.util.ConfigurationTokenSplitter;
 /**
 * ValidationCheck parameterised with an Xpath expression, a check type and for
 * check types based on equivalence, an assertion value. Comparisons are
 * case sensitive. Supported check types are:
 * 
 * "xpathequals" Pass if Xpath expression result equals the assertion value
 * "xpathnotequals" Pass if Xpath expression result does not equal the assertion value
 * "xpathcontains" Pass if the Xpath expression contains the assertion value
 * "xpathnotcontains" Pass if the Xpath expression does not contain the assertion value
 * "xpathexists" Pass if the Xpath expression points to a node (element, attribute or text) which exists
 * "xpathnotexists" Pass if the Xpath expression does not point to a node which exists
 * "xpathmatches" Pass if the Xpath expression matches the assertion value where the assertion is a regular expression
 * "xpathnotmatches" Pass if the Xpath expression does not matche the assertion value where the assertion is a regular expression
 * "xpathin" Pass if the Xpath expression result is one of the given list of assertion values
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
    private static final int XPATHIN = 9;
    

    private static final String[] TYPES = {"xpathequals", "xpathnotequals", "xpathcontains", "xpathnotcontains",
        "xpathexists", "xpathnotexists", "xpathmatches", "xpathnotmatches", "xpathin"};

    private String[] inList = null;
    private String xpath = null;
    private String type = null;
    private String value = null;
    private int comparisonType = XPATHNOTDEFINED;

    private Pattern regexPattern = null;
    private XPathExpression checkExpression = null;

    @Override
    public void setResource(String t) { xpath = t; }

    @Override
    public ValidationReport[] validate(SpineMessage o)
            throws Exception
    {
        throw new Exception("ITK validation. Use SpineValidator service");
    }

    @Override
    public void setType(String t) { type = t; }

    @Override
    public void setData(String d)
    {
        value = d;
    }

    @Override
    public String getSupportingData() { return null; }
    
    @Override
    public void initialise()
            throws Exception
    {
        for (int i = 0; i < TYPES.length; i++) {
            if (type.contentEquals(TYPES[i])) {
                comparisonType = i + 1;
                break;
            }
        }
        if (comparisonType == XPATHNOTDEFINED) {
            throw new Exception("Unrecognised Xpath check type " + type);
        }
        XPath x = XPathFactory.newInstance().newXPath();
        x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
        checkExpression = x.compile(xpath);
        if ((comparisonType == XPATHMATCHES) || (comparisonType == XPATHNOTMATCHES)) {
            regexPattern = Pattern.compile(value);
        }
        if (comparisonType == XPATHIN) {
            if (value != null) {
                inList = (new ConfigurationTokenSplitter(value)).split();
            }
        }
    }

    @Override
    public ValidatorOutput validate(String o, boolean stripHeader)
        throws Exception
    {
        ValidationReport ve = null;
        Matcher m = null;
        StringBuilder sb = new StringBuilder("Xpath ");
        sb.append(xpath);
        InputSource is = new InputSource(new StringReader(o));
        String r = checkExpression.evaluate(is);
        switch(comparisonType) {
            case XPATHIN:
                ve = null;
                if ((inList == null) || (inList.length == 0)) {
                    sb.append(" no match list given");
                    ve = new ValidationReport("Pass");
                    ve.setPassed();                
                    break;
                }
                if ((r == null) || (r.trim().length() == 0)) {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match");
                    break;
                }
                for (String s : inList) {
                    if (r.contentEquals(s)) {
                        sb.append(" matches ");
                        sb.append(s);
                        sb.append(" in ");
                        sb.append(value);
                        ve = new ValidationReport("Pass");
                        ve.setPassed();
                        break;
                    }
                }
                if (ve == null) {
                    ve = new ValidationReport("Failed");
                    if (r.length() < 128) {
                        sb.append(r);
                    } else {
                        sb.append(r.substring(0, 128));
                        sb.append("...");
                    }
                    sb.append(" does not match any item in list ");
                    sb.append(value);
                }
                break;
                
            case XPATHEXISTS:
                if ((r != null) && (r.length() > 0)) {
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                    sb.append(" returned \"");
                    if (r.length() < 32) {
                        sb.append(r);
                    } else {
                        sb.append(r.substring(0, 32));
                        sb.append("...");
                    }
                    sb.append("\"");
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned no match");
                }
                break;
            case XPATHNOTEXISTS:
                if ((r == null) || (r.length() == 0)) {
                    sb.append(" returned no match");
                    ve = new ValidationReport("Pass");
                    ve.setPassed();
                } else {
                    ve = new ValidationReport("Failed");
                    sb.append(" returned \"");
                    if (r.length() < 32) {
                        sb.append(r);
                    } else {
                        sb.append(r.substring(0, 32));
                        sb.append("...");
                    }
                    sb.append("\" when it was expected to return no match");
                }
                break;
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
                        if (r.length() < value.length()) {
                            sb.append(r);
                        } else {
                            sb.append(r.substring(0, value.length()));
                            sb.append("...");
                        }
                        sb.append("\" when \"");
                        sb.append(value);
                        sb.append("\" was expected");
                    }
                }
                break;
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
        return new ValidatorOutput(r, vreport);
    }
}
