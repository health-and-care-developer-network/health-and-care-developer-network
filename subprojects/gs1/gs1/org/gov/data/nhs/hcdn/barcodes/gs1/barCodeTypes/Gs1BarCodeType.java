package org.gov.data.nhs.hcdn.barcodes.gs1.barCodeTypes;

import org.gov.data.nhs.hcdn.barcodes.BarCodeType;
import org.gov.data.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumberFormat;
import org.gov.data.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Gs1BarCodeType extends BarCodeType
{
	@NotNull
	@Override
	Gs1BarCodeFamily barCodeFamily();

	@NotNull
	Set<GlobalTradeItemNumberFormat> gtins();
}
