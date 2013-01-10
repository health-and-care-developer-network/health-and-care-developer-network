package uk.nhs.hcdn.barcodes.gs1;

import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.common.comparison.ComparisonResult;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digits;

import static uk.nhs.hcdn.barcodes.gs1.Gs1CompanyPrefixAndItem.NumberOfDigits;
import static uk.nhs.hcdn.common.comparison.ComparisonHelper.isNotEqualTo;
import static uk.nhs.hcdn.common.comparison.ComparisonResult.EqualTo;

public final class GlobalTradeItemNumber extends AbstractToString implements Comparable<GlobalTradeItemNumber>
{
	private static final int T1 = 1;
	private static final int T2 = 2;
	private static final int T13 = 13;
	private static final int T14 = 14;
	public static final int MaximumOneBasedPositionT = T14;

	@NotNull
	private final GlobalTradeItemNumberFormat globalTradeItemNumberFormat;

	@NotNull
	private final Digits digits;

	public GlobalTradeItemNumber(@NotNull final GlobalTradeItemNumberFormat globalTradeItemNumberFormat, @NotNull final Digits digits)
	{
		globalTradeItemNumberFormat.guardCorrectNumberOfDigits(digits);

		this.globalTradeItemNumberFormat = globalTradeItemNumberFormat;
		this.digits = digits;
	}

	@NotNull
	public Digit indicatorDigit()
	{
		return digit(T1);
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@NotNull
	public Digit checkDigit()
	{
		return digit(T14);
	}

	@NotNull
	public Digit digit(final int oneBasedPositionT)
	{
		return globalTradeItemNumberFormat.extract(digits, oneBasedPositionT);
	}

	@NotNull
	public Gs1CompanyPrefixAndItem gs1CompanyPrefixAndItem()
	{
		final Digit[] slice = new Digit[NumberOfDigits];
		for(int oneBasedPositionT = T2; oneBasedPositionT <= T13; oneBasedPositionT++)
		{
			slice[oneBasedPositionT - T2] = digit(oneBasedPositionT);
		}
		return new Gs1CompanyPrefixAndItem(new Digits(slice));
	}

	@Override
	@ComparisonResult
	public int compareTo(@NotNull final GlobalTradeItemNumber o)
	{
		for(int oneBasedPositionT = 1; oneBasedPositionT <= MaximumOneBasedPositionT; oneBasedPositionT++)
		{
			final Digit us = digit(oneBasedPositionT);
			final Digit them = o.digit(oneBasedPositionT);
			@ComparisonResult final int digitComparisonResult = us.compareTo(them);
			if (isNotEqualTo(digitComparisonResult))
			{
				return digitComparisonResult;
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
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final GlobalTradeItemNumber that = (GlobalTradeItemNumber) obj;

		if (!digits.equals(that.digits))
		{
			return false;
		}
		if (globalTradeItemNumberFormat != that.globalTradeItemNumberFormat)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = globalTradeItemNumberFormat.hashCode();
		result = 31 * result + digits.hashCode();
		return result;
	}
}
