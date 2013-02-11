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

package org.warlock.tk.internalservices.rules;
import java.util.ArrayList;
/**
 * Class to encapsulate a list of Rule instances.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
class RuleSet {

    private ArrayList<Rule> rules = null;
    private String requestType = null;

    RuleSet(String rq)
            throws Exception
    {
        requestType = rq;
        rules = new ArrayList<Rule>();
    }

    void addRule(Rule r) { rules.add(r); }

    /**
     * Evaluate each Rule in the list until a Response is identified.
     * 
     * @param o String form of the request message
     * @return Response instance
     * @throws Exception If processing runs off the end of the list without identifying a response.
     */
    Response execute(String o)
            throws Exception
    {
        Response result = null;
        for (Rule r : rules) {
            Response resp = r.evaluate(o);
            if (resp != null) {
                result = resp;
                break;
            }
        }
        if (result == null) {
            throw new Exception("Rules for request type " + requestType + " ran off the end of processing without identifying a response");
        }
        return result;
    }

}