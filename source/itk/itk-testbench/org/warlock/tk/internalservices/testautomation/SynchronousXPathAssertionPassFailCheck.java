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
package org.warlock.tk.internalservices.testautomation;
import java.io.InputStream;
import java.io.StringReader;
import org.xml.sax.InputSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.warlock.util.ConfigurationTokenSplitter;
import org.warlock.util.CfHNamespaceContext;
/**
 *
 * @author Damian Murphy <murff@warlock.org>
 */
public class SynchronousXPathAssertionPassFailCheck 
    extends AbstractSynchronousPassFailCheck
{
    protected String givenXpath = null;
    protected String givenType = null;
    
    protected String assertionValue = null;
    protected XPathExpression expression = null;
    protected int assertionType = UNDEFINED;
    protected Pattern assertionPattern = null;
    protected String[] inList = null;
    
    protected static final String[] TYPES = {"exists", "doesnotexist", "matches", "doesnotmatch", "in"};
    protected static final int UNDEFINED = -1;
    protected static final int EXISTS = 0;
    protected static final int NOTEXISTS = 1;
    protected static final int MATCHES = 2;
    protected static final int NOTMATCHES = 3;
    protected static final int ISIN = 4;
    
    @Override
    public void init(String[] line)
            throws Exception
    {
        super.init(line);
        
        // line elements 0 and 1 have already been processed by super.init(), as
        // has any extractor, the rest of the elements (if present) ae processed
        // here.
        //
        // Parameters are:
        // 2 XPath expression
        // 3 type (equals, not equals etc)
        // 4 (optional assertion value, if not "EXTRACTOR")
        
        try {
            givenXpath = line[2];
            XPath x = XPathFactory.newInstance().newXPath();
            x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
            expression = x.compile(givenXpath);
        }
        catch (Exception e) {
            throw new Exception("Xpath pass/fail check syntax error, XPath not found: " + e.toString());
        }
        try {
            givenType = line[3];
            for (int i = 0; i < TYPES.length; i++) {
                if (givenType.contentEquals(TYPES[i])) {
                    assertionType = i;
                }
            }
            if (assertionType == UNDEFINED) {
                throw new Exception("Unrecognised assertion type: " + givenType);
            }
        }
        catch (Exception e) {
            throw new Exception("Xpath pass/fail check syntax error, type not found: " + e.toString());
        }
        try {
            assertionValue = line[4];
            if (assertionValue.contentEquals("EXTRACTOR")) {
                assertionValue = null;
            }
        }
        catch(Exception e) {}
        switch(assertionType) {
            // Syntactic checks that we have everything, and make
            // the Pattern if we're doing a regex. Also make the "in"
            // array where necessary.
            case EXISTS:
            case NOTEXISTS:
                if (assertionValue != null) {
                    throw new Exception("Syntax error: exists/notexists check does not assert a value");
                }
                break;
            case MATCHES:
            case NOTMATCHES:
                if (assertionValue == null) {
                    throw new Exception("Syntax error: matches/notmatches check must assert a value");
                }
                assertionPattern = Pattern.compile(assertionValue);
                break;
                
            case ISIN:
                if (assertionValue == null) {
                    throw new Exception("Syntax error: in check must assert a value");
                }
                inList = (new ConfigurationTokenSplitter(assertionValue)).split();
                break;                                
        }
    }
    
    protected boolean doChecks(Script s, InputSource is)
            throws Exception
    {
        String r = expression.evaluate(is);
        boolean result = false;
        StringBuilder sb = new StringBuilder();
        Matcher m = null;
        switch(assertionType) {
            case EXISTS:
                if ((r != null) && (r.length() > 0)) {
                    result = true;
                    sb.append(givenXpath);
                    sb.append(" exists as expected.");
                } else {
                    result = false;
                    sb.append(givenXpath);
                    sb.append(" expected to exist, but not found");                    
                }
                break;

            case NOTEXISTS:
                if ((r != null) && (r.length() > 0)) {
                    result = false;
                    sb.append(givenXpath);
                    sb.append(" exists when it is expected to be absent");                    
                } else {
                    result = true;
                    sb.append(givenXpath);
                    sb.append(" absent as expected.");
                }
                break;

            case MATCHES:
                m = assertionPattern.matcher(r);
                if (m.find()) {
                    result = true;
                    sb.append(givenXpath);
                    sb.append(" matches ");
                    sb.append(assertionValue);
                    sb.append(" as expected.");                    
                } else {
                    result = false;
                    sb.append(givenXpath);
                    sb.append(" does not match ");
                    sb.append(assertionValue);
                    sb.append(" when it was expected to do so.");                                        
                }
                break;

            case NOTMATCHES:
                m = assertionPattern.matcher(r);
                if (m.find()) {
                    result = false;
                    sb.append(givenXpath);
                    sb.append(" matches ");
                    sb.append(assertionValue);
                    sb.append(" when not expected to.");                    
                } else {
                    result = true;
                    sb.append(givenXpath);
                    sb.append(" does not match ");
                    sb.append(assertionValue);
                    sb.append(" as expected.");                                                            
                }
                break;
                
            case ISIN:
                if ((r == null) || (r.length() == 0)) {
                    result = false;
                    sb.append(givenXpath);
                    sb.append(" returned no data.");                    
                    
                } else {
                    boolean foundIn = false;
                    for (String l : inList) {
                        if (r.contentEquals(l)) {
                            result = true;
                            foundIn = true;
                            sb.append(givenXpath);
                            sb.append(" value ");
                            sb.append(r);
                            sb.append(" found as expected");
                            break;
                        }
                    }
                    if (!foundIn) {
                        result = false;
                        sb.append(givenXpath);
                        sb.append(" value ");
                        sb.append(r);
                        sb.append(" not in allowed set");                        
                    }
                }
                break;                                
        }
        description = sb.toString();
        return result;        
    }
    
    @Override
    public boolean passed(Script s, InputStream in)
            throws Exception
    {
        String responseBody = getResponseBody(in);
        InputSource is = new InputSource(new StringReader(responseBody));
        boolean p = doChecks(s, is);
        doExtract(responseBody);
        return p;
    }
}
