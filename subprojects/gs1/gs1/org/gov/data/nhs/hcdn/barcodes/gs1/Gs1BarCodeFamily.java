package org.gov.data.nhs.hcdn.barcodes.gs1;

import org.gov.data.nhs.hcdn.barcodes.BarCodeFamily;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static org.gov.data.nhs.hcdn.common.VariableArgumentsHelper.unmodifiableSetOf;

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
		this.formerActualNames = unmodifiableSetOf(formerActualNames);
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
