package uk.nhs.hcdn.barcodes;

import uk.nhs.hcdn.common.naming.ActualName;
import uk.nhs.hcdn.common.naming.FormerActualNames;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.naming.ActualName;

public interface BarCodeType extends ActualName, FormerActualNames
{
	@NotNull
	BarCodeFamily barCodeFamily();

	@NotNull
	Numeracy numeracy();

	@NotNull
	Alphanumeracy alphanumeracy();

	@NotNull
	Directionality directionality();
}
