/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import static uk.nhs.hcdn.barcodes.Alphanumeracy.FortyOneAlphanumeric;
import static uk.nhs.hcdn.barcodes.Alphanumeracy.NoneAlphanumeric;
import static uk.nhs.hcdn.barcodes.Directionality.NotOmnidirectional;
import static uk.nhs.hcdn.barcodes.Directionality.Omnidirectional;
import static uk.nhs.hcdn.barcodes.Numeracy.FourteenNumeric;
import static uk.nhs.hcdn.barcodes.Numeracy.SeventyFourNumeric;
import static uk.nhs.hcdn.barcodes.gs1.Gs1BarCodeFamily.GS1DataBar;
import static uk.nhs.hcdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumberFormat.*;

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
		this.globalTradeItemNumberFormats = EnumeratedVariableArgumentsHelper.unmodifiableSetOf(globalTradeItemNumberFormats);
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

	@Override
	public boolean canCarryApplicationIdentifiers()
	{
		return true;
	}

	@NotNull
	@Override
	public Directionality directionality()
	{
		return directionality;
	}
}
