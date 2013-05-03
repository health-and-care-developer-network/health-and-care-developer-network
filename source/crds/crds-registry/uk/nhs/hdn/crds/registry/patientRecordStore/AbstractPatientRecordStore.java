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

package uk.nhs.hdn.crds.registry.patientRecordStore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.crds.registry.domain.PatientRecord;
import uk.nhs.hdn.crds.registry.domain.SimplePatientRecord;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;
import uk.nhs.hdn.crds.registry.server.eventObservers.EventObserver;
import uk.nhs.hdn.number.NhsNumber;

import java.util.concurrent.ConcurrentMap;

import static uk.nhs.hdn.crds.registry.domain.SimplePatientRecord.initialPatientRecord;

public abstract class AbstractPatientRecordStore<K, V extends PatientRecord<V>> implements PatientRecordStore
{
	@NotNull private final ConcurrentMap<K, V> root;
	@NotNull private final EventObserver<NhsNumber> eventObserver;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	protected AbstractPatientRecordStore(@NotNull final ConcurrentMap<K, V> root, @NotNull final EventObserver<NhsNumber> eventObserver)
	{
		this.root = root;
		this.eventObserver = eventObserver;
	}

	@Nullable
	@Override
	public SimplePatientRecord get(@NotNull final NhsNumber identifier)
	{
		return simplePatientRecordFor(root.get(key(identifier)));
	}

	@Override
	public final void addEvent(@NotNull final StuffEventMessage stuffEventMessage)
	{
		@NotNull final NhsNumber patientIdentifier = stuffEventMessage.patientIdentifier();

		try
		{
			final K key = key(patientIdentifier);
			@Nullable V oldPatientRecord = root.get(key);
			do
			{
				if (oldPatientRecord == null)
				{
					// No entry, try to add one

					final SimplePatientRecord simplePatientRecord = initialPatientRecord(stuffEventMessage);
					oldPatientRecord = root.putIfAbsent(key, value(simplePatientRecord));

					if (wasNewPatientSuccessfullyAdded(oldPatientRecord))
					{
						return;
					}
				}
				else
				{
					// Existing entry. Try to atomically replace it

					final V newPatientRecord = oldPatientRecord.addRepositoryEvent(stuffEventMessage);
					if (root.replace(key, oldPatientRecord, newPatientRecord))
					{
						return;
					}
					oldPatientRecord = root.get(key);
				}
			}
			while (true);
		}
		finally
		{
			eventObserver.storeChanged(patientIdentifier);
		}
	}

	@Nullable
	protected abstract SimplePatientRecord simplePatientRecordFor(@Nullable final V patientRecord);

	@NotNull
	protected abstract K key(@NotNull final NhsNumber patientIdentifier);

	@NotNull
	protected abstract V value(@NotNull final SimplePatientRecord simplePatientRecord);

	private static <V> boolean wasNewPatientSuccessfullyAdded(@Nullable final V oldPatientRecord)
	{
		return oldPatientRecord == null;
	}
}
