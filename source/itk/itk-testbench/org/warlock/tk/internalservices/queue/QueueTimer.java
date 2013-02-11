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
package org.warlock.tk.internalservices.queue;
import java.util.HashMap;
import org.warlock.tk.internalservices.QueueManagerService;
import org.warlock.util.Logger;
/**
 * Timeout counter for queues that need it.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
class QueueTimer
    extends Thread
{
    private HashMap<String,Long> queues = null;
    private static QueueTimer instance = new QueueTimer();

    private static final long DEFAULTPERIOD = 1000;
    private long period = DEFAULTPERIOD;
    private QueueManagerService qms = null;

    private QueueTimer() {
        queues = new HashMap<String,Long>();
        start();
    }

    public static QueueTimer getInstance() { return instance; }

    public void setQueueManagerService(QueueManagerService q) { qms = q; }

    public void addQueue(String n, long t) {
        Long l = new Long(t);
        queues.put(n, l);

        // Adjust so we're running often enough to handle the fastest queue
        //
        if (period > t) {
            period = t;
        }
    }

    @Override
    public void run() {
        while(true) {
            if (qms != null) {
                // For each name, get the queue, and then check that any uncommitted
                // data that is older then its timeout is put back into the main queue

                for (String q : queues.keySet()) {
                    try {
                        DeliveryQueue dq = qms.getQueue(q);
                        if (dq != null) {
                            dq.timecheck();
                        }
                    }
                    catch (Exception e) {
                        Logger.getInstance().log("QueueTimer.run()", "Exception: " + e.getMessage());
                    }
                }
            }
            try {
                sleep(period);
            }
            catch (Exception e) {}
        }
    }
}
