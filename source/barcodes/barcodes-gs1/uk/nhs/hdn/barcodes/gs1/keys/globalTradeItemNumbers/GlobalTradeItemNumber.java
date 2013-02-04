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

package uk.nhs.hdn.barcodes.gs1.keys.globalTradeItemNumbers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.barcodes.gs1.keys.AbstractKeyFormatCheckDigitNumber;
import uk.nhs.hdn.common.digits.Digit;
import uk.nhs.hdn.common.digits.Digits;
import uk.nhs.hdn.barcodes.gs1.Gs1CompanyPrefixAndItem;
import uk.nhs.hdn.common.digits.IncorrectCheckDigitIllegalStateException;
import uk.nhs.hdn.barcodes.gs1.gs1Prefixes.Gs1Prefix;

import static uk.nhs.hdn.common.digits.Digit.Zero;
import static uk.nhs.hdn.common.digits.Digits.slice;
import static uk.nhs.hdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumberFormat.*;

// More number kinds at http://www.gs1.org/barcodes/technical/id_keys
public final class GlobalTradeItemNumber extends AbstractKeyFormatCheckDigitNumber<GlobalTradeItemNumberFormat>
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
		return new Gs1Prefix(extractCheckDigitCalculator.gs1PrefixDigits(digits));
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
