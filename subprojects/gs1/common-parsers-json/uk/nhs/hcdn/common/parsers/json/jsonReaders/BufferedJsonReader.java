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

package uk.nhs.hcdn.common.parsers.json.jsonReaders;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.InvalidJsonException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class BufferedJsonReader implements JsonReader
{
	private static final int EndOfLineMagicCharacter = -1;
	private static final int NoPushbackCharacter = -2;
	private static final EndOfFileException CachedEndOfFileException = new EndOfFileException();

	@NotNull
	private final Reader reader;

	private int lastReadCharacter;
	private boolean lastReadCharacterPushedBacked;
	private long position;

	@SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
	public BufferedJsonReader(@NotNull final BufferedReader reader)
	{
		this.reader = reader;
		lastReadCharacter = NoPushbackCharacter;
		lastReadCharacterPushedBacked = false;
		position = 0L;
	}

	@NotNull
	@Override
	public String toString()
	{
		return format(ENGLISH, "%1$s(%2$s, %3$s, %4$s)", getClass().getSimpleName(), position, lastReadCharacter > EndOfLineMagicCharacter ? new String(new char[]{(char) lastReadCharacter}) : (Integer) lastReadCharacter, lastReadCharacterPushedBacked);
	}

	@Override
	public long position()
	{
		return position;
	}

	@Override
	public char readRequiredCharacter() throws InvalidJsonException
	{
		try
		{
			return readCharacter();
		}
		catch (EndOfFileException e)
		{
			throw new InvalidJsonException(e);
		}
	}

	@Override
	public char peekCharacter() throws EndOfFileException
	{
		final char c = readCharacter();
		pushBackLastCharacter();
		return c;
	}

	@Override
	public char readCharacter() throws EndOfFileException
	{
		if (lastReadCharacterPushedBacked)
		{
			lastReadCharacterPushedBacked = false;
		}
		else
		{
			try
			{
				lastReadCharacter = reader.read();
			}
			catch (IOException e)
			{
				lastReadCharacter = EndOfLineMagicCharacter;
				throw new EndOfFileException(e);
			}
		}
		if (lastReadCharacter == EndOfLineMagicCharacter)
		{
			throw CachedEndOfFileException;
		}
		position++;
		//noinspection NumericCastThatLosesPrecision
		return (char) lastReadCharacter;
	}

	@Override
	public void pushBackLastCharacter()
	{
		if (lastReadCharacterPushedBacked)
		{
			throw new IllegalStateException("Can not push back more than one character");
		}
		lastReadCharacterPushedBacked = true;
		position--;
	}
}
