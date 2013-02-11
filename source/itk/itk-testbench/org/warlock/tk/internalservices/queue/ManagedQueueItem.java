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
import java.util.Calendar;
 /**
 * Subclass of QueueItem adding sequencing and timing information needed
 * for more sophisticated queues than "SimpleQueue".
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public class ManagedQueueItem
    extends QueueItem
{
    private static int sequenceNumber = 0;
    private long timestamp = 0;

    public ManagedQueueItem(QueueItem i) {
        super(i.getQueueName(), i.getMessage(), i.getAddress(), i.getAction());
        super.setMessageId(i.getMessageId());
        timestamp = Calendar.getInstance().getTimeInMillis();
    }

    public ManagedQueueItem(String q, String m, String ad, String ac) {
        super(q, m, ad, ac);
        timestamp = Calendar.getInstance().getTimeInMillis();
    }

    public synchronized void incrementSequence() { ++sequenceNumber; }

    public int getSequenceNumber() { return sequenceNumber; }
    public long getTimestamp() { return timestamp; }

    public boolean isOlder(long timeout) {
        long now = Calendar.getInstance().getTimeInMillis();
        return ((now - timestamp) > timeout);
    }
}
