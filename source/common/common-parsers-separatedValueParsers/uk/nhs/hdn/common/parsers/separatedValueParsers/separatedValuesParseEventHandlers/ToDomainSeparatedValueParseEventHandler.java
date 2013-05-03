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

package uk.nhs.hdn.common.parsers.separatedValueParsers.separatedValuesParseEventHandlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.CouldNotParseFieldException;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.FieldParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.lineParsers.CouldNotParseLineException;
import uk.nhs.hdn.common.parsers.separatedValueParsers.lineParsers.LineParser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.linesParsers.LinesParser;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;

import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;

public final class ToDomainSeparatedValueParseEventHandler<V, L> extends AbstractToString implements SeparatedValueParseEventHandler<ToDomainSeparatedValueParseEventHandler<V, L>.ToDomainSeparatedValueParseEventHandlerState>
{
	private final FieldParser<?>[] fieldParsers;
	@ExcludeFromToString
	private final int maximumNumberOfFields;
	@NotNull
	private final LinesParser<V, L> linesParser;
	@NotNull
	private final LineParser<V, L> lineParser;

	@SuppressWarnings("AssignmentToNull")
	public ToDomainSeparatedValueParseEventHandler(@NotNull final LinesParser<V, L> linesParser, @NotNull final LineParser<V, L> lineParser, @NotNull final FieldParser<?>... fieldParsers)
	{
		this.linesParser = linesParser;
		this.lineParser = lineParser;
		this.fieldParsers = copyOf(fieldParsers);
		maximumNumberOfFields = fieldParsers.length;
	}

	@NotNull
	@Override
	public ToDomainSeparatedValueParseEventHandlerState start(@MillisecondsSince1970 final long lastModified)
	{
		return new ToDomainSeparatedValueParseEventHandlerState(lastModified);
	}

	@Override
	public void field(@NotNull final ToDomainSeparatedValueParseEventHandlerState state, @NotNull final String value) throws CouldNotParseFieldException
	{
		state.field(value);
	}

	@Override
	public void endOfLine(@NotNull final ToDomainSeparatedValueParseEventHandlerState state) throws CouldNotParseLineException
	{
		state.endOfLine();
	}

	@Override
	public void end(@NotNull final ToDomainSeparatedValueParseEventHandlerState state)
	{
		state.end();
	}

	public final class ToDomainSeparatedValueParseEventHandlerState
	{
		@NotNull private final Object[] parsedFields;
		private boolean beforeHeaderLine;
		private int currentLineIndex;
		private int currentFieldIndex;
		@MillisecondsSince1970 private final long lastModified;
		private int numberOfFieldsPerLineDeducedFromHeaderLine;
		@NotNull private final L lines;

		private ToDomainSeparatedValueParseEventHandlerState(@MillisecondsSince1970 final long lastModified)
		{
			beforeHeaderLine = true;
			currentLineIndex = 0;
			currentFieldIndex = -1;
			this.lastModified = lastModified;
			numberOfFieldsPerLineDeducedFromHeaderLine = -1;

			parsedFields = new Object[maximumNumberOfFields];
			lines = linesParser.newParsedLines();
		}

		public void field(@NotNull final String value) throws CouldNotParseFieldException
		{
			currentFieldIndex++;
			if (beforeHeaderLine)
			{
				return;
			}
			if (currentFieldIndex == numberOfFieldsPerLineDeducedFromHeaderLine)
			{
				throw new CouldNotParseFieldException(currentFieldIndex, value, "there are more fields in the line than in the header line");
			}
			final FieldParser<?> fieldParser = fieldParsers[currentFieldIndex];

			@Nullable final Object result;
			if (value.isEmpty() && fieldParser.skipIfEmpty())
			{
				result = null;
			}
			else
			{
				result = fieldParser.parse(currentFieldIndex, value);
			}
			parsedFields[currentFieldIndex] = result;
		}

		public void endOfLine() throws CouldNotParseLineException
		{
			if (beforeHeaderLine)
			{
				beforeHeaderLine = false;
				numberOfFieldsPerLineDeducedFromHeaderLine = currentFieldIndex + 1;
				if (numberOfFieldsPerLineDeducedFromHeaderLine > maximumNumberOfFields)
				{
					throw new CouldNotParseLineException(currentLineIndex, "there are too many fields in the line");
				}
			}
			else
			{
				if (currentFieldIndex + 1 != numberOfFieldsPerLineDeducedFromHeaderLine)
				{
					throw new CouldNotParseLineException(currentLineIndex, "the number of fields differed to that in the header line");
				}
				lineParser.parse(currentLineIndex, parsedFields, lines);
			}

			currentLineIndex++;
			currentFieldIndex = -1;
		}

		public void end()
		{
			linesParser.parse(lastModified, lines);
		}
	}
}
