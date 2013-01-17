/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.client.json.jsonReaders;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.client.json.InvalidJsonException;
import uk.nhs.hcdn.common.exceptions.ShouldNeverHappenException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;

public final class BufferedJsonReader implements JsonReader
{
	private static final int EndOfLineMagicCharacter = -1;
	private static final EndOfFileException CachedEndOfFileException = new EndOfFileException();

	@NotNull
	private final PushbackReader reader;

	@SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
	public BufferedJsonReader(@NotNull final BufferedReader reader)
	{
		this.reader = new PushbackReader(reader, 2);
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
		final char character = readCharacter();
		try
		{
			reader.unread((int) character);
		}
		catch (IOException e)
		{
			throw new ShouldNeverHappenException(e);
		}
		return character;
	}

	@Override
	public char readCharacter() throws EndOfFileException
	{
		final int character;
		try
		{
			character = reader.read();
		}
		catch (IOException e)
		{
			throw new EndOfFileException(e);
		}
		if (character == EndOfLineMagicCharacter)
		{
			throw CachedEndOfFileException;
		}
		//noinspection NumericCastThatLosesPrecision
		return (char) character;
	}
}
