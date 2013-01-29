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

package uk.nhs.hdn.barcodes.gs1;

import uk.nhs.hdn.barcodes.BarCodeFamily;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.VariableArgumentsHelper;

import java.util.Set;

public enum Gs1BarCodeFamily implements BarCodeFamily
{
	EANUPC("EAN/UPC"),
	GS1DataBar("GS1 DataBar", "RSS"),
	GS1_128("GS1-128", "UCC/EAN-128", "EAN-128"),
	ITF_14("ITF-14"),
	GS1DataMatrix("GS1 DataMatrix"),
	;

	@NotNull
	private final String actualName;

	@NotNull
	private final Set<String> formerActualNames;

	Gs1BarCodeFamily(@NotNull @NonNls final String actualName, @NotNull @NonNls final String... formerActualNames)
	{
		this.actualName = actualName;
		this.formerActualNames = VariableArgumentsHelper.unmodifiableSetOf(formerActualNames);
	}

	@NonNls
	@Override
	@NotNull
	public String actualName()
	{
		return actualName;
	}

	@Override
	@NotNull
	public Set<String> formerActualNames()
	{
		return formerActualNames;
	}
}
