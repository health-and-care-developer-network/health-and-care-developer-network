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
package org.warlock.util;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.XMLConstants;
/**
 * Implementation of javax.xml.namespace.NamespaceContext with no dependencies
 * outside the "stock" Java class library. Uses generics collections.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class XMLNamespaceContext 
    implements javax.xml.namespace.NamespaceContext
{
    // variable for default URI
    private String defaultUri = null;
    // list for prefixed URIs <uri,list of prefixes>
    protected HashMap<String,ArrayList<String>> uris;
    
    /**
     * Constructor.
     * Creates a new instance of XMLNamespaceContext
     */
    public XMLNamespaceContext() {
        uris = new HashMap<String,ArrayList<String>>();
    }
    
    /**
     *  Declare a Namespace prefix.
     *
     *  Note that a Namespace URI can be bound to multiple prefixes in the current scope.
     *  A prefix can only be bound to a single Namespace URI in the current scope.
     *
     *  To declare the default element Namespace, use the empty string as the prefix.
     *  Warning: only one default namesapce is allowed, the existing one will be overwritten.
     *
     *  Note that there is an asymmetry in this library: getPrefix will not return the "" 
     *  prefix, even if you have declared a default element namespace. To check for a 
     *  default namespace, you have to look it up explicitly using getURI. This asymmetry 
     *  exists to make it easier to look up prefixes for attribute names, where the default 
     *  prefix is not allowed.
     *
     *  @param prefix  a namespace prefix
     *  @param uri     a namespace URI
     *
     */
    public void declarePrefix(String prefix, String uri) 
        throws IllegalArgumentException
    {
        if ((uri == null) || (prefix == null)) {
            throw new IllegalArgumentException("Namespace prefix and URI may not be null");
        }
        
        // default uri
        // only one default namesapce is allowed, the existing one will be overwritten.
        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            defaultUri = uri;
        }
        
        //  test if URI is bound to multiple prefixes or not? 
        if (uris.containsKey(uri)) {
            ArrayList<String> a = uris.get(uri);
            a.add(prefix);
        } else {
            ArrayList<String> a = new ArrayList<String>();
            a.add(prefix);
            uris.put(uri, a);
        }
    }

    /**
     * implements method getPrefix of javax.xml.namespace.NamespaceContext
     *  
     * Get prefix bound to Namespace URI in the current scope.
     *
     *  @param u    a namespace prefix
     *  @return     the first instance of namespaces bound to a prefix
     */
    @Override
    public String getPrefix(String u) 
        throws IllegalArgumentException
    {
        if (u == null)
            throw new IllegalArgumentException();
        
        // from javax.xml.XMLConstants
        if (u.equals(XMLConstants.XML_NS_URI)) {
            return XMLConstants.XML_NS_PREFIX;
        }
        
        // attribute from javax.xml.XMLConstants
        if (u.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            return XMLConstants.XMLNS_ATTRIBUTE;
        }
        
        if (!uris.containsKey(u))
            return null;
        // return the first url instance
        return uris.get(u).get(0);
    }
    
    /**
     * implements method getPrefix of javax.xml.namespace.NamespaceContext
     *
     * Get all prefixes bound to a Namespace URI in the current scope.
     *
     * @param u    a namespace prefix
     * @return     an iterator for the namespaces bound to a prefix
     */
    @Override
    public Iterator getPrefixes(String u)
        throws IllegalArgumentException
    {
        if (u == null)
            throw new IllegalArgumentException();
        if (u.equals(XMLConstants.XML_NS_URI)) {
            return makeSingleItemIterator(XMLConstants.XML_NS_PREFIX);
        }
        if (u.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            return makeSingleItemIterator(XMLConstants.XMLNS_ATTRIBUTE);
        }
        if (!uris.containsKey(u))
            return makeSingleItemIterator(null);
        
        return uris.get(u).iterator();
    }
    
    /**
     *   a Helper used by: Iterator getPrefixes(String u) 
     */
    private Iterator<String> makeSingleItemIterator(String s)
    {
       ArrayList<String> a = new ArrayList<String>();
       if (s != null)
        a.add(s);
       return a.iterator();       
    }
    
    /**
     * implements method getNamespaceURI of javax.xml.namespace.NamespaceContext
     * 
     * Get Namespace URI bound to a prefix in the current scope.
     *
     * @param p    a namespace prefix
     * @return     requesting a Namespace URI by prefix
     */
    @Override
    public String getNamespaceURI(String p)
        throws IllegalArgumentException
    {
        if (p == null)
            throw new IllegalArgumentException();
        
        // Default namespace
        if (p.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            return defaultUri;
        }
        
        //  from javax.xml.XMLConstants
        if (p.equals(XMLConstants.XML_NS_PREFIX)) {
            return XMLConstants.XML_NS_URI;
        }
        if (p.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        }
        
        // iterator user declared ones
        Iterator<String> it = uris.keySet().iterator();
        while (it.hasNext()) {
            String u = it.next();
            ArrayList<String> a = uris.get(u);
            if (a.contains(p)) {
                return u;
            }
        }
        return XMLConstants.NULL_NS_URI;
    }
}
