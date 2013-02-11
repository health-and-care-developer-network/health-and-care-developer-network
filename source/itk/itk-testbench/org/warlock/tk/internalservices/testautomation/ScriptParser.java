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
import java.util.HashMap;
import java.util.Properties;
import java.io.FileReader;
import java.io.BufferedReader;
import org.warlock.util.ConfigurationTokenSplitter;
 /**
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class ScriptParser {
    
    public static final int NOTHING = -1;
    public static final int TEST = 1;
    public static final int DATASOURCE = 2;
    public static final int SCHEDULE = 3;
    public static final int EXTRACTOR = 4;
    public static final int PASSFAIL = 5;
    public static final int MESSAGE = 6;
    public static final int TEMPLATE = 7;
    
    public static final String TYPES[] = {"", "TESTS", "DATASOURCES", "SCHEDULES", "EXTRACTORS",
                                        "PASSFAIL", "MESSAGES", "TEMPLATES"};
    
    private HashMap<String,DataSource> datasources = new HashMap<String,DataSource>();
    private HashMap<String,Message> messages = new HashMap<String,Message>();
    private HashMap<String,PassFailCheck> passfailchecks = new HashMap<String,PassFailCheck>();
    private HashMap<String,ResponseExtractor> extractors = new HashMap<String,ResponseExtractor>();
    private HashMap<String,Template> templates = new HashMap<String,Template>();
    private HashMap<String,Test> tests = new HashMap<String,Test>();
    private ArrayList<Schedule> schedules = new ArrayList<Schedule>();
    
    private Script script = null;
    private BufferedReader reader = null;
    private int reading = -1;
    private Properties bootProperties = null;
    
    public ScriptParser(Properties p) {
        script = new Script();
        bootProperties = p;
        script.setProperties(p);
    }

    Properties getBootProperties() { return bootProperties; }
    
    public Script parse(String file)
            throws Exception
    {
        reader = new BufferedReader(new FileReader(file));
        compile();
        reader.close();
        link();
        return script;
    }
           
    private void compile() 
            throws Exception
    {
        String line = null;
        reading = NOTHING;
        int lineNumber = 0;
        while((line = reader.readLine()) != null) {
            lineNumber++;
            // Ignore comments and blank lines
            if (line.startsWith("#"))
                continue;
            line = line.trim();
            if (line.length() == 0)
                continue;
            String[] parts = (new ConfigurationTokenSplitter(line)).split();

            if (line.startsWith("SCRIPT")) {
                script.setName(parts[1]);
                continue;
            }
            if (line.contentEquals("STOP WHEN COMPLETE")) {
                script.setStopWhenComplete();
                continue;
            }
            if (line.startsWith("SIMULATOR")) {
                script.setSimulatorRules(parts[1]);
                continue;
            }
            if (line.startsWith("VALIDATOR")) {
                script.setValidatorConfig(parts[1]);
                continue;
            }
            if (line.startsWith("BEGIN")) {
                if (reading != NOTHING) {
                    throw new Exception("Syntax error at line " + lineNumber + " : " + line + " found when already reading " + TYPES[reading]);
                }
                reading = resolveType(parts[parts.length - 1]);
                if (reading == NOTHING) {
                    throw new Exception("Syntaxt error at line " + lineNumber + " : Unrecognised component: " + parts[parts.length - 1]);
                }
                continue;
            }
            if (line.startsWith("END")) {
                if (reading != resolveType(parts[parts.length - 1])) {
                    throw new Exception("Syntax error at line " + lineNumber + " :" + line + " does not match BEGIN " + TYPES[reading]);
                }
                reading = NOTHING;
                continue;
            }
            switch (reading) {
                case TEST:
                    Test t = new Test(parts);
                    t.setScript(script);
                    tests.put(t.getName(), t);
                    break;
                    
                case DATASOURCE:
                    DataSource ds = makeDataSource(parts);
                    datasources.put(ds.getName(), ds);
                    break;
                    
                case SCHEDULE:
                    Schedule s = new Schedule(parts);
                    schedules.add(s);
                    break;
                    
                case EXTRACTOR:
                    ResponseExtractor re = makeExtractor(parts);
                    extractors.put(re.getName(), re);
                    break;
                    
                case PASSFAIL:
                    PassFailCheck pf = makePassFail(parts);
                    passfailchecks.put(pf.getName(), pf);
                    break;
                    
                case MESSAGE:
                    Message m = new Message(parts);
                    messages.put(m.getName(), m);
                    break;
                    
                case TEMPLATE:
                    Template tp = new Template(parts);
                    templates.put(tp.getName(), tp);
                    break;
                   
                case NOTHING:
                default:
                    break;
            }
        }
    }
    
    private int resolveType(String t) {
        for (int i = 1; i < TYPES.length; i++) {
            if (TYPES[i].contentEquals(t))
                return i;
        }
        return NOTHING;
    }
    
    DataSource getDataSource(String name) { return datasources.get(name); }
    Test getTest(String name) { return tests.get(name); }
    PassFailCheck getPassFailCheck(String name) { return passfailchecks.get(name); }
    Message getMessage(String name) { return messages.get(name); }
    ResponseExtractor getExtractor(String name) { return extractors.get(name); }
    Template getTemplate(String name) { return templates.get(name); }
    
    private void link() 
            throws Exception
    {
        // For each of the linkables, call link()
        for (Message m : messages.values()) {
            m.link(this);
        }
        for (DataSource d : datasources.values()) {
            d.link(this);
        }
        for (PassFailCheck p : passfailchecks.values()) {
            p.link(this);
        }
        for (Test t : tests.values()) {
            t.link(this);
        }
        for (Schedule s : schedules) {
            s.link(this);
            script.addSchedule(s);
        }
        
    }

    private PassFailCheck makePassFail(String[] parts)
            throws Exception
    {
        String pfclass = "tks.autotest.passfail." + parts[1];
        pfclass = bootProperties.getProperty(pfclass);
        if (pfclass == null) {
            throw new Exception("PassFail check " + parts[1] + " has no class defined");
        }
        PassFailCheck pf = (PassFailCheck)Class.forName(pfclass).newInstance();
        pf.init(parts);
        return pf;
    }
    
    private ResponseExtractor makeExtractor(String[] parts)
            throws Exception
    {
        String reclass = "tks.autotest.extractor." + parts[1];
        reclass = bootProperties.getProperty(reclass);
        if (reclass == null) {
            throw new Exception("ResponseExtractor " + parts[1] + " has no class defined");
        }
        ResponseExtractor re = (ResponseExtractor)Class.forName(reclass).newInstance();
        re.init(parts);
        return re;
    }
    
    private DataSource makeDataSource(String[] parts)
            throws Exception
    {        
        // parts[0] is the datasource name, parts[1] is the type
        //
        String dsclass = "tks.autotest.datasource." + parts[1];
        dsclass = bootProperties.getProperty(dsclass);
        if (dsclass == null) {
            throw new Exception("DataSource " + parts[1] + " has no class defined");
        }
        DataSource ds = (DataSource)Class.forName(dsclass).newInstance();
        ds.init(parts);
        return ds;
    }
    
}
