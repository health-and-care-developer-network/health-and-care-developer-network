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
