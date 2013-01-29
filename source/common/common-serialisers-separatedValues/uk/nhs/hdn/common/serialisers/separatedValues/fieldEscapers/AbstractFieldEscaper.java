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

import static java.util.Arrays.copyOf;

public abstract class AbstractFieldEscaper implements FieldEscaper
{
	private final int fieldSeparator;
	@NotNull
	private final char[] endOfLineSequence;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	protected AbstractFieldEscaper(final char fieldSeparator, @NotNull final char... endOfLineSequence)
	{
		final int length = endOfLineSequence.length;
		if (length == 0)
		{
			throw new IllegalArgumentException("endOfLineSequence can not be empty");
		}
		this.fieldSeparator = (int) fieldSeparator;
		this.endOfLineSequence = copyOf(endOfLineSequence, length);
	}

	@Override
	public final void writeFieldSeparator(@NotNull final Writer writer) throws CouldNotWriteDataException
	{
		try
		{
			writer.write(fieldSeparator);
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}

	@Override
	public final void writeLineEnding(@NotNull final Writer writer) throws CouldNotWriteDataException
	{
		try
		{
			writer.write(endOfLineSequence);
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}
}
