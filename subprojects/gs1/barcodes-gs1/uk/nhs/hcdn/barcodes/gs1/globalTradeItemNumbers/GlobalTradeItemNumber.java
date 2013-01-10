package uk.nhs.hcdn.barcodes.gs1.globalTradeItemNumbers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.Gs1CompanyPrefixAndItem;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.AbstractCheckDigitNumber;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.IncorrectCheckDigitIllegalStateException;

import static uk.nhs.hcdn.barcodes.Digits.slice;
import static uk.nhs.hcdn.barcodes.gs1.globalTradeItemNumbers.GlobalTradeItemNumberFormat.*;

// More number kinds at http://www.gs1.org/barcodes/technical/id_keys
public final class GlobalTradeItemNumber extends AbstractCheckDigitNumber
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
		catch (IncorrectCheckDigitIllegalStateException e)
		{
			throw new DigitsAreNotForAGlobalTradeItemNumberException(digits, e);
		}
	}

	public GlobalTradeItemNumber(@SuppressWarnings("TypeMayBeWeakened") @NotNull final GlobalTradeItemNumberFormat globalTradeItemNumberFormat, @NotNull final Digits digits)
	{
		super(globalTradeItemNumberFormat, digits);
	}

	@NotNull
	public Digit indicatorDigit()
	{
		return digitAtPositionT(T1);
	}

	@Override
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@NotNull
	public Digit checkDigit()
	{
		return digitAtPositionT(T14);
	}

	@Override
	public int size()
	{
		return MaximumOneBasedPositionT;
	}

	@NotNull
	public Gs1CompanyPrefixAndItem gs1CompanyPrefixAndItem()
	{
		return new Gs1CompanyPrefixAndItem(slice(this, T2, T13));
	}

	@NotNull
	public Digits normalise()
	{
		return slice(this, T1, MaximumOneBasedPositionT);
	}
}
