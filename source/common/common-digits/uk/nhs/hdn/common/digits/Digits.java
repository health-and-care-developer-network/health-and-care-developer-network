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

package uk.nhs.hdn.common.digits;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.comparison.ComparisonResult;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;
import uk.nhs.hdn.common.serialisers.ValueSerialiser;

import java.util.Arrays;

import static java.lang.System.arraycopy;
import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;
import static uk.nhs.hdn.common.comparison.ComparisonHelper.compareInt;
import static uk.nhs.hdn.common.comparison.ComparisonHelper.isNotEqualTo;
import static uk.nhs.hdn.common.digits.Digit.digit;
import static uk.nhs.hdn.common.digits.Digit.digitFromUtf16Code;

@SuppressWarnings("ClassWithTooManyMethods")
public final class Digits implements DigitList, ValueSerialisable
{
	private static final Digit[] EmptyDigits = new Digit[0];

	@NotNull public static final Digits Empty = new Digits(false, EmptyDigits);

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	public static Digits digits(@NotNull final int... digits)
	{
		return new Digits(digits);
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	public static Digits digits(@NotNull final CharSequence digits)
	{
		return new Digits(digits);
	}

	@NotNull
	private final Digit[] digits;
	private final int size;

	public Digits(@NotNull final CharSequence digits)
	{
		size = digits.length();
		if (size == 0)
		{
			this.digits = EmptyDigits;
			return;
		}
		this.digits = new Digit[size];
		for (int index = 0; index < size; index++)
		{
			this.digits[index] = digitFromUtf16Code(digits.charAt(index));
		}
	}

	public Digits(@NotNull final int... digits)
	{
		size = digits.length;
		if (size == 0)
		{
			this.digits = EmptyDigits;
			return;
		}
		this.digits = new Digit[size];
		for (int index = 0; index < size; index++)
		{
			this.digits[index] = digit(digits[index]);
		}
	}

	public Digits(final boolean makeDefensiveCopy, @NotNull final Digit... digits)
	{
		size = digits.length;
		if (size == 0)
		{
			this.digits = EmptyDigits;
			return;
		}
		this.digits = makeDefensiveCopy ? copyOf(digits) : digits;
	}

	private Digits(@NotNull final Digit[] copyFromDigits, final int slice)
	{
		final int length = copyFromDigits.length;
		digits = slice(copyFromDigits, slice, length);
		size = length;
	}

	private static Digit[] slice(final Digit[] copyFromDigits, final int slice, final int length)
	{
		if (slice > length)
		{
			throw new IllegalArgumentException("slice can not be greater than copyFromDigits.length");
		}
		if (slice == 0)
		{
			return EmptyDigits;
		}
		if (slice == length)
		{
			return copyFromDigits;
		}
		return Arrays.copyOf(copyFromDigits, slice);
	}

	@NotNull
	public static Digits slice(@NotNull final DigitList digitList, final int startsFromInclusiveOneBasedPositionT, final int endsAtEnclusiveOneBasedPositionT)
	{
		final Digit[] slice = new Digit[endsAtEnclusiveOneBasedPositionT - startsFromInclusiveOneBasedPositionT + 1];
		for (int oneBasedPositionT = startsFromInclusiveOneBasedPositionT; oneBasedPositionT <= endsAtEnclusiveOneBasedPositionT; oneBasedPositionT++)
		{
			slice[oneBasedPositionT - startsFromInclusiveOneBasedPositionT] = digitList.digitAtPositionT(oneBasedPositionT);
		}
		return new Digits(false, slice);
	}

	@NotNull
	public Digits firstThree()
	{
		if (size < 3)
		{
			throw new IllegalStateException("Less than three digits");
		}
		return new Digits(digits, 3);
	}

	@Override
	@NotNull
	public String toString()
	{
		final char[] protoString = new char[size];
		for(int index = 0; index < size; index++)
		{
			protoString[index] = digits[index].toChar();
		}
		return new String(protoString);
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

		final Digits that = (Digits) obj;

		return Arrays.equals(digits, that.digits);
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(digits);
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	@ComparisonResult
	public int compareTo(@NotNull final DigitList o)
	{
		@ComparisonResult final int sizeComparisonResult = compareInt(size, o.size());
		if (isNotEqualTo(sizeComparisonResult))
		{
			return sizeComparisonResult;
		}
		for (int index = 0; index < size; index++)
		{
			@ComparisonResult final int digitComparisonResult = compareDigitsAtIndex(o, index);
			if (isNotEqualTo(digitComparisonResult))
			{
				return digitComparisonResult;
			}
		}
		return ComparisonResult.EqualTo;
	}

	public boolean hasSize(final int size)
	{
		return this.size == size;
	}

	public boolean hasSizeBetween(final int lowerBoundInclusive, final int upperBoundInclusive)
	{
		return size >= lowerBoundInclusive && size <= upperBoundInclusive;
	}

	@Override
	@NotNull
	public Digit digitAt(final int index)
	{
		if (index < 0 || index >= size)
		{
			throw new IllegalArgumentException("index is out of range");
		}
		return digits[index];
	}

	@NotNull
	@Override
	public Digit digitAtPositionT(final int oneBasedPositionT)
	{
		return digitAt(oneBasedPositionT - 1);
	}

	@NotNull
	public Digits slice(final int lowerInclusiveOneBasedPositionT)
	{
		return slice(lowerInclusiveOneBasedPositionT, size - lowerInclusiveOneBasedPositionT);
	}

	@NotNull
	public Digits slice(final int lowerInclusiveOneBasedPositionT, final int upperExclusiveOneBasedPositionT)
	{
		final int length = upperExclusiveOneBasedPositionT - lowerInclusiveOneBasedPositionT;
		if (length == 0)
		{
			return Empty;
		}
		final int lowerInclusiveIndex = lowerInclusiveOneBasedPositionT - 1;
		final Digit[] slice = new Digit[length];
		arraycopy(digits, lowerInclusiveIndex, slice, 0, length);
		return new Digits(false, slice);
	}

	@Override
	public int size()
	{
		return size;
	}

	@ComparisonResult
	public int compareDigitsAtIndex(@NotNull final DigitList digits, final int index)
	{
		return compareDigitAtIndexWith(index, digits.digitAt(index));
	}

	@ComparisonResult
	public int compareDigitAtIndexWith(final int index, @NotNull final Digit digit)
	{
		return digitAt(index).compareTo(digit);
	}

	@NotNull
	public Digits add(@NotNull final Digits digits)
	{
		final int theirSize = digits.size;
		if (theirSize == 0)
		{
			return this;
		}
		if (size == 0)
		{
			return digits;
		}
		final Digit[] copy = new Digit[size + theirSize];
		arraycopy(this.digits, 0, copy, 0, size);
		arraycopy(digits.digits, 0, copy, size, theirSize);
		return new Digits(false, copy);
	}

	@NotNull
	public Digits add(@NotNull final Digit digit)
	{
		if (size == 0)
		{
			return new Digits(false, digit);
		}
		final Digit[] copy = Arrays.copyOf(digits, size + 1);
		copy[size] = digit;
		return new Digits(false, copy);
	}

	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(digits);
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}

	public int to0To999()
	{
		return scaleDigit(0, 100) + scaleDigit(1, 10) + scaleDigit(2, 1);
	}

	private int scaleDigit(final int index, final int scalar)
	{
		return digitAt(index).digit() * scalar;
	}
}
