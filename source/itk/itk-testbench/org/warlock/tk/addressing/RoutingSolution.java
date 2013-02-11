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
import java.util.HashMap;
/**
 * A RoutingSolution encapsulates physical routes for an address (or address pattern),
 * potentially multiple physical routes distinguished by service.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class RoutingSolution {

    private HashMap<String, PhysicalAddress> solutions = null;
    private boolean starService = false;

    public RoutingSolution() {
        solutions = new HashMap<String, PhysicalAddress>();
    }

    RoutingSolution(RoutingSolution r) {
        // Yes, I know. This doesn't give me a nasty warning about casting the Object
        // return from HashMap.clone(), so the build looks cleaner. Otherwise it
        // achieves the same thing.
        HashMap<String, PhysicalAddress> soln = r.getSolutions();
        solutions = new HashMap<String, PhysicalAddress>();
        for (String k : soln.keySet()) {
            solutions.put(k, soln.get(k));
        }
    }

    /**
     * Is this routing solution for "any" service
     * @return 
     */
    public boolean isStarService() { return starService; }
    
    /**
     * Is this routing solution authoritative (routers in "inbound" mode may only
     * route to addresses for which they are authoritative, and those in "outbound"
     * mode are forbidden to route to authoritative addresses).
     * @return
     * @throws Exception 
     */
    public boolean isAuthoritative()
        throws Exception
    {
        if (solutions.entrySet().isEmpty()) {
            throw new Exception("Address resolves to routing solution with no physical routes");
        }
        if (solutions.entrySet().size() > 1) {
            throw new Exception("Address resolves to routing solution with multiple physical routes");
        }
        for (String s : solutions.keySet()) {
            PhysicalAddress p = solutions.get(s);
            return p.isAuthoritative();
        }
        throw new Exception("Corrupt addressing table");
    }

    HashMap<String, PhysicalAddress> getSolutions() { return solutions; }

    /**
     * Creates and adds to this RoutingSolution, a PhysicalAddress.
     * @param s Service name, or "*"
     * @param a The address or address pattern.
     * @param t Route type (see PhysicalAddress for types)
     * @param auth Whether the router is authoritative for this address.
     * @throws Exception 
     */
    public void addService(String s, String a, String t, boolean auth)
            throws Exception
    {
        if (solutions.containsKey(s)) {
            throw new AddressingException("Duplicate service specification: " + s);
        }
        PhysicalAddress p = new PhysicalAddress(a, t, auth);
        if (s.contentEquals("*")) {
            p.setStarService();
        }
        solutions.put(s, p);
    }

    /**
     * Get the PhysicalAddress for "any" service, or throw an exception.
     * @return
     * @throws AddressingException 
     */
    public PhysicalAddress getAddress()
            throws AddressingException
    {
        if (!solutions.containsKey("*")) {
            throw new AddressingException("No service specified and routing solution contains no * physical address");
        }
        return solutions.get("*");
    }

    /**
     * Get the PhysicalAddress for the requested service.
     * @param s The service name, or "*"
     * @return
     * @throws AddressingException if no PhysicalAddress exists for this service.
     */
    public PhysicalAddress getAddress(String s)
        throws AddressingException
    {
        if (s == null) {
            return getAddress();
        }
        if (!solutions.containsKey(s)) {
            if (!solutions.containsKey("*")) {
                throw new AddressingException("No service not found and routing solution contains no * physical address");
            }
            return solutions.get("*");
        }
        return solutions.get(s);
    }
}
