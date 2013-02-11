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
import org.warlock.tk.internalservices.ValidatorService;
import org.warlock.tk.internalservices.SpineValidatorService;
import org.warlock.tk.internalservices.validation.spine.SpineMessage;
/**
 * The TKW validate mode keys validation scripts on service name. Each script is
 * a collection of validation checks supporting conditional validation, "subroutine"
 * calls and so on. A validation script for a service is a ValidationSet, this object
 * is made by the validation configuration file parser and stored in a
 * ValidatorFactory.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class ValidationSet {
    private ArrayList<Validation> validations = new ArrayList<Validation>();
    private String serviceName = null;

    ValidationSet() {}

    void setServiceName(String n) {
        serviceName = n;
    }

    public String getServiceName() { return serviceName; }

    public void addValidation(Validation v) { validations.add(v); }

    public ArrayList<ValidationReport> doSpineValidations(SpineMessage sm, SpineValidatorService vs) {
        ArrayList<ValidationReport> ve = new ArrayList<ValidationReport>();
        if (validations == null) {
            return ve;
        }
        for (Validation v : validations) {
            ValidationReport[] e = null;
            try {
                e = v.validate(sm, vs);
            }
            catch (Exception ex) {
                ValidationReport vr = new ValidationReport("Error running check");
                vr.setTest("Validation type " + v.getType() + " using " + v.getResource() + " : " + ex.getMessage());
                e = new ValidationReport[1];
                e[0] = vr;
            }
            for (ValidationReport ex : e) {
                ex.setServiceName(serviceName);
                ve.add(ex);
            }
        }
        return ve;
    }
    
    public ArrayList<ValidationReport> doValidations(String o, boolean stripHeader, ValidatorService vs)
    {
        ArrayList<ValidationReport> ve = new ArrayList<ValidationReport>();
        if (validations == null) {
            return ve;
        }
        for (Validation v : validations) {
            ValidationReport[] e = null;
            try {
                e = v.validate(o, stripHeader, vs);
            }
            catch (Exception ex) {
                ValidationReport vr = new ValidationReport("Error running check");
                vr.setTest("Validation type " + v.getType() + " using " + v.getResource() + " : " + ex.getMessage());
                e = new ValidationReport[1];
                e[0] = vr;
            }
            for (ValidationReport ex : e) {
                if (ex != null) {
                    ex.setServiceName(serviceName);
                    ve.add(ex);
                }
            }
        }
        return ve;
    }
}
