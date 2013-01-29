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

public final class CommaSeparatedFieldEscaper extends AbstractFieldEscaper
{
	private static final int DoubleQuote = (int) '"';

	@NotNull
	public static final FieldEscaper CommaSeparatedFieldEscaperInstance = new CommaSeparatedFieldEscaper();

	@SuppressWarnings("MagicCharacter")
	private CommaSeparatedFieldEscaper()
	{
		super(',', '\r', '\n');
	}

	@Override
	public void escape(@NotNull final String field, @NotNull final Writer writer) throws CouldNotWriteDataException
	{
		try
		{
			writer.write(DoubleQuote);
			for (int index = 0; index < field.length(); index++)
			{
				final int character = (int) field.charAt(index);
				if (character == DoubleQuote)
				{
					writer.write(DoubleQuote);
				}
				writer.write(character);
			}
			writer.write(DoubleQuote);
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}
}
