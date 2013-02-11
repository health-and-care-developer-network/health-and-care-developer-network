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
import java.util.ArrayList;
import java.util.Properties;
import org.warlock.tk.internalservices.ValidatorService;
import org.warlock.tk.internalservices.SpineValidatorService;
import org.warlock.tk.internalservices.validation.spine.SpineMessage;
/**
 * Validation is a wrapper round the implementations of ValidationCheck that actually
 * do the validations. This allows the loader to make something concrete before it
 * knows what sort of thing it is going to make (because the configurations are loaded
 * generically from XML and it doesn't know what order it will get the data in). The
 * Validation class itself implements ValidationCheck for the convenience of the loader.
 *
 * Initially-supported validations are schema and xslt. You can extend
 * this by providing your own implementation of ValidationCheck, and putting the mapping
 * between the type name and the implementation class in the system properties.
 *   
 * @author Damian Murphy <murff@warlock.org>
 */
public class Validation
{
    private static final String PROPERTYROOT = "tks.validator.check.";

    private ArrayList<Validation> trueChain = null;
    private ArrayList<Validation> falseChain = null;

    private static final int NULL = 0;
    private static final int SINGLE = 1;
    private static final int TRUE = 2;
    private static final int FALSE = 3;
    private int chain = NULL;

    private static final int UNDEFINED = 0;
    private static final int CHECK = 1;
    private static final int IF = 2;
    private static final int THEN = 3;
    private static final int ELSE = 4;

    private Validation parent = null;

    private ValidationCheck check = null;

    private int sequenceType = UNDEFINED;
    private String type = null;
    private String resource = null;
    private String data = null;
    private String serviceName = null;

    private String annotation = null;

    private ValidationCheck validation = null;

    public Validation(String t) {
        if (t.contentEquals("CHECK")) {
            sequenceType = CHECK;
            chain = SINGLE;
        }
        if (t.contentEquals("IF")) {
            sequenceType = IF;
            chain = SINGLE;
        }
        if (t.contentEquals("THEN")) {
            sequenceType = THEN;
            chain = NULL;
        }
        if (t.contentEquals("ELSE")) {
            sequenceType = ELSE;
            chain = NULL;
        }
        if (t.contentEquals("END IF")) {
            sequenceType = UNDEFINED;
            chain = NULL;
        }
    }

    public void setAnnotation(String a) {
        if (annotation == null) {
            annotation = a;
        }
    }

    int getSequenceType() { return sequenceType; }

    void appendValidation(Validation v) 
        throws Exception
    {
        switch(v.getSequenceType()) {
            case CHECK:
            case IF:
                v.setParent(this);
                if (chain < TRUE) {
                    throw new Exception("Syntax error in config");
                }
                if (chain == TRUE) {
                    trueChain.add(v);
                } else {
                    falseChain.add(v);
                }
                break;

            case THEN:
                chain = TRUE;
                trueChain = new ArrayList<Validation>();
                break;

            case ELSE:
                chain = FALSE;
                falseChain = new ArrayList<Validation>();
                break;

            case UNDEFINED:
                break;
        }
    }

    void setParent(Validation v) {
        parent = v;
    }

    void setType(String t) {
        if (sequenceType != IF) {
            type = t;
            return;
        }
        if (t.contentEquals("THEN")) {
            chain = TRUE;
            trueChain = new ArrayList<Validation>();
            return;
        }
        if (t.contentEquals("ELSE")) {
            chain = FALSE;
            falseChain = new ArrayList<Validation>();
            return;
        }
        if (t.contentEquals("ENDIF")) {
            chain = NULL;
            return;
        }
        type = t;
    }

    String getType() { return type; }
    String getResource() { return resource; }
    void setResource(String r) { resource = r; }

    void setData(String d) { data = d; }
    void setService(String s) { serviceName = s; }

    public String getServiceName() { return serviceName; }

    Validation initialise() {
        if (chain == NULL) {
            return parent;
        }
        return this;
    }

    Validation initialise(Properties p)
            throws Exception
    {
        if (type == null) {
            return parent;
//            throw new Exception("Validation().initialise() called and type not set");
        }
        if (sequenceType == CHECK) {
            initCheck(p);
            return parent;
        }
        if (sequenceType == UNDEFINED) {
            return null;
        }
        if (sequenceType == IF) {
            initCheck(p);
        }
        return this;
    }

    void initCheck(Properties p)
            throws Exception
    {
       StringBuilder sb = new StringBuilder(PROPERTYROOT);
       sb.append(type);
       String cname = p.getProperty(sb.toString());
       if (cname == null) {
           throw new Exception("Class definition property " + sb.toString() + " not found");
       }
       try {
           validation = (ValidationCheck)Class.forName(cname).newInstance();
       }
       catch (Exception e) {
           throw new Exception("Exception instantiating ValidatorCheck " + cname + " : " + e.getClass().getName() + " : " + e.getMessage());
       }
       validation.setType(type);
       validation.setResource(resource);
       validation.setData(data);
       validation.initialise();
    }

    public ValidationReport[] validate(SpineMessage o, SpineValidatorService vs)
        throws Exception
    {
        if (sequenceType == UNDEFINED) {
            return null;
        }
        // For an IF, we need to execute the decision check, then identify which
        // chain we're going down. Then iterate (and recurse as needed) down the
        // appropriate chain, and assemble all the reports.
        //
        if (sequenceType == IF) {
            ValidationReport[] vrep = null;
            vrep = validation.validate(o);
            if (vrep == null) {
                return null;
            }
            boolean allpassed = true;
            for (ValidationReport r : vrep) {
                if (!r.getPassed()) {
                    allpassed = false;
                    break;
                }
            }
            vrep = null;
            ArrayList<Validation>branch = null;
            ArrayList<ValidationReport>output = new ArrayList<ValidationReport>();
            if (allpassed) {
                branch = trueChain;
            } else {
                branch = falseChain;
            }
            if (branch != null) { // Empty branch
                for (Validation v : branch) {
                    ValidationReport[] vbranch = v.validate(o, vs);
                    for (ValidationReport vx : vbranch) {
                        output.add(vx);
                    }
                }
            } else {
                return new ValidationReport[0];
            }
            vrep = output.toArray(new ValidationReport[output.size()]);
            return vrep;
        }
        //
        // Normal check, just execute it and return the result. Put any annotation
        // into the first report element.
        //
        ValidationReport[] voutput = validation.validate(o);
        String sd = validation.getSupportingData();
        if (sd != null) {
            vs.writeSupportingData(sd);
        }
        if (annotation != null) {
            if ((voutput != null) && (voutput[0] != null)) {
                voutput[0].setAnnotation(annotation);
            }
        }
        return voutput;
    }
    public ValidationReport[] validate(String o, boolean stripHeader, ValidatorService vs)
        throws Exception
    {
        if (sequenceType == UNDEFINED) {
            return null;
        }
        // For an IF, we need to execute the decision check, then identify which
        // chain we're going down. Then iterate (and recurse as needed) down the
        // appropriate chain, and assemble all the reports.
        //
        if (sequenceType == IF) {
            ValidationReport[] vrep = null;
            ValidatorOutput vout = validation.validate(o, stripHeader);
            vrep = vout.getReport();
            if (vrep == null) {
                return null;
            }
            boolean allpassed = true;
            for (ValidationReport r : vrep) {
                if (!r.getPassed()) {
                    allpassed = false;
                    break;
                }
            }
            vrep = null;
            ArrayList<Validation>branch = null;
            ArrayList<ValidationReport>output = new ArrayList<ValidationReport>();
            if (allpassed) {
                branch = trueChain;
            } else {
                branch = falseChain;
            }
            if (branch != null) { // Empty branch
                for (Validation v : branch) {
                    ValidationReport[] vbranch = v.validate(o, stripHeader, vs);
                    for (ValidationReport vx : vbranch) {
                        output.add(vx);
                    }
                }
            } else {
                return new ValidationReport[0];
            }
            vrep = output.toArray(new ValidationReport[output.size()]);
            return vrep;
        }
        //
        // Normal check, just execute it and return the result. Put any annotation
        // into the first report element.
        //
        ValidatorOutput vout = validation.validate(o, stripHeader);
        ValidationReport[] voutput = vout.getReport();
        if (vs != null) {
            String sd = validation.getSupportingData();
            if (sd != null) {
                vs.writeSupportingData(sd);
            }
        }
        if (annotation != null) {
            if ((voutput != null) && (voutput[0] != null)) {
                voutput[0].setAnnotation(annotation);
            }
        }
        return voutput;
    }
}
