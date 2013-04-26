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

package uk.nhs.hdn.crds.registry.domain.metadata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.hazelcast.hazelcastDataWriters.HazelcastDataWriter;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.crds.registry.domain.identifiers.Identifier;

import java.io.DataOutput;
import java.io.IOException;

public abstract class AbstractMetadataRecord<I extends Identifier> extends AbstractToString implements HazelcastDataWriter, Serialisable, MapSerialisable
{
	@SuppressWarnings({"PublicField", "ClassEscapesDefinedScope"}) @NotNull public final I identifier;
	@MillisecondsSince1970 public final long lastModified;

	protected AbstractMetadataRecord(@NotNull final I identifier, @MillisecondsSince1970 final long lastModified)
	{
		this.identifier = identifier;
		this.lastModified = lastModified;
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		identifier.writeData(out);
		out.writeLong(lastModified);
	}

	@Override
	public void serialise(@NotNull final Serialiser serialiser) throws CouldNotSerialiseException
	{
		try
		{
			serialiseMap(serialiser);
		}
		catch (CouldNotSerialiseMapException e)
		{
			throw new CouldNotSerialiseException(this, e);
		}
	}

	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty("identifier", identifier);
			mapSerialiser.writeProperty("lastModified", lastModified);
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

		final AbstractMetadataRecord<?> that = (AbstractMetadataRecord<?>) obj;

		if (lastModified != that.lastModified)
		{
			return false;
		}
		if (!identifier.equals(that.identifier))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = identifier.hashCode();
		result = 31 * result + (int) (lastModified ^ (lastModified >>> 32));
		return result;
	}
}
