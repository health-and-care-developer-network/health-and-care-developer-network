/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.keys;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digits;

public abstract class AbstractWithSerialComponentCheckDigitNumber<F extends SerialComponentKeyFormat> extends AbstractCheckDigitNumber<F>
{
	protected AbstractWithSerialComponentCheckDigitNumber(@SuppressWarnings("TypeMayBeWeakened") @NotNull final F serialComponentKeyFormat, @NotNull final Digits digits)
	{
		super(serialComponentKeyFormat, digits);
	}

	@NotNull
	public Digits serialComponent()
	{
		return digits.slice(keyFormat.size(), digits.size());
	}
}
