package org.gov.data.nhs.hcdn.barcodes.gs1;

import org.gov.data.nhs.hcdn.barcodes.Digit;
import org.gov.data.nhs.hcdn.common.reflection.toString.AbstractToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static org.gov.data.nhs.hcdn.common.VariableArgumentsHelper.copyOf;

public final class GlobalTradeItemNumber extends AbstractToString implements Comparable<GlobalTradeItemNumber>
{
	private static final int T1 = 1;
	private static final int T2 = 2;
	private static final int T13 = 13;
	private static final int T14 = 14;
	public static final int MaximumOneBasedPositionT = T14;

	private static final int EqualTo = 0;

	@NotNull
	private final GlobalTradeItemNumberFormat globalTradeItemNumberFormat;

	@NotNull
	private final Digit[] digits;

	public GlobalTradeItemNumber(@NotNull final GlobalTradeItemNumberFormat globalTradeItemNumberFormat, @NotNull final Digit... digits)
	{
		globalTradeItemNumberFormat.guardCorrectNumberOfDigits(digits);

		this.globalTradeItemNumberFormat = globalTradeItemNumberFormat;
		this.digits = copyOf(digits);
	}

	@NotNull
	public Digit digit(final int oneBasedPositionT)
	{
		return globalTradeItemNumberFormat.extract(digits, oneBasedPositionT);
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
	public Gs1CompanyPrefixAndItem gs1CompanyPrefixAndItem()
	{
		final Digit[] digit = new Digit[12];
		for(int oneBasedPositionT = T2; oneBasedPositionT <= T13; oneBasedPositionT++)
		{
			digit[oneBasedPositionT - T2] = digit(oneBasedPositionT);
		}
		return new Gs1CompanyPrefixAndItem(digit);
	}

	@Override
	public int compareTo(@NotNull final GlobalTradeItemNumber o)
	{
		for(int oneBasedPositionT = 1; oneBasedPositionT <= MaximumOneBasedPositionT; oneBasedPositionT++)
		{
			final Digit us = digit(oneBasedPositionT);
			final Digit them = o.digit(oneBasedPositionT);
			final int comparison = us.compareTo(them);
			if (comparison != EqualTo)
			{
				return comparison;
			}
		}
		return EqualTo;
	}

	@Override
	public boolean equals(@Nullable final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final GlobalTradeItemNumber that = (GlobalTradeItemNumber) o;

		if (!Arrays.equals(digits, that.digits))
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
		result = 31 * result + Arrays.hashCode(digits);
		return result;
	}
}
