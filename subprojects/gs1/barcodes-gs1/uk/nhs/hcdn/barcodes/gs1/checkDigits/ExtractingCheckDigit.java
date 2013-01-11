package uk.nhs.hcdn.barcodes.gs1.checkDigits;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.DigitList;

public interface ExtractingCheckDigit extends CheckDigit
{
	@NotNull
	Digit extract(@NotNull final DigitList digits, final int oneBasedPositionT);
}
