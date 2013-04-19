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

package uk.nhs.hdn.crds.store.server.hazelcast.hazelcastSerialisationHolders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.hazelcast.hazelcastSerialisationHolders.AbstractHazelcastSerialisationHolder;
import uk.nhs.hdn.crds.store.domain.*;
import uk.nhs.hdn.crds.store.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.store.domain.identifiers.RepositoryIdentifier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static uk.nhs.hdn.crds.store.server.hazelcast.hazelcastDataReaders.SimplePatientRecordHazelcastDataReader.SimplePatientRecordHazelcastDataReaderInstance;

@SuppressWarnings({"SerializableHasSerializationMethods", "serial"})
public final class PatientRecordHazelcastSerialisationHolder extends AbstractHazelcastSerialisationHolder implements PatientRecord<PatientRecordHazelcastSerialisationHolder>
{
	@Nullable private SimplePatientRecord patientRecord;

	public PatientRecordHazelcastSerialisationHolder()
	{
		patientRecord = null;
	}

	@SuppressWarnings("NullableProblems")
	public PatientRecordHazelcastSerialisationHolder(@NotNull final SimplePatientRecord patientRecord)
	{
		this.patientRecord = patientRecord;
	}

	@NotNull
	public SimplePatientRecord patientRecord()
	{
		assert patientRecord != null;
		return patientRecord;
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		assert patientRecord != null;
		patientRecord.writeData(out);
	}

	@Override
	public void readData(@NotNull final DataInput in) throws IOException
	{
		assert patientRecord == null;
		patientRecord = SimplePatientRecordHazelcastDataReaderInstance.readData(in);
	}

	@Override
	@NotNull
	public PatientRecordHazelcastSerialisationHolder addRepositoryEvent(@NotNull final ProviderIdentifier providerIdentifier, @NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final RepositoryEvent repositoryEvent)
	{
		assert patientRecord != null;
		patientRecord.addRepositoryEvent(providerIdentifier,repositoryIdentifier, repositoryEvent);
		return this;
	}
}
