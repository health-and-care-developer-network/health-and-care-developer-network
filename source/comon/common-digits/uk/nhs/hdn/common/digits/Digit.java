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
import uk.nhs.hdn.common.comparison.ComparisonHelper;
import uk.nhs.hdn.common.comparison.ExtendedComparable;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;
import uk.nhs.hdn.common.serialisers.ValueSerialiser;

public enum Digit implements ExtendedComparable<Digit>, ValueSerialisable
{
	Zero(0)
	{
		@SuppressWarnings("RefusedBequest")
		@Override
		public boolean isZeroToOne()
		{
			return true;
		}

		@SuppressWarnings("RefusedBequest")
		@Override
		public boolean isZeroToTwo()
		{
			return true;
		}
	},
	One(1)
	{
		@SuppressWarnings("RefusedBequest")
		@Override
		public boolean isZeroToOne()
		{
			return true;
		}

		@SuppressWarnings("RefusedBequest")
		@Override
		public boolean isZeroToTwo()
		{
			return true;
		}
	},
	Two(2)
	{
		@SuppressWarnings("RefusedBequest")
		@Override
		public boolean isZeroToTwo()
		{
			return true;
		}
	},
	Three(3),
	Four(4),
	Five(5),
	Six(6),
	Seven(7),
	Eight(8),
	Nine(9),
	;

	private static final int Utf16CodeForZero = 48;
	private final char asCharacter;
	private final String asString;

	@SuppressWarnings({"ClassWithoutConstructor", "UtilityClassWithoutPrivateConstructor"})
	private static final class CompilerWorkaround
	{
		private static final Digit[] Index = new Digit[10];
	}

	private final int digit;

	@SuppressWarnings("NumericCastThatLosesPrecision")
	Digit(final int digit)
	{
		this.digit = digit;
		CompilerWorkaround.Index[digit] = this;
		asCharacter = (char) (Utf16CodeForZero + this.digit);
		asString = Integer.toString(this.digit);
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	public int digit()
	{
		return digit;
	}

	public char toChar()
	{
		return asCharacter;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	@NotNull
	public String toString()
	{
		return asString;
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static Digit digit(final int zeroBased)
	{
		if (zeroBased < 0 || zeroBased > 9)
		{
			throw new IllegalArgumentException("digit must be between 0 and 9");
		}
		return CompilerWorkaround.Index[zeroBased];
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static Digit digit(final long zeroBased)
	{
		//noinspection NumericCastThatLosesPrecision
		return digit((int) zeroBased);
	}

	@NotNull
	public static Digit digitFromUtf16Code(final char utf16Code)
	{
		return digit((int) utf16Code - Utf16CodeForZero);
	}

	@NotNull
	public static Digit digitFromCodepoint(final int unicodeCodePoint)
	{
		return digit(unicodeCodePoint - Utf16CodeForZero);
	}

	public boolean isZeroToOne()
	{
		return true;
	}

	public boolean isZeroToTwo()
	{
		return false;
	}

	@Override
	public boolean isLessThan(@NotNull final Digit right)
	{
		return ComparisonHelper.isLessThan(this, right);
	}

	@Override
	public boolean isEqualTo(@NotNull final Digit right)
	{
		return ComparisonHelper.isEqualTo(this, right);
	}

	@Override
	public boolean isNotEqualTo(@NotNull final Digit right)
	{
		return ComparisonHelper.isNotEqualTo(this, right);
	}

	@Override
	public boolean isGreaterThan(@NotNull final Digit right)
	{
		return ComparisonHelper.isGreaterThan(this, right);
	}

	@Override
	public boolean isLessThanOrEqualTo(@NotNull final Digit right)
	{
		return ComparisonHelper.isLessThanOrEqualTo(this, right);
	}

	@Override
	public boolean isGreaterThanOrEqualTo(@NotNull final Digit right)
	{
		return ComparisonHelper.isGreaterThanOrEqualTo(this, right);
	}

	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(digit);
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}
}
