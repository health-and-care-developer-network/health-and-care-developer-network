package uk.nhs.hcdn.barcodes.gs1.keys.globalLocationNumbers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.keys.AbstractCheckDigitNumber;

public final class GlobalLocationNumber extends AbstractCheckDigitNumber<GlobalLocationNumberFormat>
{
	public GlobalLocationNumber(@NotNull final GlobalLocationNumberFormat globalLocationNumberFormat, @NotNull final Digits digits)
	{
		super(globalLocationNumberFormat, digits);
	}
}
