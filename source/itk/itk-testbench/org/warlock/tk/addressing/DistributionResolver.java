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
package org.warlock.tk.addressing;
import org.warlock.tk.boot.ToolkitSimulator;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;
/**
 * Class to encapsulate and use routing tables for the partial implementation
 * of the ITK routing specification provided by TKW.
 * 
 * The property tks.addressing.routingtable should be set to the fully-
 * qualified path of the routing table file.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class DistributionResolver
        implements Resolver
{
    private static final String RESOLVERFILE = "tks.addressing.routingtable";
    private ToolkitSimulator simulator = null;
    private HashMap<String, ResolverTreeEntry> topLevelRoutes = new HashMap<String, ResolverTreeEntry>();
    
    public DistributionResolver(ToolkitSimulator t)
            throws Exception
    {
        simulator = t;
        String resolverfile = t.getService("Toolkit").getBootProperties().getProperty(RESOLVERFILE);
        init(resolverfile);
    }

    private void init(String r)
            throws Exception
    {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(r);
            br = new BufferedReader(fr);
        }
        catch (Exception e) {
            throw new Exception("Cannot initialise resolver file " + r + " : " + e.getMessage());
        }
        String line = null;
        while((line = br.readLine()) != null) {
            if (line.trim().length() == 0) {
                continue;
            }
            if (line.startsWith("#")) {
                continue;
            }
            StringTokenizer st = new StringTokenizer(line, "\t");
            if (st.countTokens() != 5) {
                throw new Exception("Malformed resolver file " + r + " : " + line);
            }
            String amatch = st.nextToken();
            String svc = st.nextToken();
            String t = st.nextToken();
            String route = st.nextToken();
            String auth = st.nextToken();
            addEntry(amatch, svc, t, route, auth);
        }
    }

    private ArrayList<String> splitAddress(String address)
        throws AddressingException
    {
        if (address == null) {
            throw new AddressingException("Null address");
        }
        if (!address.startsWith("urn:nhs-uk:addressing:")) {
            throw new AddressingException("Invalid address format " + address);
        }
        String a = address.substring(22);
        StringTokenizer st = new StringTokenizer(a, ":");
        ArrayList<String> parts = new ArrayList<String>();
        while(st.hasMoreTokens()) {
            parts.add(st.nextToken());
        }
        return parts;
    }

    private void addEntry(String a, String svc, String type, String r, String auth)
            throws Exception
    {
        ArrayList<String> parts = splitAddress(a);
        ResolverTreeEntry root = null;
        String s = parts.get(0);
        if (s == null) {
            throw new Exception("Malformed address: " + a);
        }
        if (topLevelRoutes.containsKey(s)) {
            root = topLevelRoutes.get(s);
        } else {
            root = new ResolverTreeEntry(s);
            topLevelRoutes.put(s, root);
        }
        root.add(parts, svc, type, r, auth);
    }

    /**
     * Resolve the given ITK-style address.
     * 
     * @param a ITK format address.
     * @return A routing solution
     * @throws AddressingException 
     */
    @Override
    public RoutingSolution resolve(String a)
            throws AddressingException
    {
        return resolve(a, null);
    }

    /**
     * Resolve the given ITK address and service name
     * @param a ITK-style address
     * @param s Service name
     * @return A routing solution.
     * @throws AddressingException 
     */
    @Override
    public RoutingSolution resolve(String a, String s)
            throws AddressingException
    {
        if (a.contains("*")) {
            throw new AddressingException("Invalid address - cannot contain wildcards");
        }
        if (a.contains(" ")) {
            throw new AddressingException("Invalid address - cannot contain spaces");
        }
        ArrayList<String> parts = splitAddress(a);
        String root = parts.get(0);
        ResolverTreeEntry rte = null;
        if (!topLevelRoutes.containsKey(root)) {
            rte = topLevelRoutes.get("*");
            if (rte == null) {
                throw new AddressingException("No routing information found for address: " + a);
            }
            // If we're here, we have a catch-all route and we're using it which means that
            // nothing beyond the first element in the address is going to help. So strip
            // everything but the first element and pass it for resolution.
            parts = new ArrayList<String>();
            parts.add(root);
            return rte.resolve(parts, s);
        } else {
            rte = topLevelRoutes.get(root);
            RoutingSolution r = rte.resolve(parts, s);
            if (r != null) {
                return r;
            }
            if (topLevelRoutes.containsKey("*")) {
                r = topLevelRoutes.get("*").resolve(parts, root);
                if (r != null) {
                    return r;
                }
            }
        }
        throw new AddressingException("Address not resolved");
    }

}
