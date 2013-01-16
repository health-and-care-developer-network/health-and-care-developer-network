/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.separatedValueParsers.fieldParsers;

import org.jetbrains.annotations.NotNull;

public interface FieldParser<V>
{
	boolean skipIfEmpty();

	@NotNull
	V parse(final int fieldIndex, @NotNull final String fieldValue) throws CouldNotParseFieldException;
}
