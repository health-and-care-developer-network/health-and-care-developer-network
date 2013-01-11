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
	public Gs1CompanyPrefix(@NotNull final Digits digits)
	{
		super(digits);
	}
}
