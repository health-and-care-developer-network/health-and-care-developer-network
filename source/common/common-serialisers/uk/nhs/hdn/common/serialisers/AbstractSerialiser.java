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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static uk.nhs.hdn.common.serialisers.ValueSerialisable.NullNumber;

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

	public static void writeNullableProperty(@NotNull final MapSerialiser mapSerialiser, @FieldTokenName @NotNull @NonNls final String field, @Nullable final String value) throws CouldNotWritePropertyException
	{
		if (value == null)
		{
			mapSerialiser.writePropertyNull(field);
		}
		else
		{
			mapSerialiser.writeProperty(field, value);
		}
	}

	public static void writeNullableProperty(@NotNull final MapSerialiser mapSerialiser, @NotNull @NonNls @FieldTokenName final String name, final long valueOrMinusOne) throws CouldNotWritePropertyException
	{
		if (valueOrMinusOne == NullNumber)
		{
			mapSerialiser.writePropertyNull(name);
		}
		else
		{
			mapSerialiser.writeProperty(name, valueOrMinusOne);
		}
	}

	public static void writeNullableProperty(@NotNull final MapSerialiser mapSerialiser, @NotNull @NonNls @FieldTokenName final String name, @Nullable final Boolean value) throws CouldNotWritePropertyException
	{
		if (value == null)
		{
			mapSerialiser.writePropertyNull(name);
		}
		else
		{
			mapSerialiser.writeProperty(name, (boolean) value);
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
	public final void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final String value) throws CouldNotWritePropertyException
	{
		writeProperty(name, value, false);
	}

	@Override
	public final void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final MapSerialisable value) throws CouldNotWritePropertyException
	{
		writeProperty(name, value, false);
	}

	@Override
	public final void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final ValueSerialisable value) throws CouldNotWritePropertyException
	{
		writeProperty(name, value, false);
	}

	@Override
	public final void writeProperty(@FieldTokenName @NonNls @NotNull final String name, final int value) throws CouldNotWritePropertyException
	{
		writeProperty(name, value, false);
	}

	@Override
	public final void writeProperty(@FieldTokenName @NonNls @NotNull final String name, final long value) throws CouldNotWritePropertyException
	{
		writeProperty(name, value, false);
	}

	@Override
	public final void writePropertyNull(@FieldTokenName @NonNls @NotNull final String name) throws CouldNotWritePropertyException
	{
		writePropertyNull(name, false);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public final <S extends MapSerialisable> void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final S[] values) throws CouldNotWritePropertyException
	{
		writeProperty(name, values, false);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public final <S extends ValueSerialisable> void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final S[] values) throws CouldNotWritePropertyException
	{
		writeProperty(name, values, false);
	}

	@Override
	public final void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final List<?> values) throws CouldNotWritePropertyException
	{
		writeProperty(name, values, false);
	}

	@Override
	public final void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final Set<?> values) throws CouldNotWritePropertyException
	{
		writeProperty(name, values, false);
	}

	@Override
	public final void writeProperty(@FieldTokenName @NonNls @NotNull final String name, final boolean value) throws CouldNotWritePropertyException
	{
		writeProperty(name, value, false);
	}

	@Override
	public final void writeProperty(@FieldTokenName @NonNls @NotNull final String name, final boolean value, final boolean isMapEntry) throws CouldNotWritePropertyException
	{
		writeProperty(name, convertBooleanToString(value), isMapEntry);
	}

	@Override
	public final void writeProperty(@NonNls @NotNull final String name, @Nullable final Object value) throws CouldNotWritePropertyException
	{
		writeProperty(name, value, false);
	}

	@SuppressWarnings("MethodWithMultipleReturnPoints")
	@Override
	public final void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @Nullable final Object value, final boolean isMapEntry) throws CouldNotWritePropertyException
	{
		if (value == null)
		{
			writePropertyNull(name, isMapEntry);
			return;
		}

		if (value instanceof MapSerialisable)
		{
			writeProperty(name, (MapSerialisable) value, isMapEntry);
			return;
		}

		if (value instanceof ValueSerialisable)
		{
			writeProperty(name, (ValueSerialisable) value, isMapEntry);
			return;
		}

		if (value instanceof MapSerialisable[])
		{
			writeProperty(name, (MapSerialisable[]) value, isMapEntry);
			return;
		}

		if (value instanceof ValueSerialisable[])
		{
			writeProperty(name, (ValueSerialisable[]) value, isMapEntry);
			return;
		}

		if (value instanceof Integer)
		{
			writeProperty(name, (int) value, isMapEntry);
			return;
		}

		if (value instanceof Long)
		{
			writeProperty(name, (long) value, isMapEntry);
			return;
		}

		if (value instanceof String)
		{
			writeProperty(name, (String) value, isMapEntry);
			return;
		}

		if (value instanceof Boolean)
		{
			writeProperty(name, (boolean) value, isMapEntry);
			return;
		}

		if (value instanceof UUID)
		{
			writeProperty(name, (UUID) value, isMapEntry);
			return;
		}

		if (value instanceof Map)
		{
			writeProperty(name, new GenericMapSerialisable((Map<?, ?>) value), isMapEntry);
			return;
		}

		if (value instanceof List)
		{
			writeProperty(name, (List<?>) value, isMapEntry);
			return;
		}

		if (value instanceof Set)
		{
			writeProperty(name, (Set<?>) value, isMapEntry);
			return;
		}

		throw new CouldNotWritePropertyException(name, value, "do not know how to write properties for this class");
	}

	@Override
	public final void writeValue(@Nullable final Object value) throws CouldNotWriteValueException
	{
		if (value == null)
		{
			writeValueNull();
			return;
		}

		if (value instanceof Serialisable)
		{
			writeValue((Serialisable) value);
			return;
		}

		if (value instanceof Serialisable[])
		{
			writeValue((Serialisable[]) value);
			return;
		}

		super.writeValue(value);
	}

	@Override
	public final <S extends Serialisable> void writeValue(@NotNull final S value) throws CouldNotWriteValueException
	{
		try
		{
			value.serialise(this);
		}
		catch (CouldNotSerialiseException e)
		{
			throw new CouldNotWriteValueException(value, e);
		}
	}
}
