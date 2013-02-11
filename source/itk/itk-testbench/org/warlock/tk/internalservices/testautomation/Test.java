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
import java.io.File;
import java.io.FileInputStream;
import org.warlock.tk.boot.ToolkitService;
import org.warlock.tk.boot.ServiceResponse;
import org.warlock.tk.internalservices.Reconfigurable;
 /**
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class Test 
    implements Linkable
{
    private static final int SEND_TKW = 1;
    private static final int SEND_RAW = 2;
    private static final int FUNCTION = 3;
    
    private static final int NAME = 0;
    private static final int SEND = 1;
    private static final int MESSAGE = 2;
    private static final int CFG = 3;
    
    private Message tosend = null;
    private PassFailCheck synccheck = null;
    private PassFailCheck asynccheck = null;
    private AsynchronousLogCorrelator correlator = null;
    private int sendhow = 0;
    
    private String msgname = null;
    private String toUrl = null;
    private String fromUrl = null;
    private String replyTo = null;
    private String synccheckname = null;
    private String asynccheckname = null;
    private String commentText = null;
    private int wait = 0;
    
    private Script script = null;
    private TestFunction function = null;
    private String[] functionArgs = null;
    
    private String name = null;
    
    public Test(String[] line) 
            throws Exception
    {
        name = line[0];
        if (line[SEND].contentEquals("SEND_TKW")) {
            sendhow = SEND_TKW;
        }
        if (line[SEND].contentEquals("SEND_RAW")) {
            sendhow = SEND_RAW;
        }
        if (line[SEND].contentEquals("FUNCTION")) {
            sendhow = FUNCTION;
        }
        if (sendhow == FUNCTION) {
            functionArgs = line;
            return;
        } else {
            msgname = line[MESSAGE];
        }
        for (int i = CFG; i < line.length - 1; i++) {
            if (line[i].contentEquals("TEXT")) {
                commentText = line[++i];
                continue;
            }
            if (line[i].contentEquals("SYNC")) {
                synccheckname = line[++i];
                continue;
            }
            if (line[i].contentEquals("ASYNC")) {
                asynccheckname = line[++i];
                continue;
            }
            if (line[i].contentEquals("TO")) {
                toUrl = line[++i];
                continue;
            }
            if (line[i].contentEquals("FROM")) {
                fromUrl = line[++i];
                continue;
            }
            if (line[i].contentEquals("REPLYTO")) {
                replyTo = line[++i];
                continue;
            }
            if (line[i].contentEquals("WAIT")) {
                try {
                    wait = Integer.parseInt(line[++i]);
                }
                catch (Exception e) {
                    throw new Exception("Invalid WAIT in test " + name + " : " + line[i]);
                }
                continue;
            }           
        }
    }
    
    void setScript(Script s) { script = s; }
    
    @Override
    public void link(ScriptParser p)
            throws Exception
    {
        if (sendhow == FUNCTION) {
            String fname = "tks.autotest.testfunction." + functionArgs[MESSAGE];
            fname = p.getBootProperties().getProperty(fname);
            if (fname == null) {
                throw new Exception("No class defined for test function " + functionArgs[MESSAGE]);
            }
            function = (org.warlock.tk.internalservices.testautomation.TestFunction)Class.forName(fname).newInstance();
            function.init(functionArgs);
            return;
        }
        tosend = p.getMessage(msgname);
        if (tosend == null) {
            throw new Exception("Test " + name + " : message " + msgname + " not found");
        }
        if (synccheckname != null) {
            synccheck = p.getPassFailCheck(synccheckname);
            if (synccheck == null) {
                throw new Exception("Test " + name + " requested synchronous check " + synccheckname + " not found");
            }
        }
        if (asynccheckname != null) {
            asynccheck = p.getPassFailCheck(asynccheckname);
            if (asynccheck == null) {
                throw new Exception("Test " + name + " requested asynchronous check " + asynccheckname + " not found");
            }
        }
    }
    
    public String getName() { return name; }
    
    boolean execute(String instanceName, Schedule s) 
            throws Exception
    {
        // 1. The transmitter and simulator should be running at this point with the
        //      appropriate directories configured.
        // 2. Make the necessary messages and put them in the transmitter send directory
        // 3. Call execute() on the transmitter to do the sending
        // 4. Do the checks to see if it worked
        // 5. Log everything
        // 6. Clean up the transmitter directory

        ReportItem ri = null;
        if (sendhow == FUNCTION) {            
            return function.execute(instanceName, s, this);            
        }
        String filename = tosend.instantiate(s.getTransmitterDirectory(), toUrl, fromUrl, replyTo);
        ToolkitService transmitter = script.getSimulator().getService("Transmitter");
        if (toUrl != null) {
            ((Reconfigurable)transmitter).reconfigure("address", toUrl);
        }
        ServiceResponse r = transmitter.execute(null);
                
        File transmitterLog = getTransmitterLogFile(s, filename);
        if (transmitterLog == null) {
            ri = new ReportItem(s.getName(), name, false, "Transmission failed: No transmitter log file found");
            ri.addDetail(commentText);
            s.getScript().log(ri);
            return false;
        }
        boolean allPassed = true;
        if (!synccheck.passed(s.getScript(), new FileInputStream(transmitterLog))) {
            allPassed = false;
        }
        ri = new ReportItem(s.getName(), name, allPassed, synccheck.getDescription());
        ri.addDetail(commentText);
        ri.setLogFile(transmitterLog.getAbsolutePath());
        s.getScript().log(ri);

        // NOTE: the way that the correlator is identified is not built yet, so only
        // the BasicMessageIdCorrelator will be used for now.
        //
        if (correlator == null) {
            correlator = new BasicMessageIdCorrelator();
        }
        File asynchronousResponseLog = null;
        if (asynccheck != null) {
            boolean asyncpassed = true;
            asynchronousResponseLog = getAsynchronousResponseLogFile(s, transmitterLog);
            if (!asynccheck.passed(s.getScript(), new FileInputStream(asynchronousResponseLog))) {
               allPassed = false; 
               asyncpassed = false;
            }
            ri = new ReportItem(s.getName(), name, asyncpassed, asynccheck.getDescription());
            ri.addDetail(commentText);
            ri.setLogFile(asynchronousResponseLog.getAbsolutePath());
            s.getScript().log(ri);
        }
        // Clean up.
        File tsource = new File(s.getTransmitterDirectory());
        File[] sources = tsource.listFiles();
        for (File f : sources) {
            f.delete();
        }
        return allPassed;
    }
    
    private File getAsynchronousResponseLogFile(Schedule s, File transmitterLog)
            throws Exception
    {
        // Correlate simulator saved messages by "RelatesTo"
        // with the current message id.
        //
        int retryCount = Integer.parseInt(s.getScript().getProperty("tks.autotest.asynchronous.log.retries"));
        int retryDelay = Integer.parseInt(s.getScript().getProperty("tks.autotest.asynchronous.log.delay"));
        for (; retryCount != 0; --retryCount) {
            File f = new File(s.getSimulatorDirectory());
            File[] list = f.listFiles();
            for (File x : list) {
                if (correlator.correlate(x, tosend)) {
                    return x;
                }
            }
            try {
                synchronized(this) {
                    wait(retryDelay);
                }
            }
            catch(InterruptedException e) {}
        }
        return null;
    }
    
    private File getTransmitterLogFile(Schedule s, String source)
            throws Exception
    {
        // Need a configurable delay in here to prevent the transmitter log check
        // failing because it looks before the sender has had a chance to write the file
        
        int retryCount = Integer.parseInt(s.getScript().getProperty("tks.autotest.synchronous.log.retries"));
        int retryDelay = Integer.parseInt(s.getScript().getProperty("tks.autotest.synchronous.log.delay"));
        for (; retryCount != 0; --retryCount) {
            File f = new File(s.getSentMessagesDirectory());
            File[] list = f.listFiles();
            for (File x : list) {
                if (x.getName().startsWith(source)) {
                    return x;
                }
            }
            try {
                synchronized(this) {
                    wait(retryDelay);
                }
            }
            catch(InterruptedException e) {}
        }
        return null;
    }
}
