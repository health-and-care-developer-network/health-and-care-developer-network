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

package uk.nhs.hdn.crds.registry.server.hazelcast.patientRecordStore;

import com.hazelcast.core.IMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.crds.registry.patientRecordStore.AbstractPatientRecordStore;
import uk.nhs.hdn.crds.registry.domain.SimplePatientRecord;
import uk.nhs.hdn.crds.registry.server.hazelcast.hazelcastSerialisationHolders.NhsNumberHazelcastSerialisationHolder;
import uk.nhs.hdn.crds.registry.server.hazelcast.hazelcastSerialisationHolders.PatientRecordHazelcastSerialisationHolder;
import uk.nhs.hdn.crds.registry.server.eventObservers.DoNothingEventObserver;
import uk.nhs.hdn.crds.registry.server.eventObservers.EventObserver;
import uk.nhs.hdn.number.NhsNumber;

public final class HazelcastPatientRecordStore extends AbstractPatientRecordStore<NhsNumberHazelcastSerialisationHolder, PatientRecordHazelcastSerialisationHolder>
{
	@SuppressWarnings({"AssignmentToCollectionOrArrayFieldFromParameter", "FeatureEnvy"})
	public HazelcastPatientRecordStore(@NotNull final IMap<NhsNumberHazelcastSerialisationHolder, PatientRecordHazelcastSerialisationHolder> root, @NotNull final EventObserver<NhsNumber> eventObserver)
	{
		super(root, DoNothingEventObserver.<NhsNumber>doNothingRepositoryEventObserver());
		root.addEntryListener(new AdaptToEventObserverEntryListener(eventObserver), false);
	}

	@Nullable
	@Override
	protected SimplePatientRecord simplePatientRecordFor(@Nullable final PatientRecordHazelcastSerialisationHolder patientRecord)
	{
		if (patientRecord == null)
		{
			return null;
		}
		return patientRecord.heldValue();
	}

	@Override
	@NotNull
	protected NhsNumberHazelcastSerialisationHolder key(@NotNull final NhsNumber patientIdentifier)
	{
		return new NhsNumberHazelcastSerialisationHolder(patientIdentifier);
	}

	@Override
	@NotNull
	protected PatientRecordHazelcastSerialisationHolder value(@NotNull final SimplePatientRecord simplePatientRecord)
	{
		return new PatientRecordHazelcastSerialisationHolder(simplePatientRecord);
	}

}
