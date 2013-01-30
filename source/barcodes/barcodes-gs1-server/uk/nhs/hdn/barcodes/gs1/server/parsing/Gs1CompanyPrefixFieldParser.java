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

package uk.nhs.hdn.barcodes.gs1.server.parsing;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.barcodes.gs1.checkDigits.IncorrectNumberOfDigitsIllegalStateException;
import uk.nhs.hdn.barcodes.gs1.companyPrefixes.Gs1CompanyPrefix;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.AbstractMandatoryPrefixFieldParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.CouldNotParseFieldException;

import static uk.nhs.hdn.barcodes.gs1.companyPrefixes.Gs1CompanyPrefix.gs1CompanyPrefix;

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
