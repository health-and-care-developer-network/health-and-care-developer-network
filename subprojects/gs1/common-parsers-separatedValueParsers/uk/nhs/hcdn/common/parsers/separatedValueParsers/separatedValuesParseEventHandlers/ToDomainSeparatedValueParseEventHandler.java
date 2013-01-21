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

package uk.nhs.hcdn.common.parsers.separatedValueParsers.separatedValuesParseEventHandlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.MillisecondsSince1970;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.fieldParsers.CouldNotParseFieldException;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.fieldParsers.FieldParser;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.lineParsers.CouldNotParseLineException;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.lineParsers.LineParser;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.linesParsers.LinesParser;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;

import java.util.ArrayList;
import java.util.List;

import static uk.nhs.hcdn.common.MillisecondsSince1970.NullMillisecondsSince1970;
import static uk.nhs.hcdn.common.VariableArgumentsHelper.copyOf;

public final class ToDomainSeparatedValueParseEventHandler<V> extends AbstractToString implements SeparatedValueParseEventHandler
{
	private final FieldParser<?>[] fieldParsers;
	private final Object[] parsedFields;
	@ExcludeFromToString
	private final int maximumNumberOfFields;
	@NotNull
	private final LinesParser<V> linesParser;
	@NotNull
	private final LineParser<V> lineParser;

	private boolean beforeHeaderLine;
	private int currentLineIndex;
	private int currentFieldIndex;
	@MillisecondsSince1970
	private long lastModified;
	private int numberOfFieldsPerLineDeducedFromHeaderLine;
	private List<V> lines;

	public ToDomainSeparatedValueParseEventHandler(@NotNull final LinesParser<V> linesParser, @NotNull final LineParser<V> lineParser, @NotNull final FieldParser<?>... fieldParsers)
	{
		this.linesParser = linesParser;
		this.lineParser = lineParser;
		this.fieldParsers = copyOf(fieldParsers);
		beforeHeaderLine = true;
		currentLineIndex = -1;
		currentFieldIndex = -1;
		lastModified = NullMillisecondsSince1970;
		numberOfFieldsPerLineDeducedFromHeaderLine = -1;

		maximumNumberOfFields = fieldParsers.length;
		parsedFields = new Object[maximumNumberOfFields];
	}

	@Override
	public void start(@MillisecondsSince1970 final long lastModified)
	{
		this.lastModified = lastModified;
		currentLineIndex = 0;
		lines = new ArrayList<>(10);
	}

	@Override
	public void field(@NotNull final String value) throws CouldNotParseFieldException
	{
		currentFieldIndex++;
		if (beforeHeaderLine)
		{
			return;
		}
		if (currentFieldIndex == numberOfFieldsPerLineDeducedFromHeaderLine)
		{
			throw new CouldNotParseFieldException(currentFieldIndex, value, "there are too more fields in the line than in the header line");
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

	@Override
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
			lines.add(lineParser.parse(currentLineIndex, parsedFields));

		}

		currentLineIndex++;
		currentFieldIndex = -1;
	}

	@Override
	public void end()
	{
		//new Gs1CompanyPrefixResourceStateSnapshot(lastModified, tuples);
		linesParser.parse(lastModified, lines);
	}
}
