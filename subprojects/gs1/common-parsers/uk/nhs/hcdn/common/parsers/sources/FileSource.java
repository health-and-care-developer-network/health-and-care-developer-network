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

package uk.nhs.hcdn.common.parsers.sources;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.MillisecondsSince1970;
import uk.nhs.hcdn.common.parsers.CouldNotParseException;
import uk.nhs.hcdn.common.parsers.Parser;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;

public final class FileSource extends AbstractToString implements Source
{
	private static final int Guess = 4096;

	@NotNull
	private final Charset charset;

	@NotNull
	private final Path filePath;

	public FileSource(@NotNull final Charset charset, @NotNull final Path filePath)
	{
		this.charset = charset;
		this.filePath = filePath;
	}

	@Override
	public void load(@NotNull final Parser parser) throws IOException, CouldNotParseException
	{
		final File file = filePath.toFile();
		@MillisecondsSince1970 final long lastModified = file.lastModified();

		final InputStream inputStream = new FileInputStream(file);
		try
		{
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset), Guess);
			try
			{
				parser.parse(bufferedReader, lastModified);
			}
			finally
			{
				try
				{
					bufferedReader.close();
				}
				catch (IOException ignored)
				{
				}
			}
		}
		finally
		{
			try
			{
				inputStream.close();
			}
			catch (IOException ignored)
			{
			}
		}
	}
}
