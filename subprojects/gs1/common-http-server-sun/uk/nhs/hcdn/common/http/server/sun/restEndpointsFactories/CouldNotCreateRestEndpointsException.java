/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpointsFactories;

import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class CouldNotCreateRestEndpointsException extends Exception
{
	public CouldNotCreateRestEndpointsException(@NotNull final Exception cause)
	{
		super(format(ENGLISH, "Could not create rest endpoints because of exception %1$s", cause.getMessage()), cause);
	}
}
