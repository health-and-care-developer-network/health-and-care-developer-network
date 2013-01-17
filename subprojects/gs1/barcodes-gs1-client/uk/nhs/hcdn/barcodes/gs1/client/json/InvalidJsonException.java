/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.client.json;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.client.json.jsonReaders.EndOfFileException;
import uk.nhs.hcdn.common.exceptions.AbstractRethrowableException;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class InvalidJsonException extends AbstractRethrowableException
{
	public InvalidJsonException(@NonNls @NotNull final String because)
	{
		super(message(because));
	}

	public InvalidJsonException(@NotNull final EndOfFileException prematureEndOfFile)
	{
		super("Invalid JSON because of premature end of file", prematureEndOfFile);
	}

	public InvalidJsonException(@NonNls @NotNull final String because, @NotNull final Exception cause)
	{
		super(message(because), cause);
	}

	private static String message(final String because)
	{
		return format(ENGLISH, "Invalid JSON because %1$s", because);
	}
}
