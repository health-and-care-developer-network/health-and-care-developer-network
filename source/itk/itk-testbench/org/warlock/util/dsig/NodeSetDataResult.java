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
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Node;
 /**
 * Support for digital signatures and verification. Used by the DOMURIdereferencer
 * to return node sets of de-referenced URIs.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class NodeSetDataResult
    implements javax.xml.crypto.NodeSetData
{
    private ArrayList<Node> nodes = new ArrayList<Node>();

    public void put(Node n) { nodes.add(n); }
    @Override
    public Iterator iterator() { return nodes.iterator(); }
}
