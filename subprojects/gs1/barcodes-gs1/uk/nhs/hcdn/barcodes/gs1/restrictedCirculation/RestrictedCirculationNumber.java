package uk.nhs.hcdn.barcodes.gs1.restrictedCirculation;

import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.common.comparison.ComparisonResult;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.tuples.ComparablePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digits;

import static uk.nhs.hcdn.common.tuples.ComparablePair.comparablePair;

public final class RestrictedCirculationNumber extends AbstractToString implements Comparable<RestrictedCirculationNumber>
{
	@NotNull
	public static RestrictedCirculationNumber from(final int first, final int second, final int third)
	{
		return new RestrictedCirculationNumber(new Digits(first, second, third));
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static RestrictedCirculationNumber restrictedCirculationNumber(@NotNull final Digit first, @NotNull final Digit second, @NotNull final Digit third)
	{
		return new RestrictedCirculationNumber(new Digits(first, second, third));
	}

	@NotNull
	private final Digits threeDigits;

	public RestrictedCirculationNumber(@NotNull final Digits threeDigits)
	{
		this.threeDigits = threeDigits;
	}

	@NotNull
	public ComparablePair<RestrictedCirculationNumber> to(final int first, final int second, final int third)
	{
		return comparablePair(this, from(first, second, third));
	}

	@Override
	@ComparisonResult
	public int compareTo(@NotNull final RestrictedCirculationNumber o)
	{
		return threeDigits.compareTo(o.threeDigits);
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

		final RestrictedCirculationNumber that = (RestrictedCirculationNumber) obj;

		if (!threeDigits.equals(that.threeDigits))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return threeDigits.hashCode();
	}

	public int to0To999()
	{
		return scaleDigit(0, 100) + scaleDigit(1, 10) + scaleDigit(2, 0);
	}

	private int scaleDigit(final int index, final int scalar)
	{
		return threeDigits.digitAt(index).digit() * scalar;
	}
}
