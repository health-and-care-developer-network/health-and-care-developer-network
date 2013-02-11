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
import java.util.Arrays;
import java.io.File;
 /**
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
class ScheduleElement {

    static final int SINGLE = 0;
    static final int LOOP = 1;
            
    private int type = -1;
    private String testName = null;
    private Test test = null;
    private ArrayList<String> subElements = null;
    private ArrayList<Test> testList = null;
    
    ScheduleElement(int t, String[] p) {
        type = t;
        subElements = new ArrayList<String>();
        subElements.addAll(Arrays.asList(p));
    }
    
    ScheduleElement(String n) {
        type = SINGLE;
        testName = n;
    }
    
    boolean isMultiple() { return (subElements != null); }
    
    void getTests(ScriptParser p) 
            throws Exception
    {
        if (type == SINGLE) {
            test = p.getTest(testName);
            if (test == null) {
                throw new Exception("Test " + testName + " not found");
            }            
        } else {
            testList = new ArrayList<Test>();
            for (String s : subElements) {
                Test t = p.getTest(s);
                if (t == null) {
                    throw new Exception("Test " + t + " not found");
                }
                testList.add(t);
            }
        }
    }
    
    boolean execute(Schedule schedule, String runName, File logroot) 
            throws Exception
    {
        boolean pass = true;
        String sof = schedule.getScript().getProperty("tks.autotest.stoponfail");
        boolean stoponfail = false;
        if ((sof == null) || (sof.toUpperCase().startsWith("Y"))) {
            stoponfail = true;
        }
        if (isMultiple()) {
            int i = 1;
            boolean keepGoing = true;
            while (keepGoing) {
                StringBuilder sb = new StringBuilder(schedule.getName());
                sb.append("/");
                sb.append(testName);
                sb.append("/");
                sb.append(i);
                for (Test t : testList) {
                    keepGoing = t.execute(sb.toString(), schedule);
                    if (!stoponfail) {
                        keepGoing = true;
                    }
                    if (!keepGoing) {
                        ReportItem r = new ReportItem(schedule.getName(), testName, "Loop break on test failure");
                        schedule.getScript().log(r);
                        pass = false;
                        break;
                    }
                }
                ++i;
            }
        } else {
            StringBuilder sb = new StringBuilder(schedule.getName());
            sb.append("/");
            sb.append(testName);
            pass = test.execute(sb.toString(), schedule);   
        }
        return pass;
    }
}

