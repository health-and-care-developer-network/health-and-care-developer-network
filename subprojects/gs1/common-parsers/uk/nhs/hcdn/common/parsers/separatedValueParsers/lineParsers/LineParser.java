/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.separatedValueParsers.lineParsers;

import org.jetbrains.annotations.NotNull;

public interface LineParser<V>
{
	@NotNull
	V parse(final int lineIndex, @NotNull final Object[] parsedFields) throws CouldNotParseLineException;
}
