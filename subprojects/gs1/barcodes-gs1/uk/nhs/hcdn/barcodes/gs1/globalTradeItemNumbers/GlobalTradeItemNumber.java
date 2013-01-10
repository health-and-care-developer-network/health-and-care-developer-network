package uk.nhs.hcdn.barcodes.gs1.globalTradeItemNumbers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.DigitList;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.Gs1CompanyPrefixAndItem;
import uk.nhs.hcdn.common.comparison.ComparisonResult;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import static uk.nhs.hcdn.barcodes.Digits.slice;
import static uk.nhs.hcdn.barcodes.gs1.globalTradeItemNumbers.GlobalTradeItemNumberFormat.globalTradeItemNumberFormatNumberOfDigits;
import static uk.nhs.hcdn.common.comparison.ComparisonHelper.isNotEqualTo;
import static uk.nhs.hcdn.common.comparison.ComparisonResult.EqualTo;

// More number kinds at http://www.gs1.org/barcodes/technical/id_keys
public final class GlobalTradeItemNumber extends AbstractToString implements DigitList
{
	@NotNull
	public static GlobalTradeItemNumber tryToDeduceGlobalTradeItemNumber(@NotNull final Digits digits) throws DigitsAreNotForAGlobalTradeItemNumberException
	{
		final int size = digits.size();
		@Nullable final GlobalTradeItemNumberFormat globalTradeItemNumberFormat = globalTradeItemNumberFormatNumberOfDigits(size);
		if (globalTradeItemNumberFormat == null)
		{
			throw new DigitsAreNotForAGlobalTradeItemNumberException(digits);
		}
		try
		{
			return new GlobalTradeItemNumber(globalTradeItemNumberFormat, digits);
		}
		catch (IncorrectCheckDigitForGlobalTradeItemNumberIllegalStateException e)
		{
			throw new DigitsAreNotForAGlobalTradeItemNumberException(digits, e);
		}
	}

	@NotNull
	private final GlobalTradeItemNumberFormat globalTradeItemNumberFormat;

	@NotNull
	private final Digits digits;

	public GlobalTradeItemNumber(@NotNull final GlobalTradeItemNumberFormat globalTradeItemNumberFormat, @NotNull final Digits digits)
	{
		globalTradeItemNumberFormat.guardCorrectNumberOfDigits(digits);
		globalTradeItemNumberFormat.guardCheckDigitCorrect(digits);

		this.globalTradeItemNumberFormat = globalTradeItemNumberFormat;
		this.digits = digits;
	}

	@NotNull
	public Digit indicatorDigit()
	{
		return digitAtPositionT(GlobalTradeItemNumberFormat.T1);
	}

	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@NotNull
	public Digit checkDigit()
	{
		return digitAtPositionT(GlobalTradeItemNumberFormat.T14);
	}

	@Override
	@NotNull
	public Digit digitAt(final int index)
	{
		return digitAtPositionT(index + 1);
	}

	@NotNull
	@Override
	public Digit digitAtPositionT(final int oneBasedPositionT)
	{
		return globalTradeItemNumberFormat.extract(digits, oneBasedPositionT);
	}

	@Override
	public int size()
	{
		return GlobalTradeItemNumberFormat.MaximumOneBasedPositionT;
	}

	@NotNull
	public Gs1CompanyPrefixAndItem gs1CompanyPrefixAndItem()
	{
		return new Gs1CompanyPrefixAndItem(slice(this, GlobalTradeItemNumberFormat.T2, GlobalTradeItemNumberFormat.T13));
	}

	@NotNull
	public Digits normalise()
	{
		return slice(this, GlobalTradeItemNumberFormat.T1, GlobalTradeItemNumberFormat.MaximumOneBasedPositionT);
	}

	@Override
	@ComparisonResult
	public int compareTo(@NotNull final DigitList o)
	{
		for(int oneBasedPositionT = GlobalTradeItemNumberFormat.T1; oneBasedPositionT <= GlobalTradeItemNumberFormat.MaximumOneBasedPositionT; oneBasedPositionT++)
		{
			final Digit us = digitAtPositionT(oneBasedPositionT);
			final Digit them = o.digitAtPositionT(oneBasedPositionT);
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
