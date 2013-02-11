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
import org.warlock.util.ConfigurationStringTokeniser;
import java.util.HashMap;
import java.util.Properties;
import java.io.FileReader;
import java.io.BufferedReader;
 /**
 * Class to implement the parser for validation configuration files, and to
 * provide the parsed validation sets to the TKW validate mode, keyed on
 * service.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class ValidatorFactory {

    private static final String VALIDATORCONFIG = "tks.validator.config";

    private static ValidatorFactory factory = new ValidatorFactory();
    private static Exception bootException = null;
    private HashMap<String, ValidationSet> validationSets = null;
    private HashMap<String, ValidationSet> subroutines = null;
    private Properties bootProperties = null;

    public static ValidatorFactory getInstance()
            throws Exception
    {
        if (bootException != null) {
            throw new Exception("Exception was thrown initialising ValidatorFactory", bootException);
        }
        return factory;
    }

    public ValidationSet getSubroutine(String r) {
        return subroutines.get(r);
    }

    public void clear() {
        validationSets = null;
        subroutines = null;
        bootProperties = null;
        bootException = null;
    }
    
    public void init(Properties p)
            throws Exception
    {
        bootProperties = p;
        String conf = p.getProperty(VALIDATORCONFIG);
        if (conf == null) {
            throw new Exception("Validator configuration file property " + VALIDATORCONFIG  + " not set");
        }

        FileReader fr = new FileReader(conf);
        BufferedReader br = new BufferedReader(fr);

        validationSets = new HashMap<String,ValidationSet>();
        subroutines = new HashMap<String,ValidationSet>();
        String line = null;
        boolean readingValidation = false;
        ValidationSet currentValidationSet = null;
        Validation currentValidation = null;
        Validation lastValidation = null;
        int lineNumber = 0;
        while ((line = br.readLine()) != null) {
            lineNumber++;
            line = line.trim();
            if (line.startsWith("#")){
                continue;
            }
            if (line.length() == 0){
                continue;
            }
            ConfigurationStringTokeniser st = new ConfigurationStringTokeniser(line);
            String elem = st.nextToken(); 
            if (elem.contentEquals("VALIDATE")) {
                if (currentValidation != null) {
                    throw new Exception("Syntax error in validation configuration: VALIDATE after unterminated condition");
                }
                currentValidationSet = new ValidationSet();
                if (!st.hasMoreTokens()) {
                    throw new Exception("Syntax error in validation configuration " + conf + " : bare VALIDATE");
                }
                elem = st.nextToken();
                currentValidationSet.setServiceName(elem);
                validationSets.put(elem, currentValidationSet);
                continue;
            }
            if (elem.contentEquals("SUBSET")) {
                if (currentValidation != null) {
                    throw new Exception("Syntax error in validation configuration: SUBSET after unterminated condition");
                }
                currentValidationSet = new ValidationSet();
                if (!st.hasMoreTokens()) {
                    throw new Exception("Syntax error in validation configuration " + conf + " : bare SUBSET with no name");
                }
                elem = st.nextToken();
                currentValidationSet.setServiceName(elem);
                subroutines.put(elem, currentValidationSet);
                continue;
            }
            String annotation = null;
            if (elem.contentEquals("ANNOTATION")) {
                if (lastValidation != null) {
                    annotation = line.substring("ANNOTATION".length()).trim();
                    lastValidation.setAnnotation(annotation);
                    annotation = null;
                }
                continue;
            }
            Validation v = null;
            if (st.hasMoreTokens()) {
                // We're reading a real validation
                v = new Validation(elem);
                lastValidation = v;
                v.setService(currentValidationSet.getServiceName());
                if (currentValidation != null) {
                    currentValidation.appendValidation(v);
                } else {
                    currentValidationSet.addValidation(v);
                }
                elem = st.nextToken();
                v.setType(elem);
                if (st.hasMoreTokens()) {
                    elem = st.nextToken();
                    v.setResource(elem);
                    if (st.hasMoreTokens()) {
                        StringBuilder sb = new StringBuilder();
                        while(st.hasMoreTokens()) {
                            sb.append(st.nextToken());
                            if (st.hasMoreTokens()) {
                                sb.append(" ");
                            }
                        }
                        v.setData(sb.toString());
                    }
                }
//                System.err.println(line);
                currentValidation = v.initialise(bootProperties);
            } else {
               // We're doing a "THEN" or and "ELSE" or an "ENDIF" from an IF
                if (currentValidation == null) {
                    System.err.println("Configuration error - expected to be in a condition but no current validation found: Line " + lineNumber + " : " + line);
                } else {
                    currentValidation.setType(elem);
                    currentValidation = currentValidation.initialise();
                }
            }
            
        }
    }

    private ValidatorFactory() {}

    public ValidationSet getValidationSet(String s) 
        throws Exception
    {
        if (validationSets == null) {
            throw new Exception("ValidatorFactory not initialised");
        }
        return validationSets.get(s);
    }
}
