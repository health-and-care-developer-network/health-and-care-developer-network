/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.server;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.fileWatching.FailedToReloadException;
import uk.nhs.hcdn.common.fileWatching.FileReloader;
import uk.nhs.hcdn.common.parsers.CouldNotParseException;
import uk.nhs.hcdn.common.parsers.ParserFactory;
import uk.nhs.hcdn.common.parsers.sources.FileSource;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.io.IOException;

public final class ParsingFileReloader extends AbstractToString implements FileReloader
{
	@NotNull
	private final ParserFactory parserFactory;

	@NotNull
	private final FileSource fileSource;

	public ParsingFileReloader(@NotNull final ParserFactory parserFactory, @NotNull final FileSource fileSource)
	{
		this.parserFactory = parserFactory;
		this.fileSource = fileSource;
	}

	@Override
	public void reload() throws FailedToReloadException
	{
		try
		{
			fileSource.load(parserFactory.parser());
		}
		catch (IOException | CouldNotParseException e)
		{
			throw new FailedToReloadException(e);
		}
	}
}
