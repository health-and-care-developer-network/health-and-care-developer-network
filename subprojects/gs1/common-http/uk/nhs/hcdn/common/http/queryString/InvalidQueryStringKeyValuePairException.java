/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.queryString;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class InvalidQueryStringKeyValuePairException extends Exception
{
	public InvalidQueryStringKeyValuePairException(@NotNull final String key, @NotNull final String value, @NonNls @NotNull final String because)
	{
		super(format(ENGLISH, "Invalid query string key-value pair (%1$s => %2$s) because %3$s", key, value, because));
	}

	public InvalidQueryStringKeyValuePairException(@NotNull final String key, @NotNull final String value, @NotNull final Exception cause)
	{
		super(format(ENGLISH, "Invalid query string key-value pair (%1$s => %2$s) because of exception %3$s", key, value, cause.getMessage()), cause);
	}
}
