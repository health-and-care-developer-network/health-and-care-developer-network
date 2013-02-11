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
package org.warlock.tk.internalservices.testautomation;
import java.io.InputStream;
/**
 * WORK IN PROGRESS: Do not use.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
abstract public class AbstractPassFailCheck
    implements PassFailCheck
{
    protected String name = null;
    protected String type = null;
    protected String responseExtractorName = null;
    protected ResponseExtractor extractor = null;
    protected String description = null;
    
    @Override
    public void init(String[] line) 
            throws Exception
    {
        name = line[0];
        type = line[1];
        if (line[line.length - 2].contentEquals("EXTRACTOR")) {
            responseExtractorName = line[line.length - 1];
        }
    }
    
    @Override
    public void link(ScriptParser p) 
            throws Exception
    {
        if (responseExtractorName != null) {
            ResponseExtractor r = p.getExtractor(responseExtractorName);
            if (r == null) {
                throw new Exception("PassFailCheck " + name + " response extractor " + responseExtractorName + " not found");
            }
            extractor = r;
        }
    }
    
    @Override
    public String getName() { return name; }

    @Override
    abstract public boolean passed(Script s, InputStream in) throws Exception;    
    
    @Override
    public String getDescription() { return description; }
    
    protected void doExtract(String s)
            throws Exception
    {
        if (extractor == null) {
            return;
        }
        extractor.extract(s);
    }
   
}
