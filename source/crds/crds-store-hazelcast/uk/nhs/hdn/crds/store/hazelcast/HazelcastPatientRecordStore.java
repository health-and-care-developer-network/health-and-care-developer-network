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

package uk.nhs.hdn.crds.store.hazelcast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.crds.store.AbstractPatientRecordStore;
import uk.nhs.hdn.crds.store.domain.SimplePatientRecord;
import uk.nhs.hdn.crds.store.hazelcast.hazelcastSerialisationHolders.NhsNumberHazelcastSerialisationHolder;
import uk.nhs.hdn.crds.store.hazelcast.hazelcastSerialisationHolders.PatientRecordHazelcastSerialisationHolder;
import uk.nhs.hdn.number.NhsNumber;

import java.util.concurrent.ConcurrentMap;

public final class HazelcastPatientRecordStore extends AbstractPatientRecordStore<NhsNumberHazelcastSerialisationHolder, PatientRecordHazelcastSerialisationHolder>
{
	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public HazelcastPatientRecordStore(@NotNull final ConcurrentMap<NhsNumberHazelcastSerialisationHolder, PatientRecordHazelcastSerialisationHolder> root)
	{
		super(root);
	}

	@Nullable
	@Override
	protected SimplePatientRecord simplePatientRecordFor(@NotNull final PatientRecordHazelcastSerialisationHolder patientIdentifier)
	{
		return patientIdentifier.patientRecord();
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
