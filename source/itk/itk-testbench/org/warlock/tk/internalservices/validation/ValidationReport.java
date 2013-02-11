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

 /**
 * Container class for reports from validation checks.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class ValidationReport
    extends Exception
{
    private String serviceName = null;
    private String detail = null;
    private String test = null;
    private String fileName = null;
    private String annotation = null;
    private int iteration = -1;
    private String context = null;
    private boolean passed = false;

    public ValidationReport(String s) {
        detail = s;
    }
    public void setPassed() { passed = true; }
    public boolean getPassed() { return passed; }
    public void setServiceName(String s) { serviceName = s; }
    public void setTest(String t) { test = t; }
    public void setFilename(String f) { fileName = f; }
    public void setAnnotation(String a) { annotation = a; }
    public void setIteration(int i) { iteration = i; }
    public void setContext(String c) { context = c; }

    public int getIteration() { return iteration; }
    public String getContext() { return context; }
    public String getServiceName() { return serviceName; }
    public String getDetail() { return detail; }
    public String getTestDetails() { return test; }
    public String getFilename() { return fileName; }
    public String getAnnotation() { return annotation; }
    public boolean hasAnnotation() { return (annotation != null); }
}
