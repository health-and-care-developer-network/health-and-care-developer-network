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

package uk.nhs.hdn.common.serialisers.separatedValues;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;
import uk.nhs.hdn.common.serialisers.CouldNotEncodeDataException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteDataException;
import uk.nhs.hdn.common.serialisers.separatedValues.fieldEscapers.FieldEscaper;

import java.io.Writer;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class SimpleSeparatedValuesLine extends AbstractToString implements SeparatedValuesLine
{
	@ExcludeFromToString
	private final int length;
	@NotNull
	private final String[] fields;

	public SimpleSeparatedValuesLine(final int length)
	{
		this.length = length;
		fields = new String[length];
	}

	@Override
	public void recordValue(final int index, @NotNull @NonNls final String rawValue)
	{
		if (index >= length || length < 0)
		{
			throw new IllegalArgumentException(format(ENGLISH, "index %1$s is out of range (0 < index < %2$s)", index, length));
		}
		if (fields[index] != null)
		{
			throw new IllegalArgumentException(format(ENGLISH, "index %1$s has already been recorded", index));
		}
		fields[index] = rawValue;
	}

	@Override
	public void writeLine(@NotNull final Writer writer, @NotNull final FieldEscaper fieldEscaper) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		if (length != 0)
		{
			writeField(writer, fieldEscaper, 0);

			for(int index = 1; index < length; index++)
			{
				fieldEscaper.writeFieldSeparator(writer);
				writeField(writer, fieldEscaper, index);
			}
		}

		fieldEscaper.writeLineEnding(writer);
	}

	private void writeField(final Writer writer, final FieldEscaper fieldEscaper, final int index) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		@Nullable final String field = fields[index];
		final String actualField = field == null ? "" : field;
		fieldEscaper.escape(actualField, writer);
	}
}
