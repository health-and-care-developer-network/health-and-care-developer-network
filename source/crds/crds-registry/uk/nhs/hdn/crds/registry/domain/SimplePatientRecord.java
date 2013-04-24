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

package uk.nhs.hdn.crds.registry.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashMap;
import uk.nhs.hdn.common.hazelcast.hazelcastDataWriters.DigitsHazelcastDataObjectWriter;
import uk.nhs.hdn.common.hazelcast.hazelcastDataWriters.HazelcastDataWriter;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseMapException;
import uk.nhs.hdn.common.serialisers.CouldNotWritePropertyException;
import uk.nhs.hdn.common.serialisers.MapSerialisable;
import uk.nhs.hdn.common.serialisers.MapSerialiser;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.number.NhsNumber;

import java.io.DataOutput;
import java.io.IOException;

import static java.lang.System.currentTimeMillis;
import static uk.nhs.hdn.crds.registry.domain.ProviderRecord.initialProviderRecords;
import static uk.nhs.hdn.crds.registry.domain.ProviderRecord.providerRecord;

public final class SimplePatientRecord extends AbstractToString implements PatientRecord<SimplePatientRecord>, HazelcastDataWriter, MapSerialisable
{
	@NotNull
	public static SimplePatientRecord initialPatientRecord(@NotNull final NhsNumber patientIdentifier, @NotNull final ProviderIdentifier providerIdentifier, @NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final RepositoryEvent repositoryEvent)
	{
		return new SimplePatientRecord(patientIdentifier, currentTimeMillis(), initialProviderRecords(providerIdentifier, repositoryIdentifier, repositoryEvent));
	}

	@NotNull private final NhsNumber patientIdentifier;
	@MillisecondsSince1970 private final long lastModified;
	@NotNull private final HazelcastAwareLinkedHashMap<ProviderIdentifier, ProviderRecord> knownProviders;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public SimplePatientRecord(@NotNull final NhsNumber patientIdentifier, @MillisecondsSince1970 final long lastModified, @NotNull final HazelcastAwareLinkedHashMap<ProviderIdentifier, ProviderRecord> knownProviders)
	{
		this.patientIdentifier = patientIdentifier;
		this.lastModified = lastModified;
		this.knownProviders = knownProviders;
	}

	@Override
	@SuppressWarnings("FeatureEnvy")
	@NotNull
	public SimplePatientRecord addRepositoryEvent(@NotNull final ProviderIdentifier providerIdentifier, @NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final RepositoryEvent repositoryEvent)
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
		final HazelcastAwareLinkedHashMap<ProviderIdentifier, ProviderRecord> replacementKnownProviders = new HazelcastAwareLinkedHashMap<>(knownProviders, providerIdentifier, providerRecord);
		return new SimplePatientRecord(patientIdentifier, currentTimeMillis(), replacementKnownProviders);
	}

	@MillisecondsSince1970
	public long lastModified()
	{
		return lastModified;
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		DigitsHazelcastDataObjectWriter.writeData(out, patientIdentifier);
		out.writeLong(lastModified);
		knownProviders.writeData(out);
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty("patientIdentifier", patientIdentifier);
			mapSerialiser.writeProperty("lastModified", lastModified);
			mapSerialiser.writeProperty("knownProviders", knownProviders);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
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

		final SimplePatientRecord that = (SimplePatientRecord) obj;

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
