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
import static org.gov.data.nhs.hcdn.barcodes.Alphanumeracy.NoneAlphanumeric;
import static org.gov.data.nhs.hcdn.barcodes.Directionality.NotOmnidirectional;
import static org.gov.data.nhs.hcdn.barcodes.Numeracy.FourteenNumeric;
import static org.gov.data.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumberFormat.*;
import static org.gov.data.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily.ITF_14;
import static org.gov.data.nhs.hcdn.common.EnumeratedVariableArgumentsHelper.unmodifiableSetOf;

public enum Itf14Gs1BarCodeType implements Gs1BarCodeType
{
	Itf14,
	;
	;

	private static final Set<String> FormerNames = emptySet();

	private static final Set<GlobalTradeItemNumberFormat> GlobalTradeItemNumberFormats = unmodifiableSetOf(GTIN_12, GTIN_13, GTIN_14);

	@NonNls
	@NotNull
	@Override
	public String actualName()
	{
		return "ITF-14";
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
		return ITF_14;
	}

	@NotNull
	@Override
	public Numeracy numeracy()
	{
		return FourteenNumeric;
	}

	@NotNull
	@Override
	public Alphanumeracy alphanumeracy()
	{
		return NoneAlphanumeric;
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

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@NotNull
	@Override
	public Set<String> formerActualNames()
	{
		return FormerNames;
	}
}
