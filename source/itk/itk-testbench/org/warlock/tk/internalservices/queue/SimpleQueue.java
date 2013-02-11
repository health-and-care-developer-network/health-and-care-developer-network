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
import java.util.ArrayList;
import java.util.HashMap;
import org.safehaus.uuid.UUIDGenerator;
/**
 * Trivial unconfirmed TKW internal message queue.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class SimpleQueue
    implements DeliveryQueue
{
    private HashMap<String, QueueItem> queue = null;
    private String name = null;

    public SimpleQueue() {
        queue = new HashMap<String,QueueItem>();
    }

    @Override
    public int getSize() { return queue.size(); }

    @Override
    public void start(String s, long t){ name = s; }

    @Override
    public String getName() { return name; }

    @Override
    public void add(QueueItem q) {
        if (q.getMessageId() == null) {
            q.setMessageId(UUIDGenerator.getInstance().generateTimeBasedUUID().toString().toUpperCase());
        }
        queue.put(q.getMessageId(),q);
    }

    @Override
    public QueueItem getSingle(QueueItem q) {
        if (q.getMessageId() == null) {
            return null;
        }
        QueueItem i = queue.get(q.getMessageId());
        if (i == null) {
            return null;
        }
        queue.remove(q.getMessageId());
        return i;
    }

    @Override
    public QueueItem[] getBatch(QueueItem q) 
    {
        QueueItem[] r = null;
        if (queue.isEmpty()) {
            return new QueueItem[0];
        }
        int count = 0;
        ArrayList<QueueItem> matches = new ArrayList<QueueItem>();
        for (QueueItem i : queue.values()) {
            matches.add(i);
            queue.remove(i.getMessageId());
            ++count;
            if (count > i.getQueryMaxItems()) {
                break;
            }
        }
        r = new QueueItem[matches.size()];
        return matches.toArray(r);
    }

    @Override
    public void confirm(String id) {
        // This sort of queue isn't reliable so we don't do confirmations
    }

    @Override
    public void batchConfirm(String[] ids) {
        // This sort of queue isn't reliable so we don't do confirmations
    }

    @Override
    public void timecheck() {
        // Not a reliable queue
    }
}
