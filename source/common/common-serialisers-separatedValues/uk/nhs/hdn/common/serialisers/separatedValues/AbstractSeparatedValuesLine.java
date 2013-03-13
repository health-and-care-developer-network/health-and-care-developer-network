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
import uk.nhs.hdn.common.serialisers.CouldNotEncodeDataException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteDataException;
import uk.nhs.hdn.common.serialisers.separatedValues.fieldEscapers.FieldEscaper;

import java.io.Writer;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public abstract class AbstractSeparatedValuesLine extends AbstractToString implements SeparatedValuesLine
{
	protected AbstractSeparatedValuesLine()
	{
	}

	@Override
	public final void recordValue(final int index, @NotNull @NonNls final String rawValue)
	{
		if (index < 0)
		{
			throw new IllegalArgumentException(format(ENGLISH, "index %1$s is out of range (index < 0)", index));
		}
		if (isFieldAlreadyRecorded(index))
		{
			throw new IllegalArgumentException(format(ENGLISH, "index %1$s has already been recorded", index));
		}
		store(index, rawValue);
	}

	protected abstract boolean isFieldAlreadyRecorded(final int index);

	protected abstract void store(final int index, @Nullable final String rawValue);

	@Nullable
	protected abstract String load(final int index);

	protected abstract int size();

	@Override
	public final void writeLine(@NotNull final Writer writer, @NotNull final FieldEscaper fieldEscaper) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		final int size = size();
		if (size != 0)
		{
			writeField(writer, fieldEscaper, 0);

			for(int index = 1; index < size; index++)
			{
				fieldEscaper.writeFieldSeparator(writer);
				writeField(writer, fieldEscaper, index);
			}
		}

		fieldEscaper.writeLineEnding(writer);
	}

	private void writeField(final Writer writer, final FieldEscaper fieldEscaper, final int index) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		@Nullable final String field = load(index);
		final String actualField = field == null ? "" : field;
		fieldEscaper.escape(actualField, writer);
	}
}
