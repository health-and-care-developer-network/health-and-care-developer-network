package org.gov.data.nhs.hcdn.barcodes.gs1.barCodeTypes;

import org.gov.data.nhs.hcdn.barcodes.Alphanumeracy;
import org.gov.data.nhs.hcdn.barcodes.Directionality;
import org.gov.data.nhs.hcdn.barcodes.Numeracy;
import org.gov.data.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumberFormat;
import org.gov.data.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static java.util.Collections.emptySet;
import static org.gov.data.nhs.hcdn.barcodes.Alphanumeracy.Gs1DataMatrixAlphanumeric;
import static org.gov.data.nhs.hcdn.barcodes.Directionality.TwoDimensional;
import static org.gov.data.nhs.hcdn.barcodes.Numeracy.Gs1DataMatrixNumeric;
import static org.gov.data.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumberFormat.GTIN_12;
import static org.gov.data.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumberFormat.GTIN_13;
import static org.gov.data.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily.GS1DataMatrix;
import static org.gov.data.nhs.hcdn.common.EnumeratedVariableArgumentsHelper.unmodifiableSetOf;

public enum Gs1DataMatrixGs1BarCodeType implements Gs1BarCodeType
{
	Ecc200,
	;

	private static final Set<String> FormerNames = emptySet();

	// TODO: Might be an incorrect set
	private static final Set<GlobalTradeItemNumberFormat> GlobalTradeItemNumberFormats = unmodifiableSetOf(GTIN_12, GTIN_13);

	@NonNls
	@NotNull
	@Override
	public String actualName()
	{
		return "ECC-200";
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	@NotNull
	public String toString()
	{
		return actualName();
	}

	@NotNull
	@Override
	public Gs1BarCodeFamily barCodeFamily()
	{
		return GS1DataMatrix;
	}

	@NotNull
	@Override
	public Numeracy numeracy()
	{
		return Gs1DataMatrixNumeric;
	}

	@NotNull
	@Override
	public Alphanumeracy alphanumeracy()
	{
		return Gs1DataMatrixAlphanumeric;
	}

	@NotNull
	@Override
	public Directionality directionality()
	{
		return TwoDimensional;
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@NotNull
	@Override
	public Set<GlobalTradeItemNumberFormat> gtins()
	{
		return GlobalTradeItemNumberFormats;
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@NotNull
	@Override
	public Set<String> formerActualNames()
	{
		return FormerNames;
	}
}
