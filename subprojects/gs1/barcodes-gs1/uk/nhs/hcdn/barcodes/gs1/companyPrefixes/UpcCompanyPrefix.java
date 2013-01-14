/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.companyPrefixes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digits;

import static uk.nhs.hcdn.barcodes.Digit.Zero;

public final class UpcCompanyPrefix extends AbstractCompanyPrefix
{
	private static final Digits DigitsZero = new Digits(false, Zero);

	public UpcCompanyPrefix(@NotNull final Digits digits)
	{
		super(digits);
	}

	@NotNull
	public Gs1CompanyPrefix toGs1CompanyPrefix()
	{
		return new Gs1CompanyPrefix(DigitsZero.add(digits));
	}
}
