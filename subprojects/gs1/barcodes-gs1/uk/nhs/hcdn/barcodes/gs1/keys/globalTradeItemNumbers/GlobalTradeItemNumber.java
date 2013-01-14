/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.Gs1CompanyPrefixAndItem;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.IncorrectCheckDigitIllegalStateException;
import uk.nhs.hcdn.barcodes.gs1.gs1Prefixes.Gs1Prefix;
import uk.nhs.hcdn.barcodes.gs1.keys.AbstractCheckDigitNumber;

import static uk.nhs.hcdn.barcodes.Digit.Zero;
import static uk.nhs.hcdn.barcodes.Digits.slice;
import static uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumberFormat.*;

// More number kinds at http://www.gs1.org/barcodes/technical/id_keys
public final class GlobalTradeItemNumber extends AbstractCheckDigitNumber<GlobalTradeItemNumberFormat>
{
	@NotNull
	public static GlobalTradeItemNumber parseGlobalTradeItemNumber(@NotNull final Digits digits, final boolean includesCheckDigit) throws DigitsAreNotForAGlobalTradeItemNumberException
	{
		final int correctionForMissingCheckDigit = includesCheckDigit ? 0 : 1;
		final int size = digits.size() + correctionForMissingCheckDigit;

		@Nullable final GlobalTradeItemNumberFormat globalTradeItemNumberFormat = globalTradeItemNumberFormatNumberOfDigits(size);
		if (globalTradeItemNumberFormat == null)
		{
			throw new DigitsAreNotForAGlobalTradeItemNumberException(digits);
		}

		if (globalTradeItemNumberFormat == GTIN_14)
		{
			return reduceDigitsToSmallestGtin(digits, includesCheckDigit);
		}

		final Digits withCheckDigit;
		if (includesCheckDigit)
		{
			withCheckDigit = digits;
		}
		else
		{
			final Digit checkDigit = globalTradeItemNumberFormat.calculateCheckDigit(digits);
			withCheckDigit = digits.add(checkDigit);
		}
		return new GlobalTradeItemNumber(globalTradeItemNumberFormat, withCheckDigit);
	}

	@SuppressWarnings("FeatureEnvy")
	@NotNull
	public static GlobalTradeItemNumber reduceDigitsToSmallestGtin(@NotNull final Digits digits, final boolean includesCheckDigit) throws DigitsAreNotForAGlobalTradeItemNumberException
	{
		final int correctionForMissingCheckDigit = includesCheckDigit ? 0 : 1;
		final int size = digits.size() + correctionForMissingCheckDigit;

		if (size != GTIN_14.size())
		{
			throw new DigitsAreNotForAGlobalTradeItemNumberException(digits);
		}

		final GlobalTradeItemNumberFormat actual;
		final Digits sliced;
		if (digits.digitAt(0) == Zero)
		{
			if (digits.digitAt(1) == Zero)
			{
				if (digits.digitAt(2) == Zero && digits.digitAt(3) == Zero && digits.digitAt(4) == Zero && digits.digitAt(5) == Zero)
				{
					actual = GTIN_8;
					sliced = digits.slice(6);
				}
				else
				{
					actual = GTIN_12;
					sliced = digits.slice(2);
				}
			}
			else
			{
				actual = GTIN_13;
				sliced = digits.slice(1);
			}
		}
		else
		{
			actual = GTIN_14;
			sliced = digits;
		}

		final Digits withCheckDigit;
		if (includesCheckDigit)
		{
			withCheckDigit = sliced;
		}
		else
		{
			final Digit checkDigit = actual.calculateCheckDigit(digits);
			withCheckDigit = digits.add(checkDigit);
		}

		try
		{
			return new GlobalTradeItemNumber(actual, withCheckDigit);
		}
		catch (IncorrectCheckDigitIllegalStateException e)
		{
			throw new DigitsAreNotForAGlobalTradeItemNumberException(digits, e);
		}
	}

	public GlobalTradeItemNumber(@NotNull final GlobalTradeItemNumberFormat globalTradeItemNumberFormat, @NotNull final Digits digits)
	{
		super(globalTradeItemNumberFormat, digits);
	}

	@NotNull
	public Digit indicatorDigit()
	{
		return digitAtPositionT(T1);
	}

	@NotNull
	public IndicatorDigitKind indicatorDigitKind()
	{
		return IndicatorDigitKind.indicatorDigitKind(indicatorDigit());
	}

	@NotNull
	public Gs1Prefix gs1Prefix()
	{
		return new Gs1Prefix(keyFormat.gs1PrefixDigits(digits));
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
