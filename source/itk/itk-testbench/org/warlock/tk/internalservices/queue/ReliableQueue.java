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
import java.util.Hashtable;
import org.safehaus.uuid.UUIDGenerator;
/**
 * Implementation of a TKW internal message queue that requires message receipt
 * to be confirmed by the requestor.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class ReliableQueue
    implements DeliveryQueue
{
    private Hashtable<String, ManagedQueueItem> queue = null;
    private Hashtable<String, ManagedQueueItem> uncommitted = null;
    private String name = null;
    private long timeout = 0;

    public ReliableQueue() {
        queue = new Hashtable<String,ManagedQueueItem>();
        uncommitted = new Hashtable<String,ManagedQueueItem>();
    }

    @Override
    public void start(String n, long t) {
        name = n;
        timeout = t;
        if (name != null) {
            QueueTimer.getInstance().addQueue(name, t);
        }
    }

    @Override
    public int getSize() { return queue.size(); }


    @Override
    public String getName() { return name; }

    @Override
    public void add(QueueItem q) {
        if (q.getMessageId() == null) {
            q.setMessageId(UUIDGenerator.getInstance().generateTimeBasedUUID().toString().toUpperCase());
        }
        if (!queue.containsKey(q.getMessageId()) && !uncommitted.containsKey(q.getMessageId())) {
            ManagedQueueItem m = new ManagedQueueItem(q);
            queue.put(q.getMessageId(),m);
        }
    }

    @Override
    public QueueItem getSingle(QueueItem q) {
        if (q.getMessageId() == null) {
            return null;
        }
        ManagedQueueItem i = queue.get(q.getMessageId());
        if (i == null) {
            return null;
        }
        uncommitted.put(i.getMessageId(), i);
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
        for (ManagedQueueItem i : queue.values()) {
            matches.add(i);
            uncommitted.put(i.getMessageId(), i);
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
        if (queue.containsKey(id)) {
            return;
        }
        if (uncommitted.containsKey(id)) {
            uncommitted.remove(id);
        }
    }

    @Override
    public void batchConfirm(String[] ids) {
        for (String s : ids) {
            this.confirm(s);
        }
    }

    @Override
    public void timecheck() {
        if (timeout == 0) {
            return;
        }
         for (ManagedQueueItem i : uncommitted.values()) {
             if (i.isOlder(timeout)) {
                 queue.put(i.getMessageId(), i);
                 uncommitted.remove(i.getMessageId());
             }
         }
    }

}
