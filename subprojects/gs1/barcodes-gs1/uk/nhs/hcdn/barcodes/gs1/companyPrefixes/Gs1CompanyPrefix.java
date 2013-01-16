/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.companyPrefixes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.Digits;

/*
	Searches
		http://gepir.gs1.org/v32/xx/search_by_name.aspx?Lang=en-US
		http://gepir.gs1.org/v32/xx/default.aspx?Lang=en-US
 */
public final class Gs1CompanyPrefix extends AbstractCompanyPrefix
{
	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static Gs1CompanyPrefix gs1CompanyPrefix(@NotNull final CharSequence digits)
	{
		return new Gs1CompanyPrefix(Digits.digits(digits));
	}

	public Gs1CompanyPrefix(@NotNull final Digits digits)
	{
		super(digits);
	}
}
