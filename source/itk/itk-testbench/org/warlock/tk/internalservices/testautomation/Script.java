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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import org.warlock.tk.boot.ToolkitSimulator;
import org.warlock.tk.boot.ToolkitService;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.internalservices.Stoppable;
 /**
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class Script {
    
    private static final String RUNROOT = "tks.autotest.root";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    
    private ArrayList<Schedule> schedules = new ArrayList<Schedule>();
    private String name = null;
    private Properties bootProperties = null;
    private File runDirectory = null;
    private String simulatorRules = null;
    private String validatorConfig = null;
    private File validatorSource = null;
    private File validatorReports = null;
    private ToolkitSimulator toolkitSimulator = null;
    private ToolkitService validator = null;
    private boolean stopWhenComplete = false;
    private ReportWriter reportWriter = null;
    
    public Script() {}
    
    void addSchedule(Schedule s) { schedules.add(s); }
    void setStopWhenComplete() { stopWhenComplete = true; }
    
    void setName(String n) { name = n; }
    void setSimulatorRules(String r) { simulatorRules = r; }
    void setProperties(Properties p) { bootProperties = p; }
    Properties getBootProperties() { return bootProperties; }
    String getProperty(String s) { return bootProperties.getProperty(s); }
    File getRunDirectory() { return runDirectory; }
    File getValidatorSource() { return validatorSource; }
    File getValidatorReports() { return validatorReports; }
    ToolkitSimulator getSimulator() { return toolkitSimulator; }
    String getName() { return name; }
    
    void setValidatorConfig(String v) {
        validatorConfig = v; 
    }
    
    private void startValidator() 
        throws Exception 
    { 
        // Make the directories, set the properties, then create and boot 
        // the validator.
        
        bootProperties.setProperty("tks.validator.config", validatorConfig);
        validatorSource = new File(runDirectory, "messages_for_validation");
        validatorSource.mkdirs();
        validatorReports = new File(runDirectory, "validator_reports");
        validatorReports.mkdirs();
        bootProperties.setProperty("tks.validator.source", validatorSource.getAbsolutePath());
        bootProperties.setProperty("tks.validator.reports", validatorReports.getAbsolutePath());
        
        validator = (ToolkitService)toolkitSimulator.getService("Validator");
        validator.boot(toolkitSimulator, bootProperties, "TKW AutoTest Response Validator");
    }
    
    private void runValidations() 
            throws Exception
    {
        HashMap<String,String> fileMap = gatherReceivedMessages();
        ServiceResponse sr = validator.execute(fileMap);
        reportWriter.setValidatorReport(sr.getResponse());
    }
        
    void log(ReportItem r) {
        reportWriter.log(r);
    }
    
    public void clear() {
        runDirectory = null;
        reportWriter = null;
    }
    
    private HashMap<String,String> gatherReceivedMessages()
            throws Exception
    {
        // Go through each of the schedules, get its log files and grab the
        // response parts. Copy to the validatorSource directory and keep a 
        // record in a HashMap of the validation file names and the original log
        // file names. Then go to the simulator logs (if the simulator is running)
        // and get what it was sent. Again copy to the validatorSource and record
        // in the HashMap. Return the HashMap.
        
        HashMap<String,String> fileMap = new HashMap<String,String>();
        for (Schedule s : schedules) {
            copyTransmitterReturns(s.getSentMessagesDirectory(), fileMap);
            copySimulatorReturns(s.getSimulatorDirectory(), fileMap);
        }
        return fileMap;
    }
    
    static String getRelativeLinkPath(String absfile, File rd, boolean parent) {
        StringBuilder sb = new StringBuilder();
        if (parent) {
            sb.append("../");
        } else {
            sb.append("./");
        }
        sb.append(absfile.substring(rd.getAbsolutePath().length() + 1));
        int l = -1;
        while ((l = sb.indexOf("\\")) != -1) {
            sb.replace(l, l + 1, "/");
        }
        return sb.toString();
    }
    
    private void copyTransmitterReturns(String tdir, HashMap<String,String>links)
            throws Exception
    {
        // Go through the files in tdir, copying to a file in the validatorSource
        // logged information received from the SUT (i.e. everything after the 
        // delimiter between sent and received data in the log file)
        
        File logDir = new File(tdir);
        if (!logDir.isDirectory()) {
            throw new Exception("Error extracting validator data: " + tdir + " is not a directory");
        }
        File[] list = logDir.listFiles();
        for (File f : list) {
            if (f.getName().endsWith(".signature")) {
                continue;
            }
            String ofile = f.getName().substring(0, f.getName().indexOf(".")) + "_response.out";
            
            links.put(ofile, getRelativeLinkPath(f.getAbsolutePath(), runDirectory, true));
            
            File output = new File(validatorSource, ofile);
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = null;
            boolean gotResponse = false;
            boolean readingBody = false;
            FileWriter ofw = null; 
            while ((line = br.readLine()) != null) {
                if (gotResponse) {
                    if (readingBody) {
                        ofw.write(line);
                        ofw.flush();
                    } else {
                        if (line.trim().length() == 0) {
                            readingBody = true;
                        }
                    }
                } else {
                    if (line.startsWith("**** END REQUEST ****")) {
                        gotResponse = true;
                        ofw = new FileWriter(output);
                    }
                }
            }
            if (ofw != null) {
                ofw.flush();
                ofw.close();
            }
        }        
    }
    
    private void copySimulatorReturns(String sdir, HashMap<String,String>links)
            throws Exception
    {
        // Go through the files in sdir, copying to a file in the validatorSource
        // logged information received from the SUT (i.e. everything before the 
        // delimiter between sent and received data in the log file)
        File logDir = new File(sdir);
        if (!logDir.isDirectory()) {
            throw new Exception("Error extracting validator data: " + sdir + " is not a directory");
        }
        File[] list = logDir.listFiles();
        for (File f : list) {
            if (f.getName().endsWith(".signature")) {
                continue;
            }
            
            File output = new File(validatorSource, f.getName());

            links.put(output.getName(), getRelativeLinkPath(f.getAbsolutePath(), runDirectory, true));
            
            FileWriter ofw = null;
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = null;
            boolean readingBody = false;
            while ((line = br.readLine()) != null) {
                if (readingBody) {
                    if (line.startsWith("************ END OF INBOUND MESSAGE **************")) {
                        ofw.flush();
                        ofw.close();
                        break;
                    }
                    ofw.write(line);
                    ofw.write("\r\n");
                    ofw.flush();
                } else {
                    if (line.trim().length() == 0) {
                        readingBody = true;
                        ofw = new FileWriter(output);
                    }
                }
            }
        }
    }
    
    public void execute(ToolkitSimulator t)
            throws Exception
    {
        System.setProperty("tkw.internal.runningautotest", "true");
        toolkitSimulator = t;
        if (reportWriter != null) {
            throw new Exception("Script " + name + " already running");
        }
        String runid = name + "_" + FORMAT.format(new Date());
        makeLogs(runid);

        if (validatorConfig != null) {
            startValidator();
        }
        
        for (Schedule s : schedules) {
            s.setSimulator(simulatorRules);
            s.execute(this, runid);
        }
        if (stopWhenComplete) {
            stopSimulator();
        }
        if (validatorConfig != null) {
            runValidations();
        }
        System.getProperties().remove("tkw.internal.runningautotest");
        reportWriter.dump();
    }
    
    public void stopSimulator()
            throws Exception
    {
        Stoppable s = (Stoppable)toolkitSimulator.getService("Toolkit");
        s.stop();
    }
    
    private void makeLogs(String runid) 
            throws Exception
    {
        StringBuilder rootDir = new StringBuilder(bootProperties.getProperty(RUNROOT));
        rootDir.append(System.getProperty("file.separator"));
        rootDir.append(runid);
        runDirectory = new File(rootDir.toString());
        if (!runDirectory.mkdirs()) {
            throw new Exception("Failed to make root directory: " + runDirectory.getAbsolutePath());
        }
        reportWriter = new ReportWriter(this, runDirectory);
    }
    
}
