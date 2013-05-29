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
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashMap;
import uk.nhs.hdn.common.hazelcast.hazelcastDataWriters.HazelcastDataWriter;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseMapException;
import uk.nhs.hdn.common.serialisers.CouldNotWritePropertyException;
import uk.nhs.hdn.common.serialisers.MapSerialisable;
import uk.nhs.hdn.common.serialisers.MapSerialiser;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;

import java.io.DataOutput;
import java.io.IOException;

import static uk.nhs.hdn.crds.registry.domain.StuffRecord.initialStuffRecords;
import static uk.nhs.hdn.crds.registry.domain.StuffRecord.stuffRecord;

public final class RepositoryRecord extends AbstractToString implements HazelcastDataWriter, MapSerialisable
{
	@NotNull
	public static HazelcastAwareLinkedHashMap<RepositoryIdentifier, RepositoryRecord> initialRepositoryRecords(@NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final StuffIdentifier stuffIdentifier, @NotNull final StuffEvent stuffEvent)
	{
		return new HazelcastAwareLinkedHashMap<>(repositoryIdentifier, repositoryRecord(repositoryIdentifier, stuffIdentifier, stuffEvent));
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static RepositoryRecord repositoryRecord(@NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final StuffIdentifier stuffIdentifier, @NotNull final StuffEvent stuffEvent)
	{
		return new RepositoryRecord(repositoryIdentifier, initialStuffRecords(stuffIdentifier, stuffEvent));
	}

	@NotNull public final RepositoryIdentifier repositoryIdentifier;
	@NotNull public final HazelcastAwareLinkedHashMap<StuffIdentifier, StuffRecord> stuffRecords;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public RepositoryRecord(@NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final HazelcastAwareLinkedHashMap<StuffIdentifier, StuffRecord> stuffRecords)
	{
		this.repositoryIdentifier = repositoryIdentifier;
		this.stuffRecords = stuffRecords;
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		repositoryIdentifier.writeData(out);
		stuffRecords.writeData(out);
	}

	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty("repositoryIdentifier", repositoryIdentifier);
			mapSerialiser.writeProperty("stuffRecords", stuffRecords);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
	}

	@NotNull
	public RepositoryRecord addRepositoryEvent(@NotNull final StuffIdentifier stuffIdentifier, @NotNull final StuffEvent stuffEvent)
	{
		@Nullable final StuffRecord existingStuffRecord = stuffRecords.get(stuffIdentifier);
		final StuffRecord stuffRecord;
		if (existingStuffRecord == null)
		{
			stuffRecord = stuffRecord(stuffIdentifier, stuffEvent);
		}
		else
		{
			stuffRecord = existingStuffRecord.addRepositoryEvent(stuffEvent);
		}
		final HazelcastAwareLinkedHashMap<StuffIdentifier, StuffRecord> replacementStuffRecords = new HazelcastAwareLinkedHashMap<>(stuffRecords, stuffIdentifier, stuffRecord);
		return new RepositoryRecord(repositoryIdentifier, replacementStuffRecords);
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

		final RepositoryRecord that = (RepositoryRecord) obj;

		if (!stuffRecords.equals(that.stuffRecords))
		{
			return false;
		}
		if (!repositoryIdentifier.equals(that.repositoryIdentifier))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = repositoryIdentifier.hashCode();
		result = 31 * result + stuffRecords.hashCode();
		return result;
	}
}
