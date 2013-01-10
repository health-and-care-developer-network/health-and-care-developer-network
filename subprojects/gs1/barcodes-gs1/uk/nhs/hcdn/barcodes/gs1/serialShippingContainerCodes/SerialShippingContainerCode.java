package uk.nhs.hcdn.barcodes.gs1.serialShippingContainerCodes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digit;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.AbstractCheckDigitNumber;

import static uk.nhs.hcdn.barcodes.gs1.serialShippingContainerCodes.SerialShippingContainerCodeFormat.MaximumOneBasedPositionT;

public final class SerialShippingContainerCode extends AbstractCheckDigitNumber
{
	public SerialShippingContainerCode(@SuppressWarnings("TypeMayBeWeakened") @NotNull final SerialShippingContainerCodeFormat serialShippingContainerCodeFormat, @NotNull final Digits digits)
	{
		super(serialShippingContainerCodeFormat, digits);
	}

	@NotNull
	@Override
	public Digit checkDigit()
	{
		return digitAtPositionT(MaximumOneBasedPositionT);
	}

	@Override
	public int size()
	{
		return MaximumOneBasedPositionT;
	}
}
