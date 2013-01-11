package uk.nhs.hcdn.barcodes.gs1.keys;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digits;

public class AbstractWithSerialComponentCheckDigitNumber extends AbstractCheckDigitNumber
{
	protected AbstractWithSerialComponentCheckDigitNumber(@NotNull final KeyFormat keyFormat, @NotNull final Digits digits)
	{
		super(keyFormat, digits);
	}
}
