/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1;

import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.gs1Prefixes.Gs1Prefix;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import org.jetbrains.annotations.NotNull;

public final class Gs1CompanyPrefixAndItem extends AbstractToString
{
	public static final int Gs1CompanyPrefixAndItemNumberOfDigits = 12;

	@NotNull
	private final Digits digits;

	public Gs1CompanyPrefixAndItem(@NotNull final Digits digits)
	{
		this.digits = digits;
	}

	@NotNull
	public Gs1Prefix gs1Prefix()
	{
		return new Gs1Prefix(digits.firstThree());
	}
}
