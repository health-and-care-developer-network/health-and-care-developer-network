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
import uk.nhs.hdn.common.hazelcast.hazelcastDataWriters.HazelcastDataWriter;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashSet;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseMapException;
import uk.nhs.hdn.common.serialisers.CouldNotWritePropertyException;
import uk.nhs.hdn.common.serialisers.MapSerialisable;
import uk.nhs.hdn.common.serialisers.MapSerialiser;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryEventIdentifier;

import java.io.DataOutput;
import java.io.IOException;

public final class RepositoryEvent extends AbstractToString implements HazelcastDataWriter, MapSerialisable
{
	@NotNull
	public static HazelcastAwareLinkedHashSet<RepositoryEvent> initialRepositoryEvents(@NotNull final RepositoryEvent repositoryEvent)
	{
		return new HazelcastAwareLinkedHashSet<>(repositoryEvent);
	}

	@NotNull private final RepositoryEventIdentifier repositoryEventIdentifier;
	@MillisecondsSince1970 private final long timestamp;
	@NotNull private final RepositoryEventKind repositoryEventKind;

	public RepositoryEvent(@NotNull final RepositoryEventIdentifier repositoryEventIdentifier, @MillisecondsSince1970 final long timestamp, @NotNull final RepositoryEventKind repositoryEventKind)
	{
		this.repositoryEventIdentifier = repositoryEventIdentifier;
		this.timestamp = timestamp;
		this.repositoryEventKind = repositoryEventKind;
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		repositoryEventIdentifier.writeData(out);
		out.writeLong(timestamp);
		out.writeByte(repositoryEventKind.ordinal());
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty("repositoryEventIdentifier", repositoryEventIdentifier);
			mapSerialiser.writeProperty("timestamp", timestamp);
			mapSerialiser.writeProperty("repositoryEventKind", repositoryEventKind);
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

		final RepositoryEvent that = (RepositoryEvent) obj;

		if (timestamp != that.timestamp)
		{
			return false;
		}
		if (!repositoryEventIdentifier.equals(that.repositoryEventIdentifier))
		{
			return false;
		}
		if (repositoryEventKind != that.repositoryEventKind)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = repositoryEventIdentifier.hashCode();
		result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
		result = 31 * result + repositoryEventKind.hashCode();
		return result;
	}
}
