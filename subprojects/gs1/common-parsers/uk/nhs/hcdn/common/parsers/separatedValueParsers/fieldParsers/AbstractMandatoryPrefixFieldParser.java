/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.separatedValueParsers.fieldParsers;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractMandatoryPrefixFieldParser<V> implements FieldParser<V>
{
	@Override
	public final boolean skipIfEmpty()
	{
		return false;
	}

	protected static void validateIsNotEmpty(final int fieldIndex, @NotNull final String fieldValue) throws CouldNotParseFieldException
	{
		if (fieldValue.isEmpty())
		{
			throw new CouldNotParseFieldException(fieldIndex, fieldValue, "empty field values are not permitted");
		}
	}
}
