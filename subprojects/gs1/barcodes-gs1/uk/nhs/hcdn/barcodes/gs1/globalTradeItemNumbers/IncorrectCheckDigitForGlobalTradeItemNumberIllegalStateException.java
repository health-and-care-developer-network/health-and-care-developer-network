package uk.nhs.hcdn.barcodes.gs1.globalTradeItemNumbers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class IncorrectCheckDigitForGlobalTradeItemNumberIllegalStateException extends IllegalStateException
{
	public IncorrectCheckDigitForGlobalTradeItemNumberIllegalStateException(@NotNull final Digits digits, @NotNull final Digit expectedCheckDigit)
	{
		super(format(ENGLISH, "Incorrect check digit for Global Trade Item Number (%1$s), expected %2$s", digits, expectedCheckDigit));
	}
}
