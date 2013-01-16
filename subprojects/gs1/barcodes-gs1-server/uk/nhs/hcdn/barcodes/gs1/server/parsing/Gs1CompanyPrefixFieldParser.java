/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.server.parsing;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.IncorrectNumberOfDigitsIllegalStateException;
import uk.nhs.hcdn.barcodes.gs1.companyPrefixes.Gs1CompanyPrefix;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.fieldParsers.AbstractMandatoryPrefixFieldParser;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.fieldParsers.CouldNotParseFieldException;

import static uk.nhs.hcdn.barcodes.gs1.companyPrefixes.Gs1CompanyPrefix.gs1CompanyPrefix;

public final class Gs1CompanyPrefixFieldParser extends AbstractMandatoryPrefixFieldParser<Gs1CompanyPrefix>
{
	@NotNull
	@Override
	public Gs1CompanyPrefix parse(final int fieldIndex, @NotNull final String fieldValue) throws CouldNotParseFieldException
	{
		validateIsNotEmpty(fieldIndex, fieldValue);
		try
		{
			return gs1CompanyPrefix(fieldValue);
		}
		catch (IllegalArgumentException | IncorrectNumberOfDigitsIllegalStateException e)
		{
			throw new CouldNotParseFieldException(fieldIndex, fieldValue, e);
		}
	}
}
