package uk.nhs.hcdn.barcodes.gs1.barCodeTypes;

import uk.nhs.hcdn.barcodes.BarCodeType;
import uk.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumberFormat;
import uk.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.BarCodeType;
import uk.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumberFormat;

import java.util.Set;

public interface Gs1BarCodeType extends BarCodeType
{
	@NotNull
	@Override
	Gs1BarCodeFamily barCodeFamily();

	@NotNull
	Set<GlobalTradeItemNumberFormat> gtins();
}
