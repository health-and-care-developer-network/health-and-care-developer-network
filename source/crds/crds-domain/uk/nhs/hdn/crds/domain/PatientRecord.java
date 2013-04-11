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

package uk.nhs.hdn.crds.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.crds.domain.hazelcast.ConvenientLinkedHashMap;
import uk.nhs.hdn.crds.domain.hazelcast.DataWriter;
import uk.nhs.hdn.number.NhsNumber;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static uk.nhs.hdn.crds.domain.ProviderRecord.initialProviderRecords;
import static uk.nhs.hdn.crds.domain.ProviderRecord.providerRecord;
import static uk.nhs.hdn.crds.domain.ProviderRecordDataReader.ProviderRecordDataReaderInstance;
import static uk.nhs.hdn.crds.domain.hazelcast.ProviderIdentifierDataReader.ProviderIdentifierDataReaderInstance;

public final class PatientRecord extends AbstractToString implements DataWriter
{
	@NotNull
	public static PatientRecord initialPatientRecord(@NotNull final NhsNumber patientIdentifier, @NotNull final ProviderIdentifier providerIdentifier, @NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final RepositoryEvent repositoryEvent)
	{
		return new PatientRecord(patientIdentifier, initialProviderRecords(providerIdentifier, repositoryIdentifier, repositoryEvent));
	}

	@NotNull private final NhsNumber patientIdentifier;
	@NotNull private final ConvenientLinkedHashMap<ProviderIdentifier, ProviderRecord> knownProviders;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public PatientRecord(@NotNull final NhsNumber patientIdentifier, @NotNull final ConvenientLinkedHashMap<ProviderIdentifier, ProviderRecord> knownProviders)
	{
		this.patientIdentifier = patientIdentifier;
		this.knownProviders = knownProviders;
	}

	@SuppressWarnings("FeatureEnvy")
	@NotNull
	public PatientRecord addRepositoryEvent(@NotNull final ProviderIdentifier providerIdentifier, @NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final RepositoryEvent repositoryEvent)
	{
		@Nullable final ProviderRecord existingProviderRecord = knownProviders.get(providerIdentifier);
		final ProviderRecord providerRecord;
		if (existingProviderRecord == null)
		{
			providerRecord = providerRecord(providerIdentifier, repositoryIdentifier, repositoryEvent);
		}
		else
		{
			providerRecord = existingProviderRecord.addRepositoryEvent(repositoryIdentifier, repositoryEvent);
		}
		final ConvenientLinkedHashMap<ProviderIdentifier, ProviderRecord> replacementKnownProviders = new ConvenientLinkedHashMap<>(knownProviders, providerIdentifier, providerRecord);
		return new PatientRecord(patientIdentifier, replacementKnownProviders);
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		new NhsNumberHazelcastSerialisationHolder(patientIdentifier).writeData(out);
		knownProviders.writeData(out);
	}

	@NotNull
	public static PatientRecord readData(@NotNull final DataInput in) throws IOException
	{
		final NhsNumberHazelcastSerialisationHolder nhsNumberHazelcastSerialisationHolder = new NhsNumberHazelcastSerialisationHolder();
		nhsNumberHazelcastSerialisationHolder.readData(in);
		final NhsNumber nhsNumber = nhsNumberHazelcastSerialisationHolder.nhsNumber();

		return new PatientRecord(nhsNumber, ConvenientLinkedHashMap.readData(in, ProviderIdentifierDataReaderInstance, ProviderRecordDataReaderInstance));
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final PatientRecord that = (PatientRecord) obj;

		if (!knownProviders.equals(that.knownProviders))
		{
			return false;
		}
		if (!patientIdentifier.equals(that.patientIdentifier))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = patientIdentifier.hashCode();
		result = 31 * result + knownProviders.hashCode();
		return result;
	}

}
