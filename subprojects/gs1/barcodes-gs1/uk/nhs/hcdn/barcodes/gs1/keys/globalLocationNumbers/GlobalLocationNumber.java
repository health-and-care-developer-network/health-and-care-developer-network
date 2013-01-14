/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

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
