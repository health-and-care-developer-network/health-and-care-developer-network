/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.client.json.parseModes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.client.json.InvalidJsonException;
import uk.nhs.hcdn.barcodes.gs1.client.json.charaterSets.CharacterSet;
import uk.nhs.hcdn.barcodes.gs1.client.json.jsonReaders.EndOfFileException;
import uk.nhs.hcdn.barcodes.gs1.client.json.jsonReaders.JsonReader;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

public abstract class AbstractParseMode extends AbstractToString implements ParseMode
{
	private final boolean isNotRootValue;

	@NotNull
	private final CharacterSet validTerminatingCharacters;

	protected AbstractParseMode(final boolean isRootValue, @NotNull final CharacterSet validTerminatingCharacters)
	{
		isNotRootValue = !isRootValue;
		this.validTerminatingCharacters = validTerminatingCharacters;
	}

	protected final void guardNextCharacter(@NotNull final JsonReader jsonReader) throws InvalidJsonException
	{
		final char nextCharacter;
		try
		{
			nextCharacter = jsonReader.peekCharacter();
		}
		catch (EndOfFileException e)
		{
			if (isNotRootValue)
			{
				throw new InvalidJsonException(e);
			}
			return;
		}
		if (validTerminatingCharacters.doesNotContain(nextCharacter))
		{
			throw new InvalidJsonException("literal value not correctly terminated");
		}
	}
}
