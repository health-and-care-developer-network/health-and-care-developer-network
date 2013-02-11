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
package org.warlock.tk.internalservices;
import java.util.ArrayList;
import java.util.Properties;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.boot.ToolkitSimulator;
import org.warlock.tk.internalservices.validation.ValidatorFactory;
import org.warlock.tk.internalservices.validation.ValidationSet;
import org.warlock.tk.internalservices.validation.ValidationReport;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.warlock.tk.internalservices.validation.spine.SpineMessage;
 /**
 * Service to call validations for Spine messages. Used by the TKW "spinevalidator"
 * mode.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 

public class SpineValidatorService
    implements org.warlock.tk.boot.ToolkitService
{
    private static final String SOURCEPROPERTY = "tks.validator.source";
    private static final String REPORTPROPERTY = "tks.validator.reports";

    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;
    private File    sourceDirectory = null;
    private File    reportDirectory = null;

    private static SimpleDateFormat LOGFILEDATE = new SimpleDateFormat("yyyyMMddHHmmss");

    /** Creates a new instance of ValidatorService */
    public SpineValidatorService() {}
    @Override
    public Properties getBootProperties() { return bootProperties; }

    @Override
    public void boot(ToolkitSimulator t, Properties p, String s)
            throws Exception
    {
        bootProperties = p;
        serviceName = s;
        simulator = t;

        String prop = null;
        prop = bootProperties.getProperty(SOURCEPROPERTY);
        if ((prop == null) || (prop.trim().length() == 0)) {
            throw new Exception("Validator source directory property " + SOURCEPROPERTY + " not set");
        }
        sourceDirectory = new File(prop);
        if (!sourceDirectory.exists()) {
            throw new Exception("Validator source directory " + prop + " not found");
        }
        if (!sourceDirectory.canRead()) {
            throw new Exception("Validator source directory " + prop + " not readable");
        }
        prop = bootProperties.getProperty(REPORTPROPERTY);
        if ((prop == null) || (prop.trim().length() == 0)) {
            throw new Exception("Validator report directory property " + REPORTPROPERTY + " not set");
        }
        reportDirectory = new File(prop);
        if (!reportDirectory.exists()) {
            throw new Exception("Validator report directory " + prop + " not found");
        }
        if (!reportDirectory.canWrite()) {
            throw new Exception("Validator report directory " + prop + " not writable");
        }
        ValidatorFactory.getInstance().init(bootProperties);
        System.out.println(serviceName + " started, class: " + this.getClass().getCanonicalName());
    }

    /**
     * Not currently used.
     * 
     * @param s 
     */
    public void writeSupportingData(String s) {
        
    }
    
    
    private void writeSummaryReport(ArrayList<ValidationReport> vr, String dateString, int numfiles)
            throws Exception
    {
       String filename = "validation_report_" + dateString + ".html";

       int passes = 0;
       int fails = 0;

       for (ValidationReport v : vr) {
            if (v.getPassed()) {
                passes++;
            } else {
                fails++;
            }
       }

       StringBuilder sb = new StringBuilder();
       sb.append("<html><head><title>Validation summary ");
       sb.append(dateString);
       sb.append("</title></head><body>");
       sb.append("<h1>Validation summary</h1>");
       sb.append("<h3>Run at: ");
       sb.append(dateString);
       sb.append(" using configuration \"");
       sb.append(simulator.getConfigurationName());
       sb.append("\"</h3>");
       sb.append("<h3>Submitter: ");
       sb.append(simulator.getOrganisationName());
       sb.append("</h3>");
       sb.append("<p/>");
       sb.append("<h2>Summary</h2><table><tr><td>Files validated: </td><td align=\"right\">");
       sb.append(numfiles);
       sb.append("</td></tr><tr><td>Checks reported: </td><td align=\"right\">");
       sb.append(vr.size());
       sb.append("</td></tr><tr><td>Passes: </td><td align=\"right\">");
       sb.append(passes);
       sb.append("</td></tr><tr><td>Check issues: </td><td align=\"right\">");
       sb.append(fails);
       sb.append("</td></tr></table><br/><h2>Results by file</h2>");
       sb.append("<table>");
       sb.append("<tr bgcolor=\"#000000\"><td><font color=\"#FFFFFF\"><b>File</b></font></td><td><font color=\"#FFFFFF\"><b>Result</b></font></td><td><font color=\"#FFFFFF\"><b>Comment</b></font></td></tr>");
       int row = 0;
       String lastfilename = "";
       for (ValidationReport v : vr) {
           if (!v.getFilename().contentEquals(lastfilename)) {
               sb.append("<tr bgcolor=\"#AAAAFF\"><td colspan=\"3\"><b>Validation file: ");
               sb.append(v.getFilename());
               sb.append("</b></td></tr>");
           }
           lastfilename = v.getFilename();
           if ((row % 2) == 0){
               sb.append("<tr bgcolor=\"#FFFFFF\"><td>");
           } else {
               sb.append("<tr bgcolor=\"#EAEAEA\"><td>");
           }
           if (v.getPassed()) {
               sb.append("<font color=\"#008000\">");
               sb.append(v.getFilename());
               sb.append("</font></td><td>");
               sb.append("<font color=\"#008000\">PASS</font></td><td><font color=\"#008000\">");
               if (v.hasAnnotation()) {
                   sb.append(v.getAnnotation());
                   sb.append("<br/>");
               }
               sb.append(v.getDetail());
               if ((v.getTestDetails() != null) && (v.getTestDetails().trim().length() != 0)) {
                   sb.append(" -- ");
                   sb.append(v.getTestDetails());
               }
               sb.append("</font></td></tr>");
           } else {
               sb.append("<font color=\"#900000\">");
               sb.append(v.getFilename());
               sb.append("</font></td><td>");
               sb.append("<font color=\"#900000\">FAIL</font></td><td><font color=\"#900000\">");
               if (v.hasAnnotation()) {
                   sb.append(v.getAnnotation());
                   sb.append("<br/>");
               }
               sb.append(v.getDetail());
               if ((v.getTestDetails() != null) && (v.getTestDetails().trim().length() != 0)) {
                   sb.append(" -- ");
                   sb.append(v.getTestDetails());
               }
               sb.append("</font></td></tr>");
           }
           ++row;

       }
       sb.append("</table>");
       sb.append("<hr/>Prepared by: ");
       sb.append(simulator.getVersion());
       sb.append("</body></html>");
       File f = new File(reportDirectory, filename);
       FileWriter fw = new FileWriter(f);
       fw.write(sb.toString());
       fw.flush();
       fw.close();
    }

    /**
     * No operation.
     * 
     * @param validationType
     * @param o
     * @return Empty ServiceResponse
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String validationType, Object o)
            throws Exception
    {
        throw new Exception("Use execute(null)");
    }

    /**
     * No operation
     * 
     * @param validationType
     * @param content
     * @return Empty ServiceResponse
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String validationType, String content)
            throws Exception
    {
        throw new Exception("Use execute(null)");
    }

    /**
     * Do validations under control of TKW properties file (validator configuration
     * location, and directories for messages-to-validate and results), and the
     * validator configurations themselves.
     * 
     * @param content Should be null.
     * @return Empty ServiceResponse on success.
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(Object content)
            throws Exception
    {
        ServiceResponse response = null;
        int validated = 0;
        String files[] = sourceDirectory.list();
        if (files.length == 0) {
            System.err.println("Nothing to do: source directory is empty");
            return new ServiceResponse(0, null);
        }
        ArrayList<ValidationReport>reports = new ArrayList<ValidationReport>();
        String runDate = LOGFILEDATE.format(new Date());
        ValidatorFactory vf = ValidatorFactory.getInstance();
        for (String f : files) {
            String service = null;
            boolean gotValidateAs = false;
            try {
                SpineMessage sm = new SpineMessage(sourceDirectory.getPath(), f);
                service = sm.getService();
                ValidationSet vs = vf.getValidationSet(service);
                if (vs == null) {
                    ArrayList<ValidationReport> reps = new ArrayList<ValidationReport>();
                    ValidationReport v = new ValidationReport("Validation failed: No validation rules found for: " + service);
                    v.setFilename(f);
                    reps.add(v);
                    reports.addAll(reps);
                } else {
                    ArrayList<ValidationReport> reps = vs.doSpineValidations(sm, this);
                    for (ValidationReport v: reps) {
                        v.setFilename(f);
                    }
                    reports.addAll(reps);
                }
            }
            catch (Exception e) {
                ValidationReport vr = new ValidationReport("ERROR: Exception thrown validating file " + f + " : " + e.getMessage());
                vr.setFilename(f);
                // e.printStackTrace();
                vr.setTest(e.toString());
                reports.add(vr);
            }
        }

        writeSummaryReport(reports, runDate, files.length);

        return new ServiceResponse(validated, null);
    }

}
