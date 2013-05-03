/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hdn.crds.registry.server.application;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;
import uk.nhs.hdn.crds.registry.patientRecordStore.PatientRecordStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public final class EventListenerRunnable implements Runnable
{
	private static final int SinkSize = 10000;

	private final BlockingQueue<StuffEventMessage> incomingEvents;
	private final PatientRecordStore patientRecordStore;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public EventListenerRunnable(@NotNull final BlockingQueue<StuffEventMessage> incomingEvents, @NotNull final PatientRecordStore patientRecordStore)
	{
		this.incomingEvents = incomingEvents;
		this.patientRecordStore = patientRecordStore;
	}

	@SuppressWarnings("InfiniteLoopStatement")
	@Override
	public void run()
	{
		final Collection<StuffEventMessage> sink = new ArrayList<>(SinkSize);
		do
		{
			// drain is more efficient than take() as it requires coarser locking
			incomingEvents.drainTo(sink, SinkSize);
			if (sink.isEmpty())
			{
				continue;
			}
			for (final StuffEventMessage stuffEventMessage : sink)
			{
				patientRecordStore.addEvent(stuffEventMessage);
			}
			sink.clear();
		} while(true);

		// TODO: Obviously, some cleaner shutdown logic is needed to avoid event loss
	}
}
