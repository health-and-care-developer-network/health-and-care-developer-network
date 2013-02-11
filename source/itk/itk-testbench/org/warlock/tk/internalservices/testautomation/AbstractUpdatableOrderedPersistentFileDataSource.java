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
import java.util.HashMap;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
/**
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public abstract class AbstractUpdatableOrderedPersistentFileDataSource 
    implements DataSource
{
    private boolean open = false;
    protected String name = null;
    protected String file = null;
    protected String extractorName = null;
    protected ResponseExtractor extractor = null;
    
    protected ArrayList<String> fields = new ArrayList<String>();
    protected ArrayList<String> recordids = new ArrayList<String>();
    protected HashMap<String,HashMap<String,String>> data = new HashMap<String,HashMap<String,String>>();
    
    @Override
    public void init(String[] lineParts)
            throws Exception
    {
        name = lineParts[0];
        file = lineParts[2];
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        boolean firstLine = true;
        while ((line = br.readLine())!= null) {
            if (firstLine) {
                readFields(line);
                firstLine = false;
            } else {
                readRecord(line);
            }
        }
        br.close();
        open = true;
    }

    private void readRecord(String l)
            throws Exception
    {
        String[] f = l.split("\\t");
        if (f.length != fields.size()) {
            throw new Exception("Data error in source " + name + " : Field id " + f[0] + " field-count mismatch");
        }
        recordids.add(f[0]);
        HashMap<String,String> record = new HashMap<String,String>();
        for (int i = 0; i < fields.size(); i++) {
            record.put(fields.get(i), f[i]);
        }
        data.put(f[0], record);
    }
    
    private void readFields(String l) {
        String[] f = l.split("\\t");
        fields.addAll(Arrays.asList(f));
    }
    
    @Override
    public void close()
            throws Exception
    {
        synchronized(this) {
            File old = new File(file);
            File bck = new File(file + ".backup");
            old.renameTo(bck);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < fields.size() - 1; i++) {
                bw.append(fields.get(i));
                bw.append("\t");
            }
            bw.append(fields.get(fields.size() - 1));
            bw.append("\n");
            bw.flush();
            for(int i = 0; i < recordids.size(); i++) {
                HashMap<String,String> rec = data.get(recordids.get(i));
                for (int j = 0; j < fields.size() - 1; j++) {
                    bw.append(rec.get(fields.get(j)));
                    bw.append("\t");
                }
                bw.append(rec.get(fields.get(fields.size() - 1)));
                bw.append("\n");
                bw.flush();
            }
            bw.close();
            open = false;
        }
    }
    
    @Override
    public String getName() { return name; }
    
    @Override
    public void link(ScriptParser p)
            throws Exception
    {
        if (extractorName != null) {
            extractor = p.getExtractor(extractorName);
            if (extractor == null) {
                throw new Exception("DataSource " + name + " extractor " + extractorName + " not found");
            }
            extractor.registerDatasourceListener(this);
        }
    }
    
    @Override
    public Iterable<String> getTags() { return fields; }
    
    @Override
    public String getValue(String id, String tag)
            throws Exception
    {
        HashMap<String,String> r = data.get(id);
        if (r == null) {
            throw new Exception("Unknown record id " + id + " in data source " + name);
        }
        if (!fields.contains(tag)) {
            throw new Exception("Unknown field " + tag + " requested from data source " + name);
        }
        String v = r.get(tag);
        return v;
    }
    
    @Override
    public void setValue(String id, String tag, String value)
            throws Exception
    {
        HashMap<String,String> r = data.get(id);
        if (r == null) {
            throw new Exception("Unknown record id " + id + " in write request to data source " + name);
        }
        if (!fields.contains(tag)) {
            throw new Exception("Unknown field " + tag + " tried to be written to data source " + name);
        }
        synchronized(this) {
            r.remove(tag);
            r.put(tag, value);
        }
    }
    
    @Override
    public boolean isReadOnly() { return false; }
    
    @Override
    public void finalize() 
            throws Throwable
    {
        super.finalize();
        if (open)
            this.close();
    }
}
