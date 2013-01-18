/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.parseModes.stringParseModes;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.charaterSets.CharacterSet;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hcdn.common.parsers.json.parseModes.ParseMode;

import static uk.nhs.hcdn.common.parsers.json.CharacterHelper.WhitespaceColonArray;

public final class KeyStringParseMode extends AbstractStringParseMode
{
	@NotNull
	public static final ParseMode KeyStringParseModeInstance = new KeyStringParseMode(WhitespaceColonArray);

	private KeyStringParseMode(@NotNull final CharacterSet validTerminatingCharacters)
	{
		super(false, validTerminatingCharacters);
	}

	@Override
	protected void useStringValue(@NotNull final JsonParseEventHandler jsonParseEventHandler, @NonNls @NotNull final String value)
	{
		jsonParseEventHandler.key(value);
	}
}
