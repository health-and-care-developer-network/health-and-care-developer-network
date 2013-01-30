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

package uk.nhs.hdn.common.parsers.json.parseModes.stringParseModes;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.charaterSets.CharacterSet;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hdn.common.parsers.json.parseModes.ParseMode;

import static uk.nhs.hdn.common.parsers.json.CharacterHelper.WhitespaceColonArray;

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
