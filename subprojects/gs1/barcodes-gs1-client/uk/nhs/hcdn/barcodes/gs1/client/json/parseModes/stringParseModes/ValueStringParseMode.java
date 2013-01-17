/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.client.json.parseModes.stringParseModes;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.client.json.charaterSets.CharacterSet;
import uk.nhs.hcdn.barcodes.gs1.client.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hcdn.barcodes.gs1.client.json.parseModes.ParseMode;

import static uk.nhs.hcdn.barcodes.gs1.client.json.CharacterHelper.*;

public final class ValueStringParseMode extends AbstractStringParseMode
{
	@NotNull
	public static final ParseMode RootValueStringParseModeInstance = new ValueStringParseMode(true, Whitespace);

	@NotNull
	public static final ParseMode ObjectValueStringParseModeInstance = new ValueStringParseMode(false, WhitespaceCommaCloseObject);

	@NotNull
	public static final ParseMode ArrayValueStringParseModeInstance = new ValueStringParseMode(false, WhitespaceCommaCloseArray);

	private ValueStringParseMode(final boolean isRootValue, @NotNull final CharacterSet validTerminatingCharacters)
	{
		super(isRootValue, validTerminatingCharacters);
	}

	@Override
	protected void useStringValue(@NotNull final JsonParseEventHandler jsonParseEventHandler, @NonNls @NotNull final String value)
	{
		jsonParseEventHandler.stringValue(value);
	}
}
