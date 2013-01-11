package uk.nhs.hcdn.barcodes.gs1.keys.globalServiceRelationNumbers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.keys.AbstractCheckDigitNumber;

public final class GlobalServiceRelationNumber extends AbstractCheckDigitNumber<GlobalServiceRelationNumberFormat>
{
	public GlobalServiceRelationNumber(@NotNull final GlobalServiceRelationNumberFormat globalServiceRelationNumberFormat, @NotNull final Digits digits)
	{
		super(globalServiceRelationNumberFormat, digits);
	}
}
