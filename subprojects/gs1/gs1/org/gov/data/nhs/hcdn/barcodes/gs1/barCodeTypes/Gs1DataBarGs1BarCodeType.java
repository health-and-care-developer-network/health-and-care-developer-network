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
import static org.gov.data.nhs.hcdn.barcodes.Alphanumeracy.FortyOneAlphanumeric;
import static org.gov.data.nhs.hcdn.barcodes.Alphanumeracy.NoneAlphanumeric;
import static org.gov.data.nhs.hcdn.barcodes.Directionality.NotOmnidirectional;
import static org.gov.data.nhs.hcdn.barcodes.Directionality.Omnidirectional;
import static org.gov.data.nhs.hcdn.barcodes.Numeracy.FourteenNumeric;
import static org.gov.data.nhs.hcdn.barcodes.Numeracy.SeventyFourNumeric;
import static org.gov.data.nhs.hcdn.barcodes.gs1.GlobalTradeItemNumberFormat.*;
import static org.gov.data.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily.GS1DataBar;
import static org.gov.data.nhs.hcdn.common.EnumeratedVariableArgumentsHelper.unmodifiableSetOf;

@SuppressWarnings("UnusedDeclaration")
public enum Gs1DataBarGs1BarCodeType implements Gs1BarCodeType
{
	Gs1DataBarOmnidirectional("GS1 DataBar Omindirectional", Omnidirectional, FourteenNumeric, NoneAlphanumeric, GTIN_8, GTIN_12, GTIN_13, GTIN_14),
	Gs1DataBarStackedOmnidirectional("GS1 DataBar Stacked Omindirectional", Omnidirectional, FourteenNumeric, NoneAlphanumeric, GTIN_8, GTIN_12, GTIN_13, GTIN_14),
	Gs1DataBarExpanded("GS1 DataBar Expanded", Omnidirectional, SeventyFourNumeric, FortyOneAlphanumeric, GTIN_8, GTIN_12, GTIN_13, GTIN_14),
	Gs1DataBarExpandedStacked("GS1 DataBar Expanded Stacked", Omnidirectional, SeventyFourNumeric, FortyOneAlphanumeric, GTIN_8, GTIN_12, GTIN_13, GTIN_14),
	Gs1DataBarLimited("GS1 DataBar Limited", NotOmnidirectional, FourteenNumeric, NoneAlphanumeric, GTIN_8, GTIN_12, GTIN_13, GTIN_14),
	Gs1DataBarTruncated("GS1 DataBar Truncated", NotOmnidirectional, FourteenNumeric, NoneAlphanumeric, GTIN_8, GTIN_12, GTIN_13, GTIN_14),
	Gs1DataBarStacked("GS1 DataBar Stacked", NotOmnidirectional, FourteenNumeric, NoneAlphanumeric, GTIN_8, GTIN_12, GTIN_13, GTIN_14),
	;

	private static final Set<String> FormerNames = emptySet();

	@NotNull
	private final String actualName;

	@NotNull
	private final Set<GlobalTradeItemNumberFormat> globalTradeItemNumberFormats;

	@NotNull
	private final Directionality directionality;

	@NotNull
	private final Numeracy numeracy;

	@NotNull
	private final Alphanumeracy alphanumeracy;

	Gs1DataBarGs1BarCodeType(@NotNull @NonNls final String actualName, @NotNull final Directionality directionality, @NotNull final Numeracy numeracy, @NotNull final Alphanumeracy alphanumeracy, @NotNull final GlobalTradeItemNumberFormat... globalTradeItemNumberFormats)
	{
		this.numeracy = numeracy;
		this.alphanumeracy = alphanumeracy;
		if (globalTradeItemNumberFormats.length == 0)
		{
			throw new IllegalArgumentException("globalTradeItemNumberFormats can not be empty");
		}
		this.actualName = actualName;
		this.globalTradeItemNumberFormats = unmodifiableSetOf(globalTradeItemNumberFormats);
		this.directionality = directionality;
	}

	@NonNls
	@NotNull
	@Override
	public String actualName()
	{
		return actualName;
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
		return GS1DataBar;
	}

	@NotNull
	@Override
	public Numeracy numeracy()
	{
		return numeracy;
	}

	@NotNull
	@Override
	public Alphanumeracy alphanumeracy()
	{
		return alphanumeracy;
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@NotNull
	@Override
	public Set<GlobalTradeItemNumberFormat> gtins()
	{
		return globalTradeItemNumberFormats;
	}

	@NotNull
	@Override
	public Directionality directionality()
	{
		return directionality;
	}
}
