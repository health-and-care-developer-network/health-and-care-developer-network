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
package org.warlock.tk.experimental.distributor.SpineInjector;
import org.warlock.tk.boot.ToolkitSimulator;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.HashMap;
/**
 * THIS PACKAGE IS NOT TO BE USED. Development of a Spine message for transmitting
 * ITK requests is underway, but will NOT use the "forward reliable" message used here.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class SpineInjectorFactory
{
    private static final SpineInjectorFactory me = new SpineInjectorFactory();
    private static Exception bootException = null;

    private String commonContentTemplate = null;
    private HashMap<String, DirectoryEntry> directory = null;
    private String spineAddress = null;
    private String spinePath = null;
    private String proxyAddress = null;
    private int proxyPort = -1;
    private String localAsid = null;
    private String localPartyKey = null;

    private SpineInjectorFactory() {}

    public static SpineInjectorFactory getInstance() { return me; }

    DirectoryEntry getDirectoryEntry(String asid) 
        throws Exception
    {
        if (asid == null) {
            throw new Exception("Null asid");
        }
        if (!directory.containsKey(asid)) {
            throw new Exception("No such asid: " + asid);
        }
        return directory.get(asid);
    }

    String getCommonContentTemplate() { return commonContentTemplate; }

    public void runSpineInjector(ToolkitSimulator s, String msg, String asid)
            throws Exception
    {
        if (bootException != null) {
            throw bootException;
        }
        if (commonContentTemplate == null) {
            loadTemplate(s);
            loadDirectory(s);
        }
        SpineInjector si = new SpineInjector(me, msg, asid);
    }

    private void loadDirectory(ToolkitSimulator s)
            throws Exception
    {
        try {
            String f = s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.spineinjectordirectory");
            if (f == null) {
                throw new Exception("No Spine message template");
            }
            directory = new HashMap<String, DirectoryEntry>();
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    DirectoryEntry d = new DirectoryEntry(line);
                    directory.put(d.getASID(), d);
                }
            }
            fr.close();
        }
        catch (Exception e) {
            throw new Exception("Failed to load directory: " + e.getMessage());
        }
        localAsid = s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.localasid");
        if (localAsid == null) {
            throw new Exception("tks.experimental.distributor.localasid not set");
        }
        localPartyKey = s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.localpartykey");
        if (localPartyKey == null) {
            throw new Exception("tks.experimental.distributor.localpartykey not set");
        }
        spineAddress  = s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.spineaddress");
        if (spineAddress == null) {
            throw new Exception("tks.experimental.distributor.spineaddress not set");
        }
        spinePath = s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.spinepath");
        if (spinePath == null) {
            throw new Exception("tks.experimental.distributor.spinepath not set");
        }
        proxyAddress = s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.proxyaddress");
        if (proxyAddress == null) {
            throw new Exception("tks.experimental.distributor.proxyaddress not set");
        }

        proxyPort = Integer.parseInt(s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.proxyport"));
    }

    String getSpineAddress() { return spineAddress; }
    String getSpinePath() { return spinePath; }
    String getProxyAddress() { return proxyAddress; }
    String getLocalAsid() { return localAsid; }
    String getLocalPartyKey() { return localPartyKey; }
    int getProxyPort() { return proxyPort; }
    
    private void loadProxyDetails(ToolkitSimulator s)
            throws Exception
    {
        try {
            proxyAddress = s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.proxyaddress");
            String pp = s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.proxyport");
            proxyPort = Integer.parseInt(pp);
            spineAddress = s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.spineaddress");
            spinePath = s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.spinepath");
            localAsid = s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.localasid");
            localPartyKey = s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.localpartykey");
        }
        catch (Exception e) {
            throw new Exception("Error getting proxy details: " + e.getMessage());
        }
    }

    private void loadTemplate(ToolkitSimulator s)
        throws Exception
    {
        try {
            String f = s.getService("Toolkit").getBootProperties().getProperty("tks.experimental.distributor.spineinjectortemplate");
            if (f == null) {
                throw new Exception("No Spine message template");
            }
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            fr.close();
            commonContentTemplate = sb.toString();
        }
        catch (Exception e) {
            bootException = e;
            throw e;
        }
    }
}
