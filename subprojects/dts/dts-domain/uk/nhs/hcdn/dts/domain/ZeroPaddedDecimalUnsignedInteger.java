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

package uk.nhs.hcdn.dts.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hcdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hcdn.common.serialisers.ValueSerialisable;
import uk.nhs.hcdn.common.serialisers.ValueSerialiser;

import static java.lang.StrictMath.pow;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.common.StringHelper.padAsDecimal;

public final class ZeroPaddedDecimalUnsignedInteger extends AbstractToString implements ValueSerialisable
{
	private static final int MaximumWidth = 18;

	@NotNull
	private static final long[] MaximumValuesForWidths = maximumValuesForWidths();
	private static final long Utf8ZeroCharacter = 48L;
	private static final long Utf8NineCharacter = 57L;
	private static final long Ten = 10L;

	@SuppressWarnings("NumericCastThatLosesPrecision")
	private static long[] maximumValuesForWidths()
	{
		final long[] maximumValuesForWidths = new long[MaximumWidth];
		for(int index = 0; index < MaximumWidth; index++)
		{
			final int maximumWidth = index + 1;
			maximumValuesForWidths[index] = (long) pow((double) 10, (double) maximumWidth) - 1L; // 9, 99, 999, etc
		}
		return maximumValuesForWidths;
	}

	@NotNull
	public static ZeroPaddedDecimalUnsignedInteger fromPaddedValue(@NotNull final CharSequence paddedValue, final int width)
	{
		if (paddedValue.length() != width)
		{
			throw new IllegalArgumentException(format(ENGLISH, "paddedValue %1$s does not have width %2$s", paddedValue, width));
		}
		long value = 0L;
		for(int index = 0; index < width; index++)
		{
			final long digit = (long) paddedValue.charAt(index);
			if (digit < Utf8ZeroCharacter || digit > Utf8NineCharacter)
			{
				throw new IllegalArgumentException(format(ENGLISH, "paddedValue %1$s contains a digit not between 0 and 9", paddedValue));
			}
			final long digitValue = digit - Utf8ZeroCharacter;
			value = value * Ten + digitValue;
		}
		return new ZeroPaddedDecimalUnsignedInteger(width, value);
	}

	private final int width;
	private final long value;

	public ZeroPaddedDecimalUnsignedInteger(final int width, final long value)
	{
		if (width < 1)
		{
			throw new IllegalArgumentException(format(ENGLISH, "width must be positive, not %1$s", value));
		}
		if (width > MaximumWidth)
		{
			throw new IllegalArgumentException(format(ENGLISH, "width can not be more than %1$s, not %2$s", MaximumWidth, value));
		}
		if (value < 0L)
		{
			throw new IllegalArgumentException(format(ENGLISH, "value must be non-negative, not %1$s", value));
		}

		final long maximumValuesForWidth = MaximumValuesForWidths[width - 1];
		if (value > maximumValuesForWidth)
		{
			throw new IllegalArgumentException(format(ENGLISH, "value can not be more than %1$s, not %2$s", maximumValuesForWidth, value));
		}

		this.width = width;
		this.value = value;
	}

	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(paddedValue());
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}

	public int width()
	{
		return width;
	}

	public long value()
	{
		return value;
	}

	@NotNull
	public String paddedValue()
	{
		return padAsDecimal(value, width);
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

		final ZeroPaddedDecimalUnsignedInteger that = (ZeroPaddedDecimalUnsignedInteger) obj;

		if (width != that.width)
		{
			return false;
		}
		if (value != that.value)
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings("NumericCastThatLosesPrecision")
	@Override
	public int hashCode()
	{
		int result = width;
		result = 31 * result + (int) (value ^ (value >>> 32));
		return result;
	}

	public boolean doesNotHaveWidth(final int width)
	{
		return this.width != width;
	}

	@NotNull
	public ZeroPaddedDecimalUnsignedInteger next()
	{
		return new ZeroPaddedDecimalUnsignedInteger(width, value + 1L);
	}
}
