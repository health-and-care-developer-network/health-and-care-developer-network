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
package org.warlock.util.dsig;
import org.w3c.dom.Attr;
 /**
 * Support for digital signatures, particularly the WS-Security header signing
 * used in ITK. This class implements comparison of XML attributes according
 * to the W3C Canonical XML specification as a java.util.Comparator which is 
 * used by the canonicalisation code to order attributes according to the W3C
 * spec.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public final class CanonicalAttributeComparator
    implements java.util.Comparator<Attr>
{
    public CanonicalAttributeComparator() {}

    @Override
    public int compare(Attr o1, Attr o2) {
        String ns1 = o1.getNamespaceURI();
        String ns2 = o2.getNamespaceURI();
        if (ns1 == null) {
            if (ns2 == null) {
                return o1.getLocalName().compareTo(o2.getLocalName());
            }
            if (ns2.contentEquals("http://www.w3.org/2000/xmlns/")) {
                return 1;
            }
            return -1;
        }
        if (ns1.contentEquals("http://www.w3.org/2000/xmlns/")) {
            if (ns2 == null) {
                return -1;
            }
            if (!ns2.contentEquals("http://www.w3.org/2000/xmlns/")) {
                return -1;
            }
            if (o1.getLocalName() == null) {
                return -1;
            }
            if (o2.getLocalName() == null) {
                return 1;
            }
            return o1.getLocalName().compareTo(o2.getLocalName());
        }
        if (ns2.contentEquals("http://www.w3.org/2000/xmlns/")) {
            return 1;
        }
        if (ns2 == null) {
            return 1;
        }
        int nscheck = ns1.compareTo(ns2);
        if (nscheck != 0) {
            return nscheck;
        }
        return o1.getLocalName().compareTo(o2.getLocalName());
    }

    @Override
    public boolean equals(Object o) {
        if (o.hashCode() != this.hashCode()) {
            return false;
        }
        try {
            CanonicalAttributeComparator c = (CanonicalAttributeComparator)o;
            return (c == this);
        }
        catch (Exception e) {
            return false;
        }
    }
}
