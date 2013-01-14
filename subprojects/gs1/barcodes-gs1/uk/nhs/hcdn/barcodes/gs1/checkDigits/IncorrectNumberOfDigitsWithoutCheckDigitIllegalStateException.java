/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.checkDigits;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class IncorrectNumberOfDigitsWithoutCheckDigitIllegalStateException extends IllegalStateException
{
	public IncorrectNumberOfDigitsWithoutCheckDigitIllegalStateException(final int correctNumberOfDigits)
	{
		super(format(ENGLISH, "Incorrect number of digits (without check digit) (expected %1$s digits)", correctNumberOfDigits));
	}
}
