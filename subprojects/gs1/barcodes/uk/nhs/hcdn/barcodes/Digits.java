package uk.nhs.hcdn.barcodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.comparison.ComparisonResult;

import java.util.Arrays;

import static uk.nhs.hcdn.barcodes.Digit.digit;
import static uk.nhs.hcdn.barcodes.Digit.digitFromUtf16Code;
import static uk.nhs.hcdn.common.VariableArgumentsHelper.copyOf;
import static uk.nhs.hcdn.common.comparison.ComparisonHelper.compareInt;
import static uk.nhs.hcdn.common.comparison.ComparisonHelper.isNotEqualTo;

public final class Digits implements Comparable<Digits>
{
	private static final Digit[] EmptyDigits = new Digit[0];

	@NotNull public static final Digits Empty = new Digits(EmptyDigits);

	public static Digits digits(@NotNull final int... digits)
	{
		return new Digits(digits);
	}

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

	public Digits(@NotNull final Digit... digits)
	{
		size = digits.length;
		if (size == 0)
		{
			this.digits = EmptyDigits;
			return;
		}
		this.digits = copyOf(digits);
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
	public int compareTo(@NotNull final Digits o)
	{
		@ComparisonResult final int sizeComparisonResult = compareInt(size, o.size);
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

	@NotNull
	public Digit digitAt(final int index)
	{
		if (index < 0 || index >= size)
		{
			throw new IllegalArgumentException("index is out of range");
		}
		return digits[index];
	}

	@ComparisonResult
	public int compareDigitsAtIndex(@NotNull final Digits digits, final int index)
	{
		return compareDigitAtIndexWith(index, digits.digitAt(index));
	}

	@ComparisonResult
	public int compareDigitAtIndexWith(final int index, @NotNull final Digit digit)
	{
		return digitAt(index).compareTo(digit);
	}
}
