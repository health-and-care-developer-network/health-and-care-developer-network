/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.separatedValueParsers.fieldParsers;

import org.jetbrains.annotations.NotNull;

public final class NonEmptyStringFieldParser extends AbstractMandatoryPrefixFieldParser<String>
{
	@NotNull
	@Override
	public String parse(final int fieldIndex, @NotNull final String fieldValue) throws CouldNotParseFieldException
	{
		validateIsNotEmpty(fieldIndex, fieldValue);
		return fieldValue;
	}
}
