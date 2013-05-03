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

package uk.nhs.hdn.common.parsers.convenientReaders;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class BufferedConvenientReader implements ConvenientReader
{
	@NotNull
	private final Reader reader;

	@SuppressWarnings("FieldMayBeFinal") // Inspection mis-reporting
	private long position;

	@SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
	public BufferedConvenientReader(@NotNull final Reader reader)
	{
		this.reader = reader instanceof BufferedReader ? reader : new BufferedReader(reader);
		position = 0L;
	}

	@NotNull
	@Override
	public String toString()
	{
		return format(ENGLISH, "%1$s(%2$s)", getClass().getSimpleName(), position);
	}

	@Override
	public long position()
	{
		return position;
	}

	@Override
	public char readCharacter() throws EndOfFileException
	{
		final int read;
		try
		{
			read = reader.read();
		}
		catch (IOException e)
		{
			throw new EndOfFileException(e);
		}
		if (read == EndOfLineMagicCharacter)
		{
			throw CachedEndOfFileException;
		}
		position++;
		//noinspection NumericCastThatLosesPrecision
		return (char) read;
	}
}
