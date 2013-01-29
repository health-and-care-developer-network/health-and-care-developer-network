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

package uk.nhs.hdn.barcodes.gs1.barCodeTypes;

import uk.nhs.hdn.barcodes.Alphanumeracy;
import uk.nhs.hdn.barcodes.Directionality;
import uk.nhs.hdn.barcodes.Numeracy;
import uk.nhs.hdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumberFormat;
import uk.nhs.hdn.barcodes.gs1.Gs1BarCodeFamily;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.EnumeratedVariableArgumentsHelper;

import java.util.Set;

import static java.util.Collections.emptySet;
import static uk.nhs.hdn.barcodes.Alphanumeracy.NoneAlphanumeric;
import static uk.nhs.hdn.barcodes.Numeracy.*;
import static uk.nhs.hdn.barcodes.Directionality.Omnidirectional;
import static uk.nhs.hdn.barcodes.gs1.Gs1BarCodeFamily.EANUPC;
import static uk.nhs.hdn.barcodes.gs1.keys.globalTradeItemNumbers.GlobalTradeItemNumberFormat.*;

public enum EanUpcGs1BarCodeType implements Gs1BarCodeType
{
	UpcAVersion("UPC-A Version", TwelveNumeric, GTIN_12),
	Ean13Version("EAN-13 Version", ThirteenNumeric, GTIN_13),
	UpcEVersion("UPC-E Version", TwelveNumeric, GTIN_12),
	Ean8Version("EAN-8 Version", EightNumeric, GTIN_8),
	;

	private static final Set<String> FormerNames = emptySet();

	@NotNull
	private final String actualName;

	@NotNull
	private final Set<GlobalTradeItemNumberFormat> globalTradeItemNumberFormats;

	@NotNull
	private final Numeracy numeracy;

	EanUpcGs1BarCodeType(@NonNls @NotNull final String actualName, @NotNull final Numeracy numeracy, @NotNull final GlobalTradeItemNumberFormat... globalTradeItemNumberFormats)
	{
		this.actualName = actualName;
		this.globalTradeItemNumberFormats = EnumeratedVariableArgumentsHelper.unmodifiableSetOf(globalTradeItemNumberFormats);
		this.numeracy = numeracy;
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
		return EANUPC;
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
		return NoneAlphanumeric;
	}

	@NotNull
	@Override
	public Directionality directionality()
	{
		return Omnidirectional;
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
		return false;
	}
}
