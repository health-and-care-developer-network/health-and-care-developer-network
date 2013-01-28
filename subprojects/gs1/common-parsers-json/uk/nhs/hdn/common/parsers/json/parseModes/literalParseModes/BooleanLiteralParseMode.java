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

package uk.nhs.hdn.common.parsers.json.parseModes.literalParseModes;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.charaterSets.CharacterSet;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;

import static uk.nhs.hdn.common.parsers.json.CharacterHelper.Whitespace;
import static uk.nhs.hdn.common.parsers.json.CharacterHelper.WhitespaceCommaCloseObject;

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
	protected void raiseEvent(@NotNull final JsonParseEventHandler jsonParseEventHandler) throws SchemaViolationInvalidJsonException
	{
		jsonParseEventHandler.literalBooleanValue(value);
	}
}
