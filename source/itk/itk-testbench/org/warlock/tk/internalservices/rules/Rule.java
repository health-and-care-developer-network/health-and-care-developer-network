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

/**
 * Class to encapsulate an Expression, and Response members for "true" and
 * "false" results for evaluating the Expression.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
class Rule {

    private Expression expression = null;
    private Response trueResponse = null;
    private Response falseResponse = null;

    Rule(Expression exp, Response t, Response f)
            throws Exception
    {
        expression = exp;
        trueResponse = t;
        falseResponse = f;
    }

    Response evaluate(String o)
            throws Exception
    {
        return (expression.evaluate(o)) ? trueResponse : falseResponse;
    }
}
