package uk.nhs.hcdn.common.exceptions;

import org.jetbrains.annotations.NotNull;

public final class ShouldNeverHappenException extends RuntimeException
{
	public ShouldNeverHappenException(@NotNull final Exception cause)
	{
		super("Exception occurred that should never happen", cause);
	}
}
