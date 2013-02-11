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

/**
 * Encapsulation of a physical address over which a message may be routed, plus type-
 * specific information such as a URL.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class PhysicalAddress {

    public static final int UNSPECIFIED = 0;
    public static final int QUEUE = 1;
    public static final int URL = 2;
    public static final int ASID = 3;
    public static final int DTS = 4;
    public static final int REDIRECTION = 5;
    public static final int SECURITYBLOCK = 6;
    public static final int NOLONGERSUPPORTEDBLOCK = 7;
    public static final int IHEXDR = 8;
    
    private static final String[] TYPES = {"unspecified", "queue", "url", "asid", "dts", "redirect", "securityalert", "unsupported", "ihexdr"};
    private int type = UNSPECIFIED;
    private String value = null;
    private boolean authoritative = false;
    private boolean starService = false;

    public PhysicalAddress(String v, String t, boolean a)
        throws Exception
    {
        for (int i = 1; i < TYPES.length; i++) {
            if (t.contentEquals(TYPES[i])) {
                type = i;
                break;
            }
        }
        if (type == UNSPECIFIED) {
            throw new Exception("Failed to initialise physical address: Unspecified type " + t);
        }
        value = v;
        authoritative = a;
    }
    public void setStarService() { starService = true; }
    public boolean isAuthoritative() { return authoritative; }
    public boolean isStarService() { return starService; }
    public String getAddress() { return value; }
    public int getType() { return type; }

    /**
     * Object.hashCode() overridden so different addresses which resolve to the same
     * physical route, will only be sent once.
     * @return 
     */
    @Override
    public int hashCode() {
        int h = type;
        if (value != null) {
            byte[] bv = value.trim().toUpperCase().getBytes();
            for (byte b : bv) {
                h += b;
            }
        }
        return h;
    }

    /**
     * Object.equals() overridden so that addresses which resolve to the same
     * route, will only be sent once. Equality is based on type and value.
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        try {
            PhysicalAddress pa = (PhysicalAddress)o;
            if (pa.getType() != type) {
                return false;
            }
            String a = pa.getAddress();
            if (a == null) {
                if (value == null) {
                    return true;
                } else {
                    return false;
                }
            }
            if (value == null) {
                return false;
            }
            return (value.trim().toUpperCase().contentEquals(a.trim().toUpperCase()));
        }
        catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }

}
