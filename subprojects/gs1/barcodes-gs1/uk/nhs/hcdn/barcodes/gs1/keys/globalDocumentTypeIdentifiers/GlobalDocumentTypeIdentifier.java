package uk.nhs.hcdn.barcodes.gs1.keys.globalDocumentTypeIdentifiers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.keys.AbstractWithSerialComponentCheckDigitNumber;

public final class GlobalDocumentTypeIdentifier extends AbstractWithSerialComponentCheckDigitNumber<GlobalDocumentTypeIdentifierFormat>
{
	public GlobalDocumentTypeIdentifier(@NotNull final GlobalDocumentTypeIdentifierFormat globalDocumentTypeIdentifierFormat, @NotNull final Digits digits)
	{
		super(globalDocumentTypeIdentifierFormat, digits);
	}
}
