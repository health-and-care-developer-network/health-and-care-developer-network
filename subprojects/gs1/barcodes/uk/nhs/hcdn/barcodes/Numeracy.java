/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes;

import org.jetbrains.annotations.NotNull;

public enum Numeracy
{
	NoneNumeric(0),
	EightNumeric(8),
	TwelveNumeric(12),
	ThirteenNumeric(13),
	FourteenNumeric(14),
	SeventyFourNumeric(74),
	Gs1DataMatrixNumeric(3116),
	;

	private final int numeracy;

	Numeracy(final int numeracy)
	{
		this.numeracy = numeracy;
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	public int numeracy()
	{
		return numeracy;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	@NotNull
	public String toString()
	{
		return Integer.toString(numeracy);
	}
}
