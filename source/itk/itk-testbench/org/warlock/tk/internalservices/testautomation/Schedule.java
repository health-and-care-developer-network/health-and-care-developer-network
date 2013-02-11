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
import java.util.Properties;
import java.io.File;
import org.warlock.util.ConfigurationTokenSplitter;
import org.warlock.tk.internalservices.Reconfigurable;
 /**
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class Schedule 
    implements Linkable
{

    private String name = null;
    private File logRoot = null;
    private String simulatorConfig = null;
    private String transmitterSource = null;
    private String simulatorMessages = null;
    private String transmitterSentMessages = null;
    private Properties bootProperties = null;
    private Script script = null;
    
    private ArrayList<ScheduleElement> tests = new ArrayList<ScheduleElement>();
    
    public Schedule(String[] line)
            throws Exception
    {
        name = line[0];
        for (int i = 2; i < line.length; i++) {
            if (line[i].startsWith("LOOP(")) {
                String subSchedule = line[i].substring("LOOP(".length() + 1, line[i].length() - 1).trim();
                String[] subParts = null;
                try {
                    subParts = (new ConfigurationTokenSplitter(subSchedule)).split();
                    ScheduleElement se = new ScheduleElement(ScheduleElement.LOOP, subParts);
                    tests.add(se);
                }
                catch (Exception e) {
                    throw new Exception("Error parsing schedule: " + name + " : " + e.toString());
                }
            } else {
                tests.add(new ScheduleElement(line[i]));
            } 
        }
    }
    
    String getName() { return name; }
    Script getScript() { return script; }
    void setSimulator(String s) { simulatorConfig = s; }
    
    
    @Override
    public void link(ScriptParser p) 
            throws Exception
    {
        bootProperties = p.getBootProperties();
        try {
            for (ScheduleElement se: tests) {
                se.getTests(p);
            }
        }
        catch (Exception e) {
            throw new Exception("Schedule " + name + " error: " + e.getMessage());
        }
    }
    
   void execute(Script s, String r) 
           throws Exception
   {
       // 1. Make schedule log directory structure
       // 2. If simulatorConfig is set, calculate the simulator save directory names
       // 3. Update with generated directories.
       // 4. Start the rules engine and "Toolkit" if necessary for handling asynchronous responses or to receive
       // 5. Call execute on each of the ScheduleElement instances in tests       
       //
       script = s;
       ReportItem ri = null;
       ri = new ReportItem(name, null, "Starting");
       s.log(ri);
       String sof = s.getProperty("tks.autotest.stoponfail");
       boolean stoponfail = false;
       if ((sof == null) || (sof.toUpperCase().startsWith("Y"))) {
            stoponfail = true;
            ri = new ReportItem(name, null, "This test run will stop if any part fails");
            s.log(ri);
       } else {
           ri = new ReportItem(name, null, "This test run will continue if any part fails");
           s.log(ri);
       }
       
       // This assumes that all the logged messages for one schedule can go into the same set
       // of directories... which is *probably* true.
       //
       setupScheduleLogs();
       for (ScheduleElement se : tests) {
           boolean elementResult = se.execute(this, r, logRoot);
           if (!elementResult && stoponfail) {
               break;
           }
       }
   }
      
   String getTransmitterDirectory() { return transmitterSource; }
   String getSentMessagesDirectory() { return transmitterSentMessages; }
   String getSimulatorDirectory() { return simulatorMessages; }
   
   private void setupScheduleLogs()
           throws Exception
   {
       // Make the directories, and call the appropriate reconfiguration
       // methods on the services, to have them used.
       
       Properties p = script.getBootProperties();
       try {
           logRoot = new File(script.getRunDirectory(), name);
           logRoot.mkdirs();
           File ts = new File(logRoot, "transmitter_source");
           ts.mkdirs();
           transmitterSource = ts.getAbsolutePath();
           p.setProperty("tks.transmitter.source", transmitterSource);
           Reconfigurable transmitter = (Reconfigurable)script.getSimulator().getService("Transmitter");
           transmitter.reconfigure("sourceDirectory", transmitterSource);
           File td = new File(logRoot, "transmitter_sent_messages");
           td.mkdirs();
           transmitterSentMessages = td.getAbsolutePath();
           p.setProperty("tks.transmitter.destination", transmitterSentMessages);
           Reconfigurable sender = (Reconfigurable)script.getSimulator().getService("Sender");
           sender.reconfigure("destinationDirectory", transmitterSentMessages);
           File sm = new File(logRoot, "simulator_saved_messages");
           sm.mkdirs();
           simulatorMessages = sm.getAbsolutePath();
           p.setProperty("tks.savedmessages", simulatorMessages);        
           Reconfigurable toolkit = (Reconfigurable)script.getSimulator().getService("Toolkit");
           toolkit.reconfigure("savedMessagesDirectory", simulatorMessages);
       }
       catch (Exception e) {
           ReportItem rdf = new ReportItem(name, null, "Directory setup failed");
           rdf.addDetail(e.toString());
           script.log(rdf);
           throw e;
       }
   }
   
   private void startServices() 
           throws Exception
   {
       Properties p = script.getBootProperties();
       p.setProperty("tks.rules.configuration.file", simulatorConfig);
       
       
       
   }
}
