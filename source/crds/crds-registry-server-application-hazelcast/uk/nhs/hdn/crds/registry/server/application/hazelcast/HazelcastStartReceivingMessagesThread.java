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

package uk.nhs.hdn.crds.registry.server.application.hazelcast;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;
import uk.nhs.hdn.crds.registry.patientRecordStore.PatientRecordStore;
import uk.nhs.hdn.crds.registry.server.HazelcastConfiguration;
import uk.nhs.hdn.crds.registry.server.startReceivingMessagesThread.StartReceivingMessagesThread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class HazelcastStartReceivingMessagesThread implements StartReceivingMessagesThread
{
	public static final int MaximumDrainSize = 10000;
	@NotNull private final BlockingQueue<StuffEventMessage> stuffEventMessages;

	public HazelcastStartReceivingMessagesThread(@NotNull final HazelcastConfiguration hazelcastConfiguation)
	{
		stuffEventMessages = hazelcastConfiguation.rootQueue();
	}

	@NotNull
	@Override
	public AtomicBoolean startReceivingMessagesThread(@NotNull final PatientRecordStore patientRecordStore)
	{
		final AtomicBoolean terminationSignal = new AtomicBoolean(false);
		new Thread(new HazelcastReceivingMessagesRunnable(terminationSignal, stuffEventMessages, patientRecordStore), "HazelcastStartReceivingMessagesThread").start();
		return terminationSignal;
	}

}
