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
 * Class to carry descriptions of messages in a queue, for the TKW internal
 * messaging queue manager service. This class contains a set of members
 * that map onto message and queue names, addresses and identifiers, plus 
 * boilerplate accessors for them.
 * 
 * @author Damian Murphy <murff@warlock.org>
 */ 

public class QueueItem {

    private String queueName = null;
    private String message = null;
    private String address = null;
    private String action = null;
    private String messageId = null;
    private String refToMessageId = null;
    private String[] confirmIds = null;
    private int queryMaxItems = 0;

    public QueueItem(String q, String m, String ad, String ac) {
        queueName = q;
        message = m;
        address = ad;
        action = ac;
    }

    public void setQueryMaxItems(int i) { queryMaxItems = i; }
    public int getQueryMaxItems() { return queryMaxItems; }

    public void setMessageId(String id) { messageId = id; }
    public void setRefToMessageId(String id) { refToMessageId = id; }
    public void setConfirmIds(String[] ids) { confirmIds = ids; }

    public String getRefToMessageId() { return refToMessageId; }
    public String getMessageId() { return messageId; }
    public String[] getConfirmIds() { return confirmIds; }
    
    public String getQueueName() { return queueName; }
    public String getMessage() { return message; }
    public String getAction() { return action; }
    public String getAddress() { return address; }
}
