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
package org.warlock.tk.internalservices.rules;
import java.io.CharArrayReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.safehaus.uuid.UUIDGenerator;
import org.warlock.util.CfHNamespaceContext;
import org.warlock.util.XMLNamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;
/**
 * Defines a substitution that may be performed on a response template,
 * to make a message instance. Provides a set of pre-defined substitution
 * types, and supports substitution from a class implementing the
 * SubstitutionValue interface.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
class Substitution {

    private static final int UUID = 1;
    private static final int HL7DATE = 2;
    private static final int ISO8601DATE = 3;
    private static final int XPATH = 4;
    private static final int LITERAL = 5;
    private static final int PROPERTY = 6;
    private static final int CLASS = 7;

    private String tag = null;
    private String literalContent = null;
    private int type = 0;
    private String[] data = null;
    private int tagLength = 0;
    private SubstitutionValue subsClass = null;
    private XPathExpression xpath = null;

    private static SimpleDateFormat HL7FORMATDATE = new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat ISO8601FORMATDATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    Substitution(String t, String typ, String[] d)
        throws Exception
    {
        tag = t;
        data = d;
        tagLength = tag.length();
        if (typ.contentEquals("UUID")) {
            type = UUID;
            return;
        }
        if (typ.contentEquals("HL7datetime")) {
            type = HL7DATE;
            return;
        }
        if (typ.contentEquals("ISO8601datetime")) {
            type = ISO8601DATE;
            return;
        }
        if (typ.contentEquals("Xpath")) {
            type = XPATH;
            if ((data == null) || (data.length == 0)) {
                throw new Exception("Syntax error in substitution: no Xpath expression found for tag " + tag);
            }
            initialiseXpath(data[0]);
            return;
        }
        if (typ.contentEquals("XPath")) {
            type = XPATH;
            if ((data == null) || (data.length == 0)) {
                throw new Exception("Syntax error in substitution: no Xpath expression found for tag " + tag);
            }
            initialiseXpath(data[0]);
            return;
        }
        if (typ.contentEquals("Literal")) {
            type = LITERAL;
            if ((data == null) || (data.length == 0)) {
                throw new Exception("Syntax error in substitution: no value found for literal tag " + tag);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < (data.length - 1); i++) {
                sb.append(data[i]);
                sb.append(" ");
            }
            sb.append(data[data.length - 1]);
            literalContent = sb.toString();
            return;
        }
        if (typ.contentEquals("Property")) {
            type = PROPERTY;
            if ((data == null) || (data.length == 0)) {
                throw new Exception("Syntax error in substitution: no property name found for tag " + tag);
            }
            return;
        }
        if (typ.contentEquals("Class")) {
            type = CLASS;
            if ((data == null) || (data.length == 0)) {
                throw new Exception("Syntax error in substitution: no class name found for tag " + tag);
            }
            if (data.length == 1) {
                initialiseSubstituterClass(data[0], null);
            } else {
                initialiseSubstituterClass(data[0], data[1]);
            }
            return;
        }
        throw new Exception("Unrecognised type : " + typ);
    }

    void substitute(StringBuffer sb, String o)
            throws Exception
    {
        int tagPoint = -1;
        String content = getContent(o);
        while ((tagPoint = sb.indexOf(tag)) != -1 ) {
            sb.replace(tagPoint, tagPoint + tagLength, content);
        }
    }

    private String getContent(String inputRequest)
        throws Exception
    {

        String content = null;
        switch(type) {
            case UUID:
                content = UUIDGenerator.getInstance().generateTimeBasedUUID().toString();
                content = content.toUpperCase();
                break;

            case HL7DATE:
                content = HL7FORMATDATE.format(new Date());
                break;

            case ISO8601DATE:
                content = ISO8601FORMATDATE.format(new Date());
                break;

            case XPATH:
                if (xpath == null) {
                    content = "CONFIGURATION ERROR: Xpath expression not set for tag " + tag;
                } else {
                    content = resolveXpath(inputRequest);
                }
                break;

            case LITERAL:
                content = (data[0] == null) ? "NOT SET" : literalContent;
                break;

            case PROPERTY:
                String p = data[0];
                if ((p == null) || (System.getProperties().getProperty(p) == null)) {
                    content = "CONFIGURATION ERROR: Property " + p + " not set";
                } else {
                    content = System.getProperties().getProperty(p);
                }
                break;

            case CLASS:
                if (subsClass == null) {
                    content = "CONFIGURATION ERROR: Substitution class not set";
                } else {
                    content = subsClass.getValue(inputRequest);
                }
                break;

            default:
                content = "";
                break;
        }
        return content;
    }

    private String resolveXpath(String input)
            throws Exception
    {
       CharArrayReader car = new CharArrayReader(input.toCharArray());
       InputSource is = new InputSource(car);
       String s = xpath.evaluate(is);
       if (s == null) {
           return "";
       }
       return s;
    }

    private void initialiseXpath(String s)
            throws Exception
    {
        try {
            XPath x = XPathFactory.newInstance().newXPath();
            XMLNamespaceContext c = CfHNamespaceContext.getXMLNamespaceContext();
            x.setNamespaceContext(c);
            xpath = x.compile(s);
        }
        catch (XPathExpressionException e) {
            StringBuilder sb = new StringBuilder("XPath expression error: ");
            if (e.getCause() != null) {
                sb.append(e.getCause().getMessage());
            }
            sb.append(" in expression ");
            sb.append(s);
            throw new Exception("Failed to compile XPath expression: " + s + " - " + sb.toString() + "for tagName " + tag);
        }
    }

    private void initialiseSubstituterClass(String c, String a)
            throws Exception
    {
        subsClass = (SubstitutionValue)Class.forName(c).newInstance();
        if (a != null) {
            subsClass.setData(a);
        }
    }
}
