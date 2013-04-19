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

package uk.nhs.hdn.crds.store.domain.identifiers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.ValueSerialiser;

import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

public abstract class AbstractIdentifier extends AbstractToString implements Identifier
{
	@NotNull private final UUID identifier;

	protected AbstractIdentifier(@NotNull final UUID identifier)
	{
		this.identifier = identifier;
	}

	@NotNull
	public final String toUuidString()
	{
		return identifier.toString();
	}

	@Override
	public final void writeData(@NotNull final DataOutput out) throws IOException
	{
		out.writeLong(identifier.getMostSignificantBits());
		out.writeLong(identifier.getLeastSignificantBits());
	}

	@Override
	public final void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(identifier);
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}

	@Override
	public final boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final AbstractIdentifier that = (AbstractIdentifier) obj;

		if (!identifier.equals(that.identifier))
		{
			return false;
		}

		return true;
	}

	@Override
	public final int hashCode()
	{
		return identifier.hashCode();
	}
}
