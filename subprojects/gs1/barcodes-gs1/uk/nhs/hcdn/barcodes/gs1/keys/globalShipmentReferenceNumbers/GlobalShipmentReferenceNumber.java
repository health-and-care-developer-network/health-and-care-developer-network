package uk.nhs.hcdn.barcodes.gs1.keys.globalShipmentReferenceNumbers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.keys.AbstractCheckDigitNumber;

public final class GlobalShipmentReferenceNumber extends AbstractCheckDigitNumber
{
	public GlobalShipmentReferenceNumber(@SuppressWarnings("TypeMayBeWeakened") @NotNull final GlobalShipmentReferenceNumberFormat globalShipmentReferenceNumberFormat, @NotNull final Digits digits)
	{
		super(globalShipmentReferenceNumberFormat, digits);
	}
}
