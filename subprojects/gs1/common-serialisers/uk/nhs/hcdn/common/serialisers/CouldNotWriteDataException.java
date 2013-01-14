/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class CouldNotWriteDataException extends AbstractDataException
{
	public CouldNotWriteDataException(@NotNull final IOException cause)
	{
		super(message(cause), cause);
	}

	public CouldNotWriteDataException(@NotNull final CouldNotEncodeDataException cause)
	{
		super(message(cause), cause);
	}

	private static String message(final Exception cause)
	{
		return format(ENGLISH, "Could not write data due to %1$s (%2$s)", cause.getClass().getSimpleName(), cause.getMessage());
	}
}
