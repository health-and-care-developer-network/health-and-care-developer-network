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
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffEventIdentifier;

import java.io.DataOutput;
import java.io.IOException;

public final class StuffEvent extends AbstractToString implements HazelcastDataWriter, MapSerialisable
{
	@NotNull
	public static HazelcastAwareLinkedHashSet<StuffEvent> initialStuffEvents(@NotNull final StuffEvent stuffEvent)
	{
		return new HazelcastAwareLinkedHashSet<>(stuffEvent);
	}

	@NotNull private final StuffEventIdentifier stuffEventIdentifier;
	@MillisecondsSince1970 private final long timestamp;
	@NotNull private final StuffEventKind stuffEventKind;

	public StuffEvent(@NotNull final StuffEventIdentifier stuffEventIdentifier, @MillisecondsSince1970 final long timestamp, @NotNull final StuffEventKind stuffEventKind)
	{
		this.stuffEventIdentifier = stuffEventIdentifier;
		this.timestamp = timestamp;
		this.stuffEventKind = stuffEventKind;
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		stuffEventIdentifier.writeData(out);
		out.writeLong(timestamp);
		out.writeByte(stuffEventKind.ordinal());
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty("repositoryEventIdentifier", stuffEventIdentifier);
			mapSerialiser.writeProperty("timestamp", timestamp);
			mapSerialiser.writeProperty("repositoryEventKind", stuffEventKind);
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

		final StuffEvent that = (StuffEvent) obj;

		if (timestamp != that.timestamp)
		{
			return false;
		}
		if (!stuffEventIdentifier.equals(that.stuffEventIdentifier))
		{
			return false;
		}
		if (stuffEventKind != that.stuffEventKind)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = stuffEventIdentifier.hashCode();
		result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
		result = 31 * result + stuffEventKind.hashCode();
		return result;
	}
}
