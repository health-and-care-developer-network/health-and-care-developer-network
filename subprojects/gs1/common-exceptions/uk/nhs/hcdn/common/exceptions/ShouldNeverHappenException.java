/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.exceptions;

import org.jetbrains.annotations.NotNull;

public final class ShouldNeverHappenException extends RuntimeException
{
	public ShouldNeverHappenException(@NotNull final Exception cause)
	{
		super("Exception occurred that should never happen", cause);
	}
}
