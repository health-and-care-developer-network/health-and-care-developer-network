/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.separatedValueParsers.fieldParsers;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractNonMandatoryPrefixFieldParser<V> implements FieldParser<V>
{
	@Override
	public final boolean skipIfEmpty()
	{
		return true;
	}

	protected static void guardIsNotEmpty(@NotNull final String fieldValue)
	{
		if (fieldValue.isEmpty())
		{
			throw new IllegalStateException("Empty field values should not be parsed");
		}
	}
}
