/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.exceptions.AbstractRethrowableException;

public final class BadRequestException extends AbstractRethrowableException
{
	public BadRequestException(@NonNls @NotNull final String message)
	{
		super(message);
	}

	public BadRequestException(@NonNls @NotNull final String message, @NotNull final Exception cause)
	{
		super(message, cause);
	}
}
