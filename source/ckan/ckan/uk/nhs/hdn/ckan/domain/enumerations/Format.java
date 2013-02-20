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

package uk.nhs.hdn.ckan.domain.enumerations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;
import uk.nhs.hdn.common.serialisers.ValueSerialiser;
import uk.nhs.hdn.common.unknown.IsUnknown;

public enum Format implements ValueSerialisable, IsUnknown
{
	CSV,
	XLS,
	RDF,
	Unknown,
	;


	@SuppressWarnings("ConditionalExpression")
	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(this == Unknown ? name() : "");
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}

	@Nullable
	public static Format format(@NotNull final String value)
	{
		if (value.isEmpty())
		{
			return Unknown;
		}
		return valueOf(value);
	}

	@Override
	public boolean isUnknown()
	{
		return this == Unknown;
	}

	@Override
	public boolean isKnown()
	{
		return this != Unknown;
	}

	@Override
	public boolean isSameKnownessAs(@NotNull final IsUnknown that)
	{
		if (isKnown())
		{
			if (that.isKnown())
			{
				return true;
			}
			return false;
		}
		return that.isUnknown();
	}

	@Override
	public boolean isDifferentKnownessAs(@NotNull final IsUnknown that)
	{
		return !isSameKnownessAs(that);
	}
}
