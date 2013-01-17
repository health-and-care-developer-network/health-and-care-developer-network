/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.client.json.parseModes.literalParseModes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.client.json.charaterSets.CharacterSet;
import uk.nhs.hcdn.barcodes.gs1.client.json.jsonParseEventHandlers.JsonParseEventHandler;

import static uk.nhs.hcdn.barcodes.gs1.client.json.CharacterHelper.*;

public final class NullLiteralParseMode extends AbstractLiteralParseMode
{
	@NotNull
	public static final NullLiteralParseMode RootNullConstantParseMode = new NullLiteralParseMode(true, Whitespace);

	@NotNull
	public static final NullLiteralParseMode ObjectNullConstantParseMode = new NullLiteralParseMode(false, WhitespaceCommaCloseObject);

	@NotNull
	public static final NullLiteralParseMode ValueNullConstantParseMode = new NullLiteralParseMode(false, WhitespaceCommaCloseArray);

	private NullLiteralParseMode(final boolean isRootValue, @NotNull final CharacterSet validTerminatingCharacters)
	{
		super(isRootValue, validTerminatingCharacters, "null");
	}

	@Override
	protected void raiseEvent(@NotNull final JsonParseEventHandler jsonParseEventHandler)
	{
		jsonParseEventHandler.nullValue();
	}
}
