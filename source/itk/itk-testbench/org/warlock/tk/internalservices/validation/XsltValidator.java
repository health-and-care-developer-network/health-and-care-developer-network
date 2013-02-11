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
import org.warlock.xsltransform.TransformManager;
import org.warlock.tk.internalservices.validation.spine.SpineMessage;
 /**
 * Apply an XSL transform to the file for validation. This uses a case-insensitive
 * "contains" test on the output of the transform (as a string) to detect failure.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class XsltValidator
    implements ValidationCheck
{
    private String transformFile = null;
    private String checkterm = null;
    
    public XsltValidator() {}

    @Override
    public ValidationReport[] validate(SpineMessage o)
            throws Exception
    {
        throw new Exception("ITK validation. Use SpineValidator service");
    }

    @Override
    public void setResource(String t) { transformFile = t; }

    @Override
    public void setData(String d) { checkterm = d; }

    @Override
    public String getSupportingData() { return null; }
    
    
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
    public void setType(String t) {}

    @Override
    public ValidatorOutput validate(String o, boolean stripHeader) {
        ValidationReport report[] = null;
        String x = null;
        try {
            StringBuilder sb = new StringBuilder(transformFile);
            TransformManager t = TransformManager.getInstance();
            x = t.doTransform(transformFile, o);
            String c = checkterm.toUpperCase();
            if (x.toUpperCase().contains(c)) {
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
        return new ValidatorOutput(x, report);
    }
}
