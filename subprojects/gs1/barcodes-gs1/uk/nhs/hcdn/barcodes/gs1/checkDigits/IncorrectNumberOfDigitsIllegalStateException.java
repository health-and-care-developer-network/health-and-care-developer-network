/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.checkDigits;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class IncorrectNumberOfDigitsIllegalStateException extends IllegalStateException
{
	public IncorrectNumberOfDigitsIllegalStateException(final int correctNumberOfDigits, final int maximumSizeOfSerialNumber)
	{
		super(format(ENGLISH, "Incorrect number of digits (expected %1$s digits, including a check digit, and upto %2$s serial component digits)", correctNumberOfDigits, maximumSizeOfSerialNumber));
	}

	public IncorrectNumberOfDigitsIllegalStateException(final int atLeastNumberOfDigits)
	{
		super(format(ENGLISH, "Incorrect number of digits (expected at least %1$s digits)", atLeastNumberOfDigits));
	}
}
