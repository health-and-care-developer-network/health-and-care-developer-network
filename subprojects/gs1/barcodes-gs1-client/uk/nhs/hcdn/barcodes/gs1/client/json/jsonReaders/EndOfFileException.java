/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.client.json.jsonReaders;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.exceptions.AbstractRethrowableException;

import java.io.IOException;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class EndOfFileException extends AbstractRethrowableException
{
	public EndOfFileException()
	{
		super("End of file");
	}

	public EndOfFileException(@NotNull final IOException cause)
	{
		super(format(ENGLISH, "Premature of file caused by IOException (%1$s)", cause));
	}
}
