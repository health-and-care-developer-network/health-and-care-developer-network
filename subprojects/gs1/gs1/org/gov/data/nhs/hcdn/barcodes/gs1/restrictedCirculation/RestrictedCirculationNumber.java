package org.gov.data.nhs.hcdn.barcodes.gs1.restrictedCirculation;

import org.gov.data.nhs.hcdn.barcodes.Digit;
import org.gov.data.nhs.hcdn.common.reflection.toString.AbstractToString;
import org.gov.data.nhs.hcdn.common.tuples.ComparablePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static org.gov.data.nhs.hcdn.barcodes.Digit.digit;
import static org.gov.data.nhs.hcdn.common.tuples.ComparablePair.comparablePair;

public final class RestrictedCirculationNumber extends AbstractToString implements Comparable<RestrictedCirculationNumber>
{
	private static final int EqualTo = 0;

	@NotNull
	public static RestrictedCirculationNumber from(final int first, final int second, final int third)
	{
		return restrictedCirculationNumber(digit(first), digit(second), digit(third));
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static RestrictedCirculationNumber restrictedCirculationNumber(@NotNull final Digit first, @NotNull final Digit second, @NotNull final Digit third)
	{
		return new RestrictedCirculationNumber(first, second, third);
	}

	@NotNull
	private final Digit[] threeDigits;

	public RestrictedCirculationNumber(@NotNull final Digit first, @NotNull final Digit second, @NotNull final Digit third)
	{
		threeDigits = new Digit[]
		{
			first,
			second,
			third
		};
	}

	@NotNull
	public ComparablePair<RestrictedCirculationNumber> to(final int first, final int second, final int third)
	{
		return comparablePair(this, from(first, second, third));
	}

	@Override
	public int compareTo(@NotNull final RestrictedCirculationNumber o)
	{
		for(int index = EqualTo; index < 3; index++)
		{
			final int comparison = threeDigits[index].compareTo(o.threeDigits[index]);
			if (comparison != EqualTo)
			{
				return comparison;
			}
		}
		return EqualTo;
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass()))
		{
			return false;
		}

		final RestrictedCirculationNumber that = (RestrictedCirculationNumber) obj;

		if (!Arrays.equals(threeDigits, that.threeDigits))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(threeDigits);
	}

	public int to0To999()
	{
		return scaleDigit(0, 100) + scaleDigit(1, 10) + scaleDigit(2, 0);
	}

	private int scaleDigit(final int index, final int scalar)
	{
		return threeDigits[index].digit() * scalar;
	}
}
