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
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashSet;
import uk.nhs.hdn.common.hazelcast.hazelcastDataWriters.HazelcastDataWriter;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseMapException;
import uk.nhs.hdn.common.serialisers.CouldNotWritePropertyException;
import uk.nhs.hdn.common.serialisers.MapSerialisable;
import uk.nhs.hdn.common.serialisers.MapSerialiser;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;

import java.io.DataOutput;
import java.io.IOException;

import static uk.nhs.hdn.crds.registry.domain.StuffEvent.initialStuffEvents;

public final class StuffRecord extends AbstractToString implements HazelcastDataWriter, MapSerialisable
{
	@NotNull
	public static HazelcastAwareLinkedHashMap<StuffIdentifier, StuffRecord> initialStuffRecords(@NotNull final StuffIdentifier repositoryIdentifier, @NotNull final StuffEvent stuffEvent)
	{
		return new HazelcastAwareLinkedHashMap<>(repositoryIdentifier, stuffRecord(repositoryIdentifier, stuffEvent));
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static StuffRecord stuffRecord(@NotNull final StuffIdentifier repositoryIdentifier, @NotNull final StuffEvent stuffEvent)
	{
		return new StuffRecord(repositoryIdentifier, initialStuffEvents(stuffEvent));
	}

	@NotNull private final StuffIdentifier stuffIdentifier;
	@NotNull private final HazelcastAwareLinkedHashSet<StuffEvent> stuffEvents;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public StuffRecord(@NotNull final StuffIdentifier stuffIdentifier, @NotNull final HazelcastAwareLinkedHashSet<StuffEvent> stuffEvents)
	{
		this.stuffIdentifier = stuffIdentifier;
		this.stuffEvents = stuffEvents;
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		stuffIdentifier.writeData(out);
		stuffEvents.writeData(out);
	}

	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty("stuffIdentifier", stuffIdentifier);
			mapSerialiser.writeProperty("stuffEvents", stuffEvents);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
	}

	@NotNull
	public StuffRecord addRepositoryEvent(@NotNull final StuffEvent stuffEvent)
	{
		return new StuffRecord(stuffIdentifier, new HazelcastAwareLinkedHashSet<>(stuffEvents, stuffEvent));
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

		final StuffRecord that = (StuffRecord) obj;

		if (!stuffEvents.equals(that.stuffEvents))
		{
			return false;
		}
		if (!stuffIdentifier.equals(that.stuffIdentifier))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = stuffIdentifier.hashCode();
		result = 31 * result + stuffEvents.hashCode();
		return result;
	}
}
