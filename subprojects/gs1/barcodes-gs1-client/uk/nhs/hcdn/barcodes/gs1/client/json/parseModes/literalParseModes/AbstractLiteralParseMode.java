/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.client.json.parseModes.literalParseModes;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.client.json.InvalidJsonException;
import uk.nhs.hcdn.barcodes.gs1.client.json.charaterSets.CharacterSet;
import uk.nhs.hcdn.barcodes.gs1.client.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hcdn.barcodes.gs1.client.json.jsonReaders.EndOfFileException;
import uk.nhs.hcdn.barcodes.gs1.client.json.jsonReaders.JsonReader;
import uk.nhs.hcdn.barcodes.gs1.client.json.parseModes.AbstractParseMode;

import static uk.nhs.hcdn.barcodes.gs1.client.json.CharacterHelper.isNot;

public abstract class AbstractLiteralParseMode extends AbstractParseMode
{
	@NotNull
	private final char[] word;
	private final int length;

	protected AbstractLiteralParseMode(final boolean isRootValue, @NotNull final CharacterSet validTerminatingCharacters, @NonNls @NotNull final String asciiWord)
	{
		super(isRootValue, validTerminatingCharacters);
		word = asciiWord.toCharArray();
		length = word.length;
	}

	@Override
	public final void parse(@NotNull final JsonParseEventHandler jsonParseEventHandler, @NotNull final JsonReader jsonReader) throws InvalidJsonException
	{
		for(int index = 0; index < length; index++)
		{
			try
			{
				if (isNot(jsonReader.readCharacter(), word[index]))
				{
					throw new InvalidJsonException("unexpected character in constant word");
				}
			}
			catch (EndOfFileException e)
			{
				throw new InvalidJsonException(e);
			}
		}
		guardNextCharacter(jsonReader);
		raiseEvent(jsonParseEventHandler);
	}

	protected abstract void raiseEvent(@NotNull final JsonParseEventHandler jsonParseEventHandler);
}
