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
import java.util.HashMap;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.boot.ToolkitSimulator;
import org.warlock.tk.internalservices.validation.ValidatorFactory;
import org.warlock.tk.internalservices.validation.ValidationSet;
import org.warlock.tk.internalservices.validation.ValidationReport;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import org.warlock.util.CfHNamespaceContext;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.warlock.itklogverifier.LogVerifier;
/**
 * Service to implement the TKW "validate" mode. Driven by configurations in the
 * TKW properties file, and validator configurations.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class ValidatorService
    implements org.warlock.tk.boot.ToolkitService
{
    private static final String SOURCEPROPERTY = "tks.validator.source";
    private static final String REPORTPROPERTY = "tks.validator.reports";

    private static final String ACTIONXPATH = "/soap:Envelope/soap:Header/wsa:Action";
    private static final String SERVICEXPATH = "//itk:DistributionEnvelope/itk:header/@service";
    
    private String serviceName = null;
    private ToolkitSimulator simulator = null;
    private Properties bootProperties = null;
    private File    sourceDirectory = null;
    private File    reportDirectory = null;
    private XPathExpression actionExtractor = null;
    private XPathExpression distributionEnvelopeServiceExtractor = null;
    
    private static SimpleDateFormat LOGFILEDATE = new SimpleDateFormat("yyyyMMddHHmmss");

    private FileWriter supportingDataFileWriter = null;
    private File supportingDataFile = null;
    private boolean supportingDataWritten = false;
    
    /** Creates a new instance of ValidatorService */
    public ValidatorService() {}

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
        XPath x = XPathFactory.newInstance().newXPath();
        x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
        actionExtractor = x.compile(ACTIONXPATH);
        
        x = XPathFactory.newInstance().newXPath();
        x.setNamespaceContext(CfHNamespaceContext.getXMLNamespaceContext());
        distributionEnvelopeServiceExtractor = x.compile(SERVICEXPATH);        
        
        ValidatorFactory.getInstance().clear();        
        ValidatorFactory.getInstance().init(bootProperties);
        
        if (bootProperties.containsKey("tks.phxmlconverter.clustermap")) {
            System.setProperty("tks.phxmlconverter.clustermap", bootProperties.getProperty("tks.phxmlconverter.clustermap"));
        }
        
        System.out.println(serviceName + " started, class: " + this.getClass().getCanonicalName());
    }

    /**
     * "Supporting data" is created by validations that work on some derivative of the message under
     * test, where the validator output is written with respect to the derivative and not the original
     * message. An example is CDA "template schemas" where the message for validation is in the
     * "wire" form, and is converted to a "template" form for additional schema validation. The templated
     * form is "supporting data" and is saved to a file using this method.
     * 
     * @param s Supporting data for writing.
     */
    public void writeSupportingData(String s) {
        try {
            if (supportingDataFileWriter == null) {
                return;
            }
            supportingDataFileWriter.write(s);
            supportingDataFileWriter.flush();
            supportingDataWritten = true;
        }
        catch (Exception e) {
            System.err.println("Exception writing supporting data: " + e.getMessage());
        }
    }
    
    private void initSupportingData(String filename, String runDate) {
        try {
            String fn = "supportingData_" + filename + "-" + runDate + ".out";
            supportingDataFile = new File(reportDirectory, fn);
            supportingDataFileWriter = new FileWriter(supportingDataFile);        
        }
        catch (Exception e) {
            System.err.println("Exception creating supporting data file: " + e.getMessage());
            supportingDataFileWriter = null;
        }
        supportingDataWritten = false;
    }
    
    private void closeSupportingData() {
        if (supportingDataFileWriter == null) {
            return;
        }
        try {
            supportingDataFileWriter.flush();
            supportingDataFileWriter.close();
            if (!supportingDataWritten) {
                supportingDataFile.delete();
            }
            supportingDataFile = null;
        }
        catch (Exception e) {}
    }
    
    /**
     * Validate the "messages for validation" directory contents as configured in the
     * TKW properties file.
     * 
     * @param content Not used.
     * @return Empty ServiceResponse.
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(Object content)
            throws Exception
    {
        HashMap<String,String> links = null;
        try {
            if (content != null) {
                links = ((HashMap<String,String>)content);
            }
        }
        catch(Exception e) {
            links = null;
            System.err.println("ValidatorService.execute(Object) called with non-null parameter that is not a link set");
        }
        
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
            String vm = null;
            boolean gotValidateAs = false;
            initSupportingData(f, runDate);
            try {
                File valFile = new File(sourceDirectory, f);
                FileReader fr = new FileReader(valFile);
                BufferedReader br = new BufferedReader(fr);
                StringBuilder sb = new StringBuilder();
                boolean firstLine = true;
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        if (line.startsWith("VALIDATE-AS:")) {
                            service = line.substring("VALIDATE-AS:".length());
                            if ((service == null) || (service.trim().length() == 0)) {
                                throw new Exception("Malformed VALIDATE-AS directive, should be VALIDATE-AS: servicename");
                            }
                            service = service.trim();
                            gotValidateAs = true;
                            continue;
                        }
                    }
                    sb.append(line);
                    sb.append("\n");
                }
                vm = sb.toString();
                if (service == null) {
                    if ((service = getSoapAction(vm)) != null) {
                        gotValidateAs = false;
                    } else {
                        if ((service = getService(vm)) != null) {
                            gotValidateAs = true;
                        } else {
                            throw new Exception("Failed to resolve service name via SOAP Action or DistributionEnvelope service - try adding VALIDATE-AS: with the service name as first line in the file");
                        }
                    }
                }
                ValidationSet vs = vf.getValidationSet(service);
                if (vs == null) {
                    ArrayList<ValidationReport> reps = new ArrayList<ValidationReport>();
                    ValidationReport v = new ValidationReport("Validation failed: Unrecognised service: " + service);
                    v.setFilename(f);
                    reps.add(v);
                    reports.addAll(reps);
                } else {
                    ArrayList<ValidationReport> reps = vs.doValidations(vm, !gotValidateAs, this);
                    for (ValidationReport v: reps) {
                        v.setFilename(f);
                    }
                    reports.addAll(reps);
                }
                closeSupportingData();
            }
            catch (Exception e) {
                ValidationReport vr = new ValidationReport("ERROR: Exception thrown validating file " + f + " : " + e.getMessage());
                vr.setFilename(f);
                vr.setTest(e.getMessage());
                reports.add(vr);
            }
        }

        String fn = writeSummaryReport(reports, runDate, files.length, links);

        return new ServiceResponse(validated, fn);
    }

    private String writeSummaryReport(ArrayList<ValidationReport> vr, String dateString, int numfiles, HashMap<String,String> links)
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
               if (links == null) {
                    sb.append(v.getFilename());
               } else {
                   if (links.containsKey(v.getFilename())) {
                       sb.append("<a href=\"");
                       sb.append(links.get(v.getFilename()));
                       sb.append("\">");
                       sb.append(v.getFilename());
                       sb.append("</a>");
                   } else {
                       sb.append(v.getFilename());
                   }
               }
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
               if ((v.getContext() != null) && (v.getIteration() != -1)) {
                   sb.append(" in ");
                   sb.append(v.getContext());
                   sb.append(" instance ");
                   sb.append(v.getIteration());
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
               if ((v.getContext() != null) && (v.getIteration() != -1)) {
                   sb.append(" in ");
                   sb.append(v.getContext());
                   sb.append(" instance ");
                   sb.append(v.getIteration());
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
       String dontDoSignature = System.getProperty("tks.skipsignlogs");
       if ((dontDoSignature == null) || (dontDoSignature.toUpperCase().startsWith("Y"))) {
           LogVerifier l = LogVerifier.getInstance();
           l.makeSignature(f.getCanonicalPath());
       }
       return filename;

    }

    private String getSoapAction(String vm)
            throws Exception
    {
        String s = null;
        InputSource is = new InputSource(new StringReader(vm));
        s = actionExtractor.evaluate(is);
        if ((s == null) || (s.trim().length() == 0)) {
            return null;
        }
        return s;
    }
    
    private String getService(String vm)
            throws Exception
    {
        String s = null;
        InputSource is = new InputSource(new StringReader(vm));
        s = distributionEnvelopeServiceExtractor.evaluate(is);
        if ((s == null) || (s.trim().length() == 0)) {
            return null;
        }
        return s;
    }

    /**
     * No operation
     * 
     * @param validationType
     * @param o
     * @return
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String validationType, Object o)
            throws Exception
    {
        throw new Exception("Use execute(null)");
    }

    /**
     * No operation.
     * 
     * @param validationType
     * @param content
     * @return
     * @throws Exception 
     */
    @Override
    public ServiceResponse execute(String validationType, String content)
            throws Exception
    {
        throw new Exception("Use execute(null)");
    }
}
