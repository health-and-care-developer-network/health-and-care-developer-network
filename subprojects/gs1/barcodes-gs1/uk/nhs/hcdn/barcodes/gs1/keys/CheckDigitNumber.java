package uk.nhs.hcdn.barcodes.gs1.keys;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.DigitList;

public interface CheckDigitNumber extends DigitList
{
	@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
	@NotNull
	Digit checkDigit();
}
