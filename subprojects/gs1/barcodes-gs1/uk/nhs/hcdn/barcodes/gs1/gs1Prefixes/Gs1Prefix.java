package uk.nhs.hcdn.barcodes.gs1.gs1Prefixes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.common.comparison.ComparisonResult;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.tuples.ComparablePair;

import static uk.nhs.hcdn.common.tuples.ComparablePair.comparablePair;

public final class Gs1Prefix extends AbstractToString implements Comparable<Gs1Prefix>
{
	@NotNull
	private final Digits threeDigits;

	@NotNull
	private final Gs1PrefixAssignment gs1PrefixAssignment;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	public Gs1Prefix(@NotNull final Digits threeDigits)
	{
		this.threeDigits = threeDigits;
		gs1PrefixAssignment = KnownGs1PrefixAssignment.gs1PrefixAssignment(this);
	}

	@NotNull
	public Gs1PrefixAssignment gs1PrefixAssignment()
	{
		return gs1PrefixAssignment;
	}

	@NotNull
	public ComparablePair<Gs1Prefix> to(@NotNull final Digits digits)
	{
		return comparablePair(this, new Gs1Prefix(digits));
	}

	@Override
	@ComparisonResult
	public int compareTo(@NotNull final Gs1Prefix o)
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

		final Gs1Prefix that = (Gs1Prefix) obj;

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
