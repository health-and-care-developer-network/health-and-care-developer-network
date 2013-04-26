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

package uk.nhs.hdn.crds.registry.server.hazelcast.hazelcastSerialisationHolders;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.hazelcast.hazelcastSerialisationHolders.AbstractHazelcastSerialisationHolder;
import uk.nhs.hdn.crds.registry.domain.PatientRecord;
import uk.nhs.hdn.crds.registry.domain.SimplePatientRecord;
import uk.nhs.hdn.crds.registry.domain.StuffEvent;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static uk.nhs.hdn.crds.registry.server.hazelcast.hazelcastDataReaders.SimplePatientRecordHazelcastDataReader.SimplePatientRecordHazelcastDataReaderInstance;

@SuppressWarnings({"SerializableHasSerializationMethods", "serial"})
public final class PatientRecordHazelcastSerialisationHolder extends AbstractHazelcastSerialisationHolder<SimplePatientRecord> implements PatientRecord<PatientRecordHazelcastSerialisationHolder>
{
	public PatientRecordHazelcastSerialisationHolder()
	{
	}

	@SuppressWarnings("NullableProblems")
	public PatientRecordHazelcastSerialisationHolder(@NotNull final SimplePatientRecord simplePatientRecord)
	{
		super(simplePatientRecord);
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		assert heldValue != null;
		heldValue.writeData(out);
	}

	@Override
	public void readData(@NotNull final DataInput in) throws IOException
	{
		assert heldValue == null;
		heldValue = SimplePatientRecordHazelcastDataReaderInstance.readData(in);
	}

	@Override
	@NotNull
	public PatientRecordHazelcastSerialisationHolder addRepositoryEvent(@NotNull final ProviderIdentifier providerIdentifier, @NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final StuffIdentifier stuffIdentifier, @NotNull final StuffEvent stuffEvent)
	{
		assert heldValue != null;
		heldValue.addRepositoryEvent(providerIdentifier, repositoryIdentifier, stuffIdentifier, stuffEvent);
		return this;
	}
}
