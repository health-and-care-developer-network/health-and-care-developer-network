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
import java.text.SimpleDateFormat;
 /**
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
class ReportItem {
    
   
    private String added = ReportWriter.FORMAT.format(new Date());
    private String scheduleName = null;
    private String testName = null;
    private Boolean testPassed = null;
    private String logComment = null;
    private String detail = null;
    private String logfile = null;
    
    ReportItem(String schedule, String test, boolean passed, String comment) {
        scheduleName = schedule;
        testName = test;
        testPassed = Boolean.valueOf(passed);
        logComment = comment;        
    }
    
    ReportItem(String schedule, String test, String comment) {
        scheduleName = schedule;
        testName = test;
        logComment = comment;                
    }
    
    void setLogFile(String f) { logfile = f; }
    void addDetail(String s) { detail = s; }
    
    String getLogFile() { return logfile; }
    String getTime() { return added; }
    String getSchedule() { return scheduleName; }
    String getTest() { return testName; }
    String getComment() { return logComment; }
    String getDetail() { return detail; }
    String getPassed() {
        if (testPassed == null) return "";
        return (testPassed.booleanValue()) ? "PASS" : "FAIL";
    }
    boolean passedPresent() { return testPassed != null; }
    boolean passed() { return ((testPassed != null) && testPassed.booleanValue()); }
}
