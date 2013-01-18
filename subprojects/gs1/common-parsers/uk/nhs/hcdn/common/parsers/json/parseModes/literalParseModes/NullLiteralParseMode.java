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

package uk.nhs.hcdn.common.parsers.json.parseModes.literalParseModes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.charaterSets.CharacterSet;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;

import static uk.nhs.hcdn.common.parsers.json.CharacterHelper.*;

public final class NullLiteralParseMode extends AbstractLiteralParseMode
{
	@NotNull
	public static final NullLiteralParseMode RootNullConstantParseMode = new NullLiteralParseMode(true, Whitespace);

	@NotNull
	public static final NullLiteralParseMode ObjectNullConstantParseMode = new NullLiteralParseMode(false, WhitespaceCommaCloseObject);

	@NotNull
	public static final NullLiteralParseMode ArrayNullConstantParseMode = new NullLiteralParseMode(false, WhitespaceCommaCloseArray);

	private NullLiteralParseMode(final boolean isRootValue, @NotNull final CharacterSet validTerminatingCharacters)
	{
		super(isRootValue, validTerminatingCharacters, "null");
	}

	@Override
	protected void raiseEvent(@NotNull final JsonParseEventHandler jsonParseEventHandler)
	{
		jsonParseEventHandler.literalNullValue();
	}
}
