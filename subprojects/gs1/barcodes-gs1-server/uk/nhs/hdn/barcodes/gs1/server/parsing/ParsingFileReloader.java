/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hdn.barcodes.gs1.server.parsing;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.fileWatching.FailedToReloadException;
import uk.nhs.hdn.common.fileWatching.FileReloader;
import uk.nhs.hdn.common.parsers.CouldNotParseException;
import uk.nhs.hdn.common.parsers.ParserFactory;
import uk.nhs.hdn.common.parsers.sources.FileSource;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

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
