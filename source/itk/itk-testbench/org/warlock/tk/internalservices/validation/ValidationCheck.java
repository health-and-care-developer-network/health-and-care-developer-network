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
import org.warlock.tk.internalservices.validation.spine.SpineMessage;
/**
 * Interface implemented by classes that perform the various validation types.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public interface ValidationCheck {
    public void initialise() throws Exception;
    public void setType(String t);
    public void setResource(String r);
    public void setData(String d) throws Exception;
    public ValidatorOutput validate(String o, boolean stripHeader) throws Exception;
    public ValidationReport[] validate(SpineMessage o) throws Exception;
    public String getSupportingData();
}
