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
import java.util.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileWriter;
 /**
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
class ReportWriter {
    static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");    
    
    private String runDate = ReportWriter.FORMAT.format(new Date());
    private Script script = null;
    private File runDirectory = null;
    private ArrayList<ReportItem> items = new ArrayList<ReportItem>();
    private String validatorReport = null;
    
    private boolean dumped = false;
    
    ReportWriter(Script s, File l) {
        script = s;
        runDirectory = l;
    }
    
    void setValidatorReport(String v) { validatorReport = v; }
    
    @Override
    protected void finalize() 
            throws Throwable
    {
        super.finalize();
        if (!dumped) {
            dump();
        }
    }
    
    void log(ReportItem r) { items.add(r); }
    
    void dump()
            throws Exception
    {
        if (dumped) {
            return;
        }
        dumped = true;
        File f = new File(runDirectory, "test_log.html");
        FileWriter fw = new FileWriter(f);
        
        fw.write("<html><head><title>TKW Automated Test Report: ");
        fw.write(script.getName());
        fw.write(" run at ");
        fw.write(runDate);
        fw.write("</title></head><body><h1>Automated Test Report</h1>");
        fw.write("<h3>Run at: ");
        fw.write(runDate);
        fw.write(" using test script ");
        fw.write(script.getName());
        fw.write("</h3>");
        fw.flush();
        // Summary
        int testcount = 0;
        int passcount = 0;
        for (ReportItem i : items) {
            if (i.passedPresent()) {
                testcount++;
                if (i.passed()) {
                    passcount++;
                }
            }
        }
        fw.write("<h2>Summary</h2><table><tr><td>Tests run</td><td>");
        fw.write(Integer.toString(testcount));
        fw.write("</td></tr><tr><td>Passed</td><td>");
        fw.write(Integer.toString(passcount));
        fw.write("</td></tr></table><br/>");
        fw.write("<h3>Validator report: ");
        if (validatorReport == null) {
            fw.write(" <font color=\"#900000\">validator not run</font></h3><br/>");
        } else {
            fw.write(" <a href=\"./validator_reports/");
            fw.write(validatorReport);
            fw.write("\">");
            fw.write(validatorReport);
            fw.write("</a></h3><br/>");
        }
        fw.flush();
        fw.write("<h2>Results</h2><table><tr bgcolor=\"#000000\">");
        fw.write("<td><font color=\"#FFFFFF\"><b>Time</b></font></td>");
        fw.write("<td><font color=\"#FFFFFF\"><b>Schedule</b></font></td>");
        fw.write("<td><font color=\"#FFFFFF\"><b>Test</b></font></td>");
        fw.write("<td><font color=\"#FFFFFF\"><b>Passed</b></font></td>");
        fw.write("<td><font color=\"#FFFFFF\"><b>Comment</b></font></td></tr>");
        int row = 0;

        for (ReportItem r : items) {
           String bgc = null;
           if ((row % 2) == 0){
               bgc = "#FFFFFF";
           } else {
               bgc = "#EAEAEA";
           }
           fw.write("<tr bgcolor=\"");
           fw.write(bgc);
           fw.write("\">");
           String colour = null;
           if (r.passedPresent()) {
               colour = (r.passed()) ? "#008000" : "#900000";
           } else {
               colour = "#000000";
           }
           fw.write(writeCell(r.getTime(), colour));
           fw.write(writeCell(r.getSchedule(), colour));
           if (r.getLogFile() != null) {
               StringBuilder sb = new StringBuilder("<a href=\"");
               sb.append(Script.getRelativeLinkPath(r.getLogFile(), runDirectory, false));
               sb.append("\">");
               sb.append(r.getTest());
               sb.append("</a>");
               fw.write(writeCell(sb.toString(), colour));
           } else {
               fw.write(writeCell(r.getTest(), colour));
           }
           fw.write(writeCell(r.getPassed(), colour));
           fw.write(writeCell(r.getComment(), colour));
           fw.write("</tr>");

           if (r.getDetail() != null) {
               fw.write("<tr bgcolor=\"");
               fw.write(bgc);
               fw.write("\"><td colspan=\"5\">");
               fw.write("<font color=\"");
               fw.write(colour);
               fw.write("\">");
               fw.write(r.getDetail());
               fw.write("</font></td></tr>");
           }
           fw.flush();
           ++row; 
        }
        fw.write("</table>");
        fw.write("<hr/>Prepared by: ");
        fw.write(org.warlock.tk.boot.ToolkitSimulator.VERSION);
        fw.write("</body></html>");
        fw.flush();
        fw.close();
    }
    
    private String writeCell(String content, String clr) {
        if (content == null) {
            return "<td/>";
        }
        StringBuilder sb = new StringBuilder("<td><font color=\"");
        sb.append(clr);
        sb.append("\">");
        sb.append(content);
        sb.append("</font></td>");
        return sb.toString();
    }
}
