package org.gov.data.nhs.hcdn.barcodes.gs1.barCodeTypes;

import org.gov.data.nhs.hcdn.barcodes.Alphanumeracy;
import org.gov.data.nhs.hcdn.barcodes.Directionality;
import org.gov.data.nhs.hcdn.barcodes.Numeracy;
import org.gov.data.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumberFormat;
import org.gov.data.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily;
import org.gov.data.nhs.hcdn.common.EnumeratedVariableArgumentsHelper;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static org.gov.data.nhs.hcdn.barcodes.Alphanumeracy.FortyEightAlphanumeric;
import static org.gov.data.nhs.hcdn.barcodes.Directionality.NotOmnidirectional;
import static org.gov.data.nhs.hcdn.barcodes.Numeracy.NoneNumeric;
import static org.gov.data.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumberFormat.GTIN_12;
import static org.gov.data.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumberFormat.GTIN_13;
import static org.gov.data.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumberFormat.GTIN_14;
import static org.gov.data.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily.GS1_128;
import static org.gov.data.nhs.hcdn.common.VariableArgumentsHelper.unmodifiableSetOf;

public enum Gs1128G1BarCodeType implements Gs1BarCodeType
{
	Gs1128,
	;

	@NonNls
	private static final Set<String> FormerNames = unmodifiableSetOf("UCC/EAN-128", "UCC-128", "EAN-128");

	// TODO: May not be a correct list
	private static final Set<GlobalTradeItemNumberFormat> GlobalTradeItemNumberFormats = EnumeratedVariableArgumentsHelper.unmodifiableSetOf(GTIN_12, GTIN_13, GTIN_14);

	@NonNls
	@NotNull
	@Override
	public String actualName()
	{
		return "GS1-128";
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	@NotNull
	public String toString()
	{
		return actualName();
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@NotNull
	@Override
	public Set<String> formerActualNames()
	{
		return FormerNames;
	}

	@NotNull
	@Override
	public Gs1BarCodeFamily barCodeFamily()
	{
		return GS1_128;
	}

	@NotNull
	@Override
	public Numeracy numeracy()
	{
		return NoneNumeric;
	}

	@NotNull
	@Override
	public Alphanumeracy alphanumeracy()
	{
		return FortyEightAlphanumeric;
	}

	@NotNull
	@Override
	public Directionality directionality()
	{
		return NotOmnidirectional;
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@NotNull
	@Override
	public Set<GlobalTradeItemNumberFormat> gtins()
	{
		return GlobalTradeItemNumberFormats;
	}
}
