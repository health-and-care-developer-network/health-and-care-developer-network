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

package uk.nhs.hdn.common.parsers.fixedWidth.fixedWidthFields;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.CouldNotParseException;

import java.io.IOException;
import java.io.Reader;

public final class LeftPaddedFixedWidthField implements FixedWidthField
{
	private final int padding;
	private final int width;

	public LeftPaddedFixedWidthField(final char padding, final int width)
	{
		this.padding = (int) padding;
		this.width = width;
	}

	@NotNull
	@Override
	public String parse(@NotNull final Reader bufferedReader) throws IOException, CouldNotParseException
	{
		for(int characterIndex = 0; characterIndex < width; characterIndex++)
		{
			final int character = bufferedReader.read();
			if (character == -1)
			{
				throw new CouldNotParseException(0, "End of input within left padded fixed width field");
			}
			if (character == padding)
			{
				continue;
			}
			return readUnpaddedValue(bufferedReader, characterIndex, character);
		}
		return "";
	}

	@SuppressWarnings("NumericCastThatLosesPrecision")
	private String readUnpaddedValue(final Reader bufferedReader, final int characterIndex, final int character) throws IOException
	{
		final char[] unpaddedCharacters = new char[width - characterIndex];
		unpaddedCharacters[0] = (char) character;
		bufferedReader.read(unpaddedCharacters, 1, unpaddedCharacters.length - 1);
		return new String(unpaddedCharacters);
	}
}
