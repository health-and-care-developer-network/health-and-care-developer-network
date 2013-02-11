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
import org.warlock.util.CfHNamespaceContext;
import org.warlock.util.XMLNamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;
import org.warlock.xsltransform.TransformManager;
import java.io.CharArrayReader;

/**
 * Encapsulates a rules test expression.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
class Expression {

    private static final int NONE = 0;
    private static final int XPATHEQUALS = 1;
    private static final int XPATHNOTEQUALS = 2;
    private static final int XSLT = 3;
    private static final int CONTAINS = 4;
    private static final int NOTCONTAINS = 5;
    private static final int ALWAYS = 6;
    private static final int NEVER = 7;

    private String name = null;
    private String expression = null;
    private String matchValue = null;
    private int type = NONE;
    private XPathExpression xpath = null;

    Expression(String n, String typ, String exp, String match)
            throws Exception
    {
        name = n;
        matchValue = match;
        expression = exp;
        if (typ.contentEquals("XpathEquals")) {
            type = XPATHEQUALS;
            initialiseXpath();
            return;
        }
        if (typ.contentEquals("XpathNotEquals")) {
            type = XPATHNOTEQUALS;
            initialiseXpath();
            return;
        }
        if (typ.contentEquals("Xslt")) {
            type = XSLT;
            initialiseXslt();
            return;
        }
        if (typ.contentEquals("Contains")) {
            type = CONTAINS;
            return;
        }
        if (typ.contentEquals("NotContains")) {
            type = NOTCONTAINS;
            return;
        }
        if (typ.contentEquals("Always")) {
            type = ALWAYS;
            return;
        }
        if (typ.contentEquals("Never")) {
            type = NEVER;
            return;
        }
        throw new Exception("Syntax error: unrecognised expression type: " + typ);
    }

    boolean evaluate(String o)
            throws Exception
    {
        String x = null;
        switch (type) {
            case ALWAYS:
                return true;

            case NEVER:
                return false;
                
            case XPATHEQUALS:

                x = evaluateXpath(o);
                return x.contentEquals(matchValue);

            case XPATHNOTEQUALS:
                x = evaluateXpath(o);
                return !x.contentEquals(matchValue);

            case XSLT:
                TransformManager t = TransformManager.getInstance();
                x = t.doTransform(name, o);
                return !x.contains(matchValue);

            case CONTAINS:
                return o.contains(expression);

            case NOTCONTAINS:
                return !o.contains(expression);

            default:
                return false;
        }
    }

    private String evaluateXpath(String input)
            throws Exception
    {
       CharArrayReader car = new CharArrayReader(input.toCharArray());
       InputSource is = new InputSource(car);
       try {
           String s = xpath.evaluate(is);
           if (s == null) {
               return "";
           }
           return s;
       }
       catch (Exception e) {
           throw new Exception("Exception evaluating Xpath rule: " + e.getMessage());
       }
    }

    private void initialiseXpath()
            throws Exception
    {
        try {
            XPath x = XPathFactory.newInstance().newXPath();
            XMLNamespaceContext c = CfHNamespaceContext.getXMLNamespaceContext();
            x.setNamespaceContext(c);
            xpath = x.compile(expression);
        }
        catch (XPathExpressionException e) {
            StringBuilder sb = new StringBuilder("XPath expression error: ");
            if (e.getCause() != null) {
                sb.append(e.getCause().getMessage());
            }
            sb.append(" in expression ");
            sb.append(expression);
            throw new Exception("Failed to compile XPath expression: " + expression + " : " + sb.toString());
        }
    }

    private void initialiseXslt()
            throws Exception
    {
        TransformManager t = TransformManager.getInstance();

        // expression is a filename

        t.addTransform(name, expression);
    }
}
