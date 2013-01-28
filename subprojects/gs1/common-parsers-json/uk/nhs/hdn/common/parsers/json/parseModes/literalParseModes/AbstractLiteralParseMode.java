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
import uk.nhs.hdn.common.parsers.json.InvalidJsonException;
import uk.nhs.hdn.common.parsers.charaterSets.CharacterSet;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;
import uk.nhs.hdn.common.parsers.json.jsonReaders.EndOfFileException;
import uk.nhs.hdn.common.parsers.json.jsonReaders.JsonReader;
import uk.nhs.hdn.common.parsers.json.parseModes.AbstractParseMode;

import static uk.nhs.hdn.common.parsers.json.CharacterHelper.isNot;

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

	protected abstract void raiseEvent(@NotNull final JsonParseEventHandler jsonParseEventHandler) throws SchemaViolationInvalidJsonException;
}
