/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1;

import uk.nhs.hcdn.barcodes.BarCodeFamily;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.BarCodeFamily;
import uk.nhs.hcdn.common.VariableArgumentsHelper;

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
