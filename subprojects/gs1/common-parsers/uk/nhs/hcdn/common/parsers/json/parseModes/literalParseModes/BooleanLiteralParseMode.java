/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.parseModes.literalParseModes;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.charaterSets.CharacterSet;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;

import static uk.nhs.hcdn.common.parsers.json.CharacterHelper.Whitespace;
import static uk.nhs.hcdn.common.parsers.json.CharacterHelper.WhitespaceCommaCloseObject;

public final class BooleanLiteralParseMode extends AbstractLiteralParseMode
{
	private static final String True = "true";
	private static final String False = "false";

	@NotNull
	public static final BooleanLiteralParseMode RootTrueBooleanConstantParseMode = new BooleanLiteralParseMode(true, Whitespace, True, true);

	@NotNull
	public static final BooleanLiteralParseMode ObjectTrueBooleanConstantParseMode = new BooleanLiteralParseMode(false, WhitespaceCommaCloseObject, True, true);

	@NotNull
	public static final BooleanLiteralParseMode ArrayTrueBooleanConstantParseMode = new BooleanLiteralParseMode(false, WhitespaceCommaCloseObject, True, true);

	@NotNull
	public static final BooleanLiteralParseMode RootFalseBooleanConstantParseMode = new BooleanLiteralParseMode(true, Whitespace, False, false);

	@NotNull
	public static final BooleanLiteralParseMode ObjectFalseBooleanConstantParseMode = new BooleanLiteralParseMode(false, WhitespaceCommaCloseObject, False, false);

	@NotNull
	public static final BooleanLiteralParseMode ArrayFalseBooleanConstantParseMode = new BooleanLiteralParseMode(false, WhitespaceCommaCloseObject, False, false);

	private final boolean value;

	private BooleanLiteralParseMode(final boolean isRootValue, @NotNull final CharacterSet validTerminatingCharacters, @NonNls @NotNull final String word, final boolean value)
	{
		super(isRootValue, validTerminatingCharacters, word);
		this.value = value;
	}

	@Override
	protected void raiseEvent(@NotNull final JsonParseEventHandler jsonParseEventHandler)
	{
		jsonParseEventHandler.literalBooleanValue(value);
	}
}
