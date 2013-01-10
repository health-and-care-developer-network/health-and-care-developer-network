package org.gov.data.nhs.hcdn.barcodes;

import org.gov.data.nhs.hcdn.common.naming.ActualName;
import org.gov.data.nhs.hcdn.common.naming.FormerActualNames;
import org.jetbrains.annotations.NotNull;

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
