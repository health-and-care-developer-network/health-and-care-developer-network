/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.IncorrectCheckDigitIllegalStateException;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class DigitsAreNotForAGlobalTradeItemNumberException extends Exception
{
	public DigitsAreNotForAGlobalTradeItemNumberException(@NotNull final Digits digits)
	{
		super(message(digits));
	}

	public DigitsAreNotForAGlobalTradeItemNumberException(@NotNull final Digits digits, @NotNull final IncorrectCheckDigitIllegalStateException cause)
	{
		super(message(digits), cause);
	}

	private static String message(final Digits digits)
	{
		return format(ENGLISH, "Digits (%1$s) are not fot a Global Trade Item Number", digits);
	}
}
