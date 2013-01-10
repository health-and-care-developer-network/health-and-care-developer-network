package uk.nhs.hcdn.barcodes.gs1.checkDigits;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;

public interface CheckDigit
{
	void guardCorrectNumberOfDigitsIfNoCheckDigit(@NotNull final Digits digits);

	void guardCorrectNumberOfDigits(@NotNull final Digits digits);

	void guardCheckDigitCorrect(@NotNull final Digits digits);

	@NotNull
	Digit calculateCheckDigit(@NotNull final Digits withoutCheckDigit);
}
