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
import org.warlock.util.ConfigurationStringTokeniser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.Node;
import org.warlock.tk.boot.ServiceResponse;

/**
 * The TKW simulator rules engine is "pluggable" and can be provided by any implementation
 * of the "Engine" interface. The DefaultRulesEngine is the default implementation,
 * the one whose configuration files are provided for use in the ITK Accreditation process,
 * and described in the configuration manual.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class DefaultRulesEngine
    implements Engine
{
    private ArrayList<Substitution> substitutions = new ArrayList<Substitution>();
    private HashMap<String,Response> responses = new HashMap<String,Response>();
    private HashMap<String,Expression> expressions = new HashMap<String,Expression>();
    private HashMap<String,RuleSet> rules = new HashMap<String,RuleSet>();

    private static final int NOTHING = -1;
    private static final int RESPONSES = 0;
    private static final int SUBSTITUTIONS = 1;
    private static final int EXPRESSIONS = 2;
    private static final int RULE = 3;

    public DefaultRulesEngine() {}

    private void addResponse(String line)
        throws Exception
    {
        ConfigurationStringTokeniser st = new ConfigurationStringTokeniser(line);
        if ((st.countTokens() < 2) || (st.countTokens() > 5)) {
            throw new Exception("Syntax error defining response: " + line);
        }
        String name = st.nextToken();
        String url = st.nextToken();
        int rcode = 200;
        if (st.hasMoreTokens()) {
            try {
                rcode = Integer.parseInt(st.nextToken());
            }
            catch (Exception e) {
                throw new Exception("Syntax error defining response code (should be an integer): " + line);
            }
        }
        String action = null;
        if (st.hasMoreTokens()) {
            action = st.nextToken();
        }
        Response r = new Response(name, url);
        r.setCode(rcode);
        r.setAction(action);
        responses.put(name, r);
    }

    private void addSubstitution(String line)
        throws Exception
    {
        ConfigurationStringTokeniser st = new ConfigurationStringTokeniser(line);
        int dataCount = st.countTokens() - 2;
        String[] data = null;
        if (dataCount > 0) {
            data = new String[dataCount];
        }
        String name = st.nextToken();
        String type = st.nextToken();
        for (int i = 0; i < dataCount; i++) {
            data[i] = st.nextToken();
        }
        Substitution s = new Substitution(name, type, data);
        substitutions.add(s);
    }

    private void addExpression(String line)
        throws Exception
    {
        ConfigurationStringTokeniser st = new ConfigurationStringTokeniser(line);
        if ((st.countTokens() < 2) || (st.countTokens() > 4)) {
            throw new Exception("Syntax error defining expression: " + line);
        }
        String name = st.nextToken();
        String type = st.nextToken();
        String exp = null;
        // Need to do this because "always" only has 1 parameter
        if (st.hasMoreTokens()) {
            exp = st.nextToken();
        }
        // Need to do this because "contains" and "notcontains" only have 3 parameters
        String matchvalue = null;
        if (st.hasMoreTokens()) {
            matchvalue = st.nextToken();
        }
        Expression e = new Expression(name, type, exp, matchvalue);
        expressions.put(name, e);
    }

    private RuleSet addRule(String line)
        throws Exception
    {
        if (rules.containsKey(line)) {
            return rules.get(line);
        }
        RuleSet r = new RuleSet(line);
        rules.put(line, r);
        return r;
    }

    private void addRulePart(String line, RuleSet r)
            throws Exception
    {
        Response t = null;
        Response f = null;
        Expression e = null;
        ConfigurationStringTokeniser st = new ConfigurationStringTokeniser(line);
        int tcount = st.countTokens();
        if ((tcount != 6) && (tcount != 4)) {
            throw new Exception("Syntax error in rule: " + line);
        }
        st.nextToken();
        String ename = st.nextToken();
        st.nextToken();
        String truername = st.nextToken();
        if (!truername.equalsIgnoreCase("next")) {
            t = responses.get(truername);
            if (t == null) {
                throw new Exception("Rule \"true\" response " + truername + " declared but not defined in " + line);
            }
        }
        String falsername = null;
        if (tcount == 6) {
            st.nextToken();
            falsername = st.nextToken();
            if (!falsername.equalsIgnoreCase("next")) {
                f = responses.get(falsername);
                if (f == null) {
                    throw new Exception("Rule \"falsee\" response " + falsername + " declared but not defined in " + line);
                }
            }
        }
        e = expressions.get(ename);
        if (e == null) {
            throw new Exception("Rule expression " + ename + " declared but not defined in line " + line);
        }
        Rule rule = new Rule(e, t, f);
        r.addRule(rule);
    }

    /**
     * Loads the simulator rules configuration at the given file name.
     * 
     * @param filename
     * @throws Exception 
     */
    @Override
    public void load(String filename)
            throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = null;
        int readingWhat = NOTHING;
        int lineNumber = 0;
        RuleSet current = null;
        while ((line = br.readLine()) != null) {
            ++lineNumber;
            if (line.startsWith("#")) {
                continue;
            }
            switch (readingWhat) {
                case RESPONSES:
                    if (line.contentEquals("END RESPONSES")) {
                        readingWhat = NOTHING;
                    } else {
                        addResponse(line);
                    }
                    break;

                case SUBSTITUTIONS:
                    if (line.contentEquals("END SUBSTITUTIONS")) {
                        readingWhat = NOTHING;
                    } else {
                        addSubstitution(line);
                    }
                    break;

                case EXPRESSIONS:
                    if (line.contentEquals("END EXPRESSIONS")) {
                        readingWhat = NOTHING;
                    } else {
                        addExpression(line);
                    }
                    break;


                case RULE:
                    if (line.contentEquals("END RULE")) {
                        readingWhat = NOTHING;
                        current = null;
                    } else {
                        if (current == null) {
                            if (line.startsWith("if")) {
                                throw new Exception("Syntax error in rules file at line " + lineNumber + " found \"if\" exprected request type");
                            }
                            current = addRule(line);
                        } else {
                            addRulePart(line, current);
                        }
                    }
                    break;

                case NOTHING:
                default:
                    if (line.contentEquals("BEGIN RESPONSES")) {
                        readingWhat = RESPONSES;
                        continue;
                    }
                    if (line.contentEquals("BEGIN SUBSTITUTIONS")) {
                        readingWhat = SUBSTITUTIONS;
                        continue;
                    }
                    if (line.contentEquals("BEGIN EXPRESSIONS")) {
                        readingWhat = EXPRESSIONS;
                        continue;
                    }
                    if (line.contentEquals("BEGIN RESPONSES")) {
                        readingWhat = RESPONSES;
                        continue;
                    }
                    if (line.contentEquals("BEGIN RULE")) {
                        readingWhat = RULE;
                        continue;
                    }
                    break;

            }
        }
        br.close();
    }

    /**
     * Do the rules lookup for a message of the given SOAPaction.
     * 
     * @param type SOAPaction
     * @param input Message
     * @return ServiceResponse containing an HTTP response code and body.
     * @throws RulesException
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, String input)
            throws RulesException, Exception
    {
        RuleSet r = rules.get(type);
        if (r == null) {
            return new ServiceResponse(-1, "Rules error: no rules found for type " + type);
        }
        Response resp = r.execute(input);
        if (resp == null) {
            return new ServiceResponse(500, "Rules error: null response returned for type " + type);
        }
        return resp.instantiate(substitutions, input);
    }

    /**
     * Do the rules lookup for a request presented as a DOM.
     * 
     * @param type SOAPaction
     * @param input Root element of DOM form of request
     * @return ServiceResponse containing HTTP result code and body.
     * @throws RulesException
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String type, Node input)
            throws RulesException, Exception
    {
        RuleSet r = rules.get(type);
        if (r == null) {
            throw new RulesException("No rules found for type " + type);
        }
        DOMImplementationLS dls = (DOMImplementationLS)input.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer lss = dls.createLSSerializer();
        String s = lss.writeToString(input);
        Response resp = r.execute(s);
        return resp.instantiate(substitutions, s);
    }
}
