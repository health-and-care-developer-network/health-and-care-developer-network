package uk.nhs.hcdn.barcodes.gs1.checkDigits;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class IncorrectNumberOfDigitsIllegalStateException extends IllegalStateException
{
	public IncorrectNumberOfDigitsIllegalStateException(final int correctNumberOfDigits)
	{
		super(format(ENGLISH, "Incorrect number of digits (expected %1$s digits)", correctNumberOfDigits));
	}
}
