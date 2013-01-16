/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.queryString;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public class InvalidQueryStringException extends Exception
{
	public InvalidQueryStringException(@NonNls @NotNull final String because)
	{
		super(format(ENGLISH, "Invalid query string because %1$s", because));
	}
}
