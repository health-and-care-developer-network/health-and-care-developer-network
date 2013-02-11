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
import java.util.Hashtable;
import java.util.Vector;
import java.util.ArrayList;
import java.util.HashSet;
import org.safehaus.uuid.UUIDGenerator;
/**
 * Implementation of a TKW internal message queue that serves messages in the
 * order that they were received, and requires that requestors confirm receipt.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */
public class ReliableSequencingQueue
        implements DeliveryQueue
{
    private Vector<ManagedQueueItem> queue = null;
    private HashSet<String> active = null;
    private Hashtable<String, ManagedQueueItem> uncommitted = null;
    private String name = null;
    private long timeout = 0;

    public ReliableSequencingQueue() {
        queue = new Vector<ManagedQueueItem>();
        active = new HashSet<String>();
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
        if (!active.contains(q.getMessageId()) && !uncommitted.containsKey(q.getMessageId())) {
            ManagedQueueItem m = new ManagedQueueItem(q);
            m.incrementSequence();
            active.add(m.getMessageId());
            queue.add(m);
        }
    }

    @Override
    public QueueItem getSingle(QueueItem q) {
        if (q.getMessageId() == null) {
            return null;
        }
        for (ManagedQueueItem mqi : queue) {
            if (mqi.getMessageId().contentEquals(q.getMessageId())) {
                uncommitted.put(mqi.getMessageId(), mqi);
                active.remove(mqi.getMessageId());
                queue.remove(mqi);
                return mqi;
            }
        }
        return null;
    }

    @Override
    public QueueItem[] getBatch(QueueItem q)
    {
        QueueItem[] r = null;
        if (queue.isEmpty()) {
            return new QueueItem[0];
        }
        ArrayList<QueueItem> matches = new ArrayList<QueueItem>();
        int count = 0;
        for (ManagedQueueItem i : queue) {
            matches.add(i);
            uncommitted.put(i.getMessageId(), i);
            active.remove(i.getMessageId());
            queue.remove(i);
            ++count;
            if (count == i.getQueryMaxItems()) {
                break;
            }
        }
        r = new QueueItem[matches.size()];
        return matches.toArray(r);
    }

    @Override
    public void confirm(String id) {
        if (active.contains(id)) {
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
         for (ManagedQueueItem mqi : uncommitted.values()) {
             if (mqi.isOlder(timeout)) {
                 for (int i = 0; i < queue.size(); i++) {
                     if (queue.elementAt(i).getSequenceNumber() > mqi.getSequenceNumber()) {
                         queue.add(i, mqi);
                         active.add(mqi.getMessageId());
                         uncommitted.remove(mqi.getMessageId());
                     }
                 }
             }
         }
    }

}
