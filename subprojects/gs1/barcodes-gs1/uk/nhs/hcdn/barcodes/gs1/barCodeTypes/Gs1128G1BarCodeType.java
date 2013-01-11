package uk.nhs.hcdn.barcodes.gs1.barCodeTypes;

import uk.nhs.hcdn.barcodes.Alphanumeracy;
import uk.nhs.hcdn.barcodes.Directionality;
import uk.nhs.hcdn.barcodes.Numeracy;
import uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumberFormat;
import uk.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily;
import uk.nhs.hcdn.common.EnumeratedVariableArgumentsHelper;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.VariableArgumentsHelper;

import java.util.Set;

import static uk.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily.GS1_128;

public enum Gs1128G1BarCodeType implements Gs1BarCodeType
{
	Gs1128,
	;

	@NonNls
	private static final Set<String> FormerNames = VariableArgumentsHelper.unmodifiableSetOf("UCC/EAN-128", "UCC-128", "EAN-128");

	// TODO: May not be a correct list
	private static final Set<GlobalTradeItemNumberFormat> GlobalTradeItemNumberFormats = EnumeratedVariableArgumentsHelper.unmodifiableSetOf(GlobalTradeItemNumberFormat.GTIN_12, GlobalTradeItemNumberFormat.GTIN_13, GlobalTradeItemNumberFormat.GTIN_14);

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
		return Numeracy.NoneNumeric;
	}

	@NotNull
	@Override
	public Alphanumeracy alphanumeracy()
	{
		return Alphanumeracy.FortyEightAlphanumeric;
	}

	@NotNull
	@Override
	public Directionality directionality()
	{
		return Directionality.NotOmnidirectional;
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@NotNull
	@Override
	public Set<GlobalTradeItemNumberFormat> gtins()
	{
		return GlobalTradeItemNumberFormats;
	}

	@Override
	public boolean canCarryApplicationIdentifiers()
	{
		return true;
	}
}
