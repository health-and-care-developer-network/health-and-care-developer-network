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
import uk.nhs.hdn.common.EnumeratedVariableArgumentsHelper;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.VariableArgumentsHelper;

import java.util.Set;

import static uk.nhs.hdn.barcodes.gs1.Gs1BarCodeFamily.GS1_128;

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
