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

 /**
 * Delay function. Configured as a test takes first argument is delay in
 * milliseconds. Optional second argument is a threshold (again in milliseconds)
 * below which the delay will be retried if interrupted.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */

public class DelayFunction 
    implements TestFunction
{
    private static final int DELAYSPEC = 3;
    private static final int THRESHOLDSPEC = 4;
    
    private long threshold = 0;
    private long delay = 0;
    
    public DelayFunction() {}
    
    @Override
    public void init(String[] config) 
            throws Exception
    {
        delay = Long.parseLong(config[DELAYSPEC]);
        if (config.length > 4) {
            threshold = Long.parseLong(config[THRESHOLDSPEC]);
        }
    }
    
    @Override
    public boolean execute(String instanceName, Schedule s, Test t) 
            throws Exception
    {
        s.getScript().log(new ReportItem(s.getName(), t.getName(), "Delay " + delay + " milliseconds"));
        Long start = System.currentTimeMillis();
        Long wtime = delay;
        boolean retry = true;
        
        while (retry) {
            try {
                synchronized(this) {
                    wait(wtime);
                }
                retry = false;
            }
            catch (InterruptedException e) {
                if (threshold != 0) {
                    Long period = System.currentTimeMillis() - start;
                    if ((delay - period) < threshold) {
                        retry = false;
                    } else {
                        wtime = wtime - period;
                        if (wtime < 0) {
                            retry = false;
                        }
                    }
                } else {
                    retry = false;
                }
            }
        }
        return true;
    }
}

