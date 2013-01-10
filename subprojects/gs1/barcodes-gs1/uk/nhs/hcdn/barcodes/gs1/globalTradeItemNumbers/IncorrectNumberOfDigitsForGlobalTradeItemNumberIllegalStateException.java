package uk.nhs.hcdn.barcodes.gs1.globalTradeItemNumbers;

import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class IncorrectNumberOfDigitsForGlobalTradeItemNumberIllegalStateException extends IllegalStateException
{
	public IncorrectNumberOfDigitsForGlobalTradeItemNumberIllegalStateException(@NotNull final GlobalTradeItemNumberFormat globalTradeItemNumberFormat, final int correctNumberOfDigits)
	{
		super(format(ENGLISH, "Incorrect number of digits for %1$s (expected %2$s digits)", globalTradeItemNumberFormat, correctNumberOfDigits));
	}
}
