/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.parseModes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.InvalidJsonException;
import uk.nhs.hcdn.common.parsers.json.charaterSets.CharacterSet;
import uk.nhs.hcdn.common.parsers.json.jsonReaders.EndOfFileException;
import uk.nhs.hcdn.common.parsers.json.jsonReaders.JsonReader;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

public abstract class AbstractParseMode extends AbstractToString implements ParseMode
{
	protected final boolean isNotRootValue;

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
			nextCharacter = peekNextCharacter(jsonReader);
		}
		catch (EndOfFileException ignored)
		{
			return;
		}
		guardIsTerminatingCharacter(nextCharacter);
	}

	protected final char peekNextCharacter(@NotNull final JsonReader jsonReader) throws InvalidJsonException, EndOfFileException
	{
		try
		{
			return jsonReader.peekCharacter();
		}
		catch (EndOfFileException e)
		{
			if (isNotRootValue)
			{
				throw new InvalidJsonException(e);
			}
			throw e;
		}
	}

	protected final void guardIsTerminatingCharacter(final char nextCharacter) throws InvalidJsonException
	{
		if (isTerminatingCharacter(nextCharacter))
		{
			throw new InvalidJsonException("literal value not correctly terminated");
		}
	}

	protected final boolean isTerminatingCharacter(final char nextCharacter)
	{
		return validTerminatingCharacters.doesNotContain(nextCharacter);
	}
}
