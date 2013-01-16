/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.exceptions;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractRethrowableException extends Exception
{
	@SuppressWarnings("NonFinalFieldOfException")
	private boolean stackTraceFilled;

	protected AbstractRethrowableException(@NonNls @NotNull final String message)
	{
		super(message);
		stackTraceFilled = false;
	}

	protected AbstractRethrowableException(@NonNls @NotNull final String message, @NotNull final Throwable cause)
	{
		super(message, cause);
		stackTraceFilled = false;
	}

	@Override
	public synchronized Throwable fillInStackTrace()
	{
		if (stackTraceFilled)
		{
			return this;
		}
		stackTraceFilled = true;
		return super.fillInStackTrace();
	}
}
