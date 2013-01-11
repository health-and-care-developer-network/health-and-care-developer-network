package uk.nhs.hcdn.barcodes.gs1.keys.globalReturnableAssetIdentifiers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.keys.AbstractWithSerialComponentCheckDigitNumber;
import uk.nhs.hcdn.barcodes.gs1.keys.globalDocumentTypeIdentifiers.GlobalDocumentTypeIdentifierFormat;

public final class GlobalReturnableAssetIdentifier extends AbstractWithSerialComponentCheckDigitNumber<GlobalDocumentTypeIdentifierFormat>
{
	public GlobalReturnableAssetIdentifier(@NotNull final GlobalDocumentTypeIdentifierFormat globalDocumentTypeIdentifierFormat, @NotNull final Digits digits)
	{
		super(globalDocumentTypeIdentifierFormat, digits);
	}
}
