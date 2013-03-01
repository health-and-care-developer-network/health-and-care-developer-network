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

package uk.nhs.hdn.common.serialisers.separatedValues.fieldEscapers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.serialisers.CouldNotWriteDataException;

import java.io.IOException;
import java.io.Writer;

// This class converts invalid control code sequences to white space
public final class SanitisingTabSeparatedFieldEscaper extends AbstractFieldEscaper
{
	@NotNull
	public static final FieldEscaper SanitisingTabSeparatedFieldEscaperInstance = new SanitisingTabSeparatedFieldEscaper();

	private static final int ReplacementCharacter = (int) '\uFFFD';

	@SuppressWarnings("MagicCharacter")
	private SanitisingTabSeparatedFieldEscaper()
	{
		super('\t', '\n');
	}

	@Override
	public void escape(@NotNull final String field, @NotNull final Writer writer) throws CouldNotWriteDataException
	{
		for(int index = 0; index < field.length(); index++)
		{
			final int character = (int) field.charAt(index);
			final int actualCharacter = guardCharacter(character);
			try
			{
				writer.write(actualCharacter);
			}
			catch (IOException e)
			{
				throw new CouldNotWriteDataException(e);
			}
		}
	}

	@SuppressWarnings("MagicCharacter")
	private static int guardCharacter(final int character)
	{
		switch(character)
		{
			case '\t':
			case '\r':
			case '\n':
				return ReplacementCharacter;
			default:
				return character;
		}
	}
}
