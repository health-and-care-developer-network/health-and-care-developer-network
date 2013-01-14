/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.barCodeTypes;

import uk.nhs.hcdn.barcodes.BarCodeType;
import uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumberFormat;
import uk.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Gs1BarCodeType extends BarCodeType
{
	@NotNull
	@Override
	Gs1BarCodeFamily barCodeFamily();

	@NotNull
	Set<GlobalTradeItemNumberFormat> gtins();

	boolean canCarryApplicationIdentifiers();
}
