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
 */package org.warlock.tk.addressing;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Internal class to support the DistributionResolver. Routing table files are parsed
  * into a tree, each node is a ResolverTreeEntry which contains either more ResolverTreeENtry
  * instances, or RoutingSolutions.
  * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
class ResolverTreeEntry {

    private HashMap<String, ResolverTreeEntry> children = new HashMap<String, ResolverTreeEntry>();
    private RoutingSolution solution = null;
    private boolean starRoute = false;
    private String addressElement = null;

    ResolverTreeEntry(String a) {
        addressElement = a;
        if (a.contentEquals("*")) {
            starRoute = true;
        }
        solution = new RoutingSolution();
    }

    RoutingSolution resolve(ArrayList<String> a, String service)
        throws AddressingException
    {
        // We've been given an ArrayList containing the addressing parts from the point
        // at which our parent decided it could process. If there is only one element
        // in the list, then if that element either is our address element, OR we are
        // a "starRoute", then make a routing solution based on our routing solution,
        // filtered if necessary by service.

        // Otherwise... see if the second element is something that matches one of our children.
        // If it is, strip the first element and call "resolve" on the child. If not, but we
        // are a "starRoute", then return a routing solution based on our routing solution.
        // Otherwise throw an AddressingException saying we can't resolve.
        if (a.size() == 0) {
            throw new AddressingException("Invalid address");
        }
        String root = a.get(0);
        // TODO: This fails if we've gone down a specific address fragment, cannot resolve
        // *inside* that fragment, but could resolve *above* it. We signal that we've not
        // resolved by throwing an exception that forces us to give up rather than
        // resolving off the parent star route
        if (a.size() == 1) {
            if (root.contentEquals(addressElement)) {
                return solution;
            }
            if (starRoute) {
                return solution;
            }
            // Not found
            return null;
        } else {
            a.remove(0);
            String child = a.get(0);
            if (children.containsKey(child)) {
                RoutingSolution r = children.get(child).resolve(a, service);
                if (r != null) {
                    return r;
                }
                if (children.containsKey("*")) {
                    return children.get("*").resolve(a, service);
                }
            } else {
                if (children.containsKey("*")) {
                    return children.get("*").resolve(a, service);
                }
                return null;
            }
        }
        if (starRoute) {
            return solution;
        }
        throw new AddressingException("Cannot resolve address");
    }


    void add(ArrayList<String> address, String svc, String type, String r, String auth)
            throws Exception
    {
        // We've been given a routing table line. If this line is "us" then
        // the ArrayList "address" only has a single item in it, so once we've recorded
        // the details in the routing solution we can go.
        //
        // Otherwise, strip off the first element (which *is* "us"), and see what we have
        // in the way of child nodes. If we have one that matches the new first element, call
        // "add" on it, passing in the details. If not, make one using the first element,
        // add the new entry to the children keyed on the first address element, and *then*
        // call "add" on the entry we've just made.
        //
        if (address.size() > 1) {
            address.remove(0);
            String a = address.get(0);
            if (children.containsKey(a)) {
                children.get(a).add(address, svc, type, r, auth);
            } else {
                ResolverTreeEntry rte = new ResolverTreeEntry(a);
                children.put(a, rte);
                rte.add(address, svc, type, r, auth);
            }
        } else {
            solution.addService(svc, r, type, auth.toLowerCase().startsWith("a"));
        }
    }

}
