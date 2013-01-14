/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.naming.ActualName;
import uk.nhs.hcdn.common.naming.FormerActualNames;

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
