package uk.nhs.hcdn.barcodes.gs1.keys.serialShippingContainerCodes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.keys.AbstractCheckDigitNumber;

public final class SerialShippingContainerCode extends AbstractCheckDigitNumber<SerialShippingContainerCodeFormat>
{
	public SerialShippingContainerCode(@NotNull final SerialShippingContainerCodeFormat serialShippingContainerCodeFormat, @NotNull final Digits digits)
	{
		super(serialShippingContainerCodeFormat, digits);
	}
}
