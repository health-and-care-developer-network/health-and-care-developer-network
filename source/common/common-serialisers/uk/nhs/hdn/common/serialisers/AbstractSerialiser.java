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

package uk.nhs.hdn.common.serialisers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.unknown.IsUnknown;

public abstract class AbstractSerialiser extends AbstractValueSerialiser implements Serialiser
{
	public static <V extends ValueSerialisable & IsUnknown> void writePropertyIfKnown(@NotNull final MapSerialiser mapSerialiser,
    @FieldTokenName @NotNull @NonNls final String name, @NotNull final V value) throws CouldNotWritePropertyException
	{
		if (value.isKnown())
		{
			mapSerialiser.writeProperty(name, value);
		}
	}

	public static <V extends MapSerialisable & IsUnknown> void writePropertyIfKnown(@NotNull final MapSerialiser mapSerialiser, @NonNls @NotNull @FieldTokenName final String name, @NotNull final V value) throws CouldNotWritePropertyException
	{
		if (value.isKnown())
		{
			mapSerialiser.writeProperty(name, value);
		}
	}

	public static void writeNullableProperty(@NotNull final MapSerialiser mapSerialiser, @NotNull @NonNls @FieldTokenName final String name, @Nullable final ValueSerialisable valueSerialisable) throws CouldNotWritePropertyException
	{
		if (valueSerialisable == null)
		{
			return;
		}
		mapSerialiser.writeProperty(name, valueSerialisable);
	}

	@Override
	public final void writeProperty(@FieldTokenName @NonNls @NotNull final String name, final boolean value) throws CouldNotWritePropertyException
	{
		writeProperty(name, convertBooleanToString(value));
	}

	@SuppressWarnings("MethodWithMultipleReturnPoints")
	@Override
	public final void writeProperty(@NonNls @NotNull final String name, @Nullable final Object value) throws CouldNotWritePropertyException
	{
		if (value == null)
		{
			writePropertyNull(name);
			return;
		}

		if (value instanceof MapSerialisable)
		{
			writeProperty(name, (MapSerialisable) value);
			return;
		}

		if (value instanceof MapSerialisable[])
		{
			writeProperty(name, (MapSerialisable[]) value);
			return;
		}

		if (value instanceof ValueSerialisable)
		{
			writeProperty(name, (ValueSerialisable) value);
			return;
		}

		if (value instanceof ValueSerialisable[])
		{
			writeProperty(name, (ValueSerialisable[]) value);
			return;
		}

		if (value instanceof Integer)
		{
			writeProperty(name, (int) value);
			return;
		}

		if (value instanceof Long)
		{
			writeProperty(name, (long) value);
			return;
		}

		if (value instanceof String)
		{
			writeProperty(name, (String) value);
			return;
		}

		if (value instanceof Boolean)
		{
			writeProperty(name, (boolean) value);
			return;
		}

		throw new CouldNotWritePropertyException(name, value, "do not know how to write properties for this class");
	}

	@Override
	public void writeValue(@Nullable final Object value) throws CouldNotWriteValueException
	{
		if (value == null)
		{
			writeValueNull();
			return;
		}

		if (value instanceof Serialisable)
		{
			try
			{
				((Serialisable) value).serialise(this);
			}
			catch (CouldNotSerialiseException e)
			{
				throw new CouldNotWriteValueException(value, e);
			}
			return;
		}

		super.writeValue(value);
	}
}
