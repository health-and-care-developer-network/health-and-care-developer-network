/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.separatedValueParsers.fieldParsers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class CouldNotParseFieldException extends Exception
{
	public CouldNotParseFieldException(final int fieldIndex, @NonNls @NotNull final String fieldValue, @NonNls @NotNull final String because)
	{
		super(format(ENGLISH, "Could not parse field index %1$s with value %1$s because %3$s", fieldIndex, fieldValue, because));
	}

	public CouldNotParseFieldException(final int fieldIndex, @NotNull final String fieldValue, @NotNull final Exception cause)
	{
		super(format(ENGLISH, "Could not parse field index %1$s with value %1$s because of exception %3$s", fieldIndex, fieldValue, cause.getMessage()), cause);
	}
}
