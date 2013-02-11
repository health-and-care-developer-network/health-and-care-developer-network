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
 /**
 * Interface implemented by TKW internal message queue classes.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 
public interface DeliveryQueue {
    public int getSize();
    public String getName();
    public void add(QueueItem q);
    public QueueItem getSingle(QueueItem q);
    public QueueItem[] getBatch(QueueItem q);
    public void confirm(String id);
    public void batchConfirm(String[] ids);
    public void start(String n, long t);
    public void timecheck();
}
