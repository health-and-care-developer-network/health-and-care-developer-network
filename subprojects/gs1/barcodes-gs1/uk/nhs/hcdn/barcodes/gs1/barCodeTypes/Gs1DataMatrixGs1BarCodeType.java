package uk.nhs.hcdn.barcodes.gs1.barCodeTypes;

import uk.nhs.hcdn.barcodes.Alphanumeracy;
import uk.nhs.hcdn.barcodes.Directionality;
import uk.nhs.hcdn.barcodes.Numeracy;
import uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumberFormat;
import uk.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.EnumeratedVariableArgumentsHelper;

import java.util.Set;

import static java.util.Collections.emptySet;
import static uk.nhs.hcdn.barcodes.Alphanumeracy.Gs1DataMatrixAlphanumeric;
import static uk.nhs.hcdn.barcodes.Directionality.TwoDimensional;
import static uk.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily.GS1DataMatrix;

public enum Gs1DataMatrixGs1BarCodeType implements Gs1BarCodeType
{
	Ecc200,
	;

	private static final Set<String> FormerNames = emptySet();

	// TODO: Might be an incorrect set
	private static final Set<GlobalTradeItemNumberFormat> GlobalTradeItemNumberFormats = EnumeratedVariableArgumentsHelper.unmodifiableSetOf(GlobalTradeItemNumberFormat.GTIN_12, GlobalTradeItemNumberFormat.GTIN_13);

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
		return Numeracy.Gs1DataMatrixNumeric;
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

	@Override
	public boolean canCarryApplicationIdentifiers()
	{
		return true;
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@NotNull
	@Override
	public Set<String> formerActualNames()
	{
		return FormerNames;
	}
}
