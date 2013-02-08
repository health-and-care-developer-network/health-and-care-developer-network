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

package uk.nhs.hdn.common.parsers.separatedValueParsers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.parsers.CouldNotParseException;
import uk.nhs.hdn.common.parsers.Parser;
import uk.nhs.hdn.common.parsers.convenientReaders.BufferedConvenientReader;
import uk.nhs.hdn.common.parsers.convenientReaders.ConvenientReader;
import uk.nhs.hdn.common.parsers.convenientReaders.EndOfFileException;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.CouldNotParseFieldException;
import uk.nhs.hdn.common.parsers.separatedValueParsers.lineParsers.CouldNotParseLineException;
import uk.nhs.hdn.common.parsers.separatedValueParsers.separatedValuesParseEventHandlers.SeparatedValueParseEventHandler;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.io.BufferedReader;

public class CommaSeparatedValueParser extends AbstractToString implements Parser
{
	private static final char Comma = ',';
	private static final char DoubleQuote = '"';
	private static final char CarriageReturn = '\r';
	private static final char LineFeed = '\n';
	private static final int GuessOfBufferSize = 4096;

	@NotNull
	private final SeparatedValueParseEventHandler separatedValueParseEventHandler;
	private final boolean readAllRows;

	public CommaSeparatedValueParser(@NotNull final SeparatedValueParseEventHandler separatedValueParseEventHandler, final boolean readOnlyFirstRow)
	{
		this.separatedValueParseEventHandler = separatedValueParseEventHandler;
		readAllRows = !readOnlyFirstRow;
	}

	@Override
	@SuppressWarnings("NestedAssignment")
	public void parse(@NotNull final BufferedReader bufferedReader, @MillisecondsSince1970 final long lastModified) throws CouldNotParseException
	{
		final ConvenientReader convenientReader = new BufferedConvenientReader(bufferedReader);

		separatedValueParseEventHandler.start(lastModified);

		readRows(convenientReader);

		separatedValueParseEventHandler.end();
	}

	@SuppressWarnings("LoopConditionNotUpdatedInsideLoop")
	private void readRows(final ConvenientReader convenientReader) throws CouldNotParseException
	{
		boolean endOfLineNeedsToBeRaisedIfEndOfFileEncountered = true;
		do
		{
			final char character;
			try
			{
				character = convenientReader.readCharacter();
			}
			catch (EndOfFileException ignored)
			{
				if (endOfLineNeedsToBeRaisedIfEndOfFileEncountered)
				{
					raiseEndOfLine(convenientReader);
				}
				return;
			}

			if (is(character, DoubleQuote))
			{
				endOfLineNeedsToBeRaisedIfEndOfFileEncountered = parseQuotedField(convenientReader);
			}
			else
			{
				endOfLineNeedsToBeRaisedIfEndOfFileEncountered = parseUnquotedField(character, convenientReader);
			}
		} while(readAllRows);
	}

	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	private boolean parseUnquotedField(final char firstCharacter, final ConvenientReader convenientReader) throws CouldNotParseException
	{
		final StringBuilder fieldStringBuilder = newFieldStringBuilder();
		char character = firstCharacter;
		do
		{
			switch (character)
			{
				case Comma:
					return raiseField(convenientReader, fieldStringBuilder);

				case CarriageReturn:
					guardCrFollowedByLf(convenientReader);
					//noinspection fallthrough

				case LineFeed:
					return raiseFieldThenEndOfLine(convenientReader, fieldStringBuilder);

				default:
					fieldStringBuilder.append(character);
			}

			try
			{
				character = convenientReader.readCharacter();
			}
			catch (EndOfFileException ignored)
			{
				return raiseFieldThenEndOfLine(convenientReader, fieldStringBuilder);
			}
		}
		while(true);
	}

	@SuppressWarnings({"FeatureEnvy", "BooleanMethodNameMustStartWithQuestion"})
	private boolean parseQuotedField(final ConvenientReader convenientReader) throws CouldNotParseException
	{
		final StringBuilder fieldStringBuilder = newFieldStringBuilder();
		do
		{
			final char character;
			try
			{
				character = convenientReader.readCharacter();
			}
			catch (EndOfFileException e)
			{
				throw new CouldNotParseException(convenientReader.position(), e);
			}
			if (is(character, DoubleQuote))
			{
				final char next;
				try
				{
					next = convenientReader.readCharacter();
				}
				catch (EndOfFileException ignored)
				{
					return raiseFieldThenEndOfLine(convenientReader, fieldStringBuilder);
				}

				switch(next)
				{
					case Comma:
						return raiseField(convenientReader, fieldStringBuilder);

					case CarriageReturn:
						guardCrFollowedByLf(convenientReader);
						//noinspection fallthrough

					case LineFeed:
						return raiseFieldThenEndOfLine(convenientReader, fieldStringBuilder);

					case DoubleQuote:
						break;

					default:
						throw new CouldNotParseException(convenientReader.position(), "a field has a double quote follow by neither a double quote, a comma, a line feed, a carriage return or end-of-line");
				}
			}

			fieldStringBuilder.append(character);

		} while(true);
	}

	private static void guardCrFollowedByLf(final ConvenientReader convenientReader) throws CouldNotParseException
	{
		if (isNot(readRequired(convenientReader), LineFeed))
		{
			throw new CouldNotParseException(convenientReader.position(), "should be CRLF, not CR something else");
		}
	}

	private static boolean is(final char character, final char is)
	{
		return (int) character == (int) is;
	}

	private static boolean isNot(final char character, final char is)
	{
		return (int) character != (int) is;
	}

	private static char readRequired(final ConvenientReader convenientReader) throws CouldNotParseException
	{
		try
		{
			return convenientReader.readCharacter();
		}
		catch (EndOfFileException e)
		{
			throw new CouldNotParseException(convenientReader.position(), e);
		}
	}

	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	private boolean raiseField(final ConvenientReader convenientReader, final StringBuilder fieldStringBuilder) throws CouldNotParseException
	{
		try
		{
			separatedValueParseEventHandler.field(fieldStringBuilder.toString());
		}
		catch (CouldNotParseFieldException e)
		{
			throw new CouldNotParseException(convenientReader.position(), e);
		}

		return true;
	}

	@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
	private boolean raiseFieldThenEndOfLine(final ConvenientReader convenientReader, final StringBuilder fieldStringBuilder) throws CouldNotParseException
	{
		raiseField(convenientReader, fieldStringBuilder);

		raiseEndOfLine(convenientReader);

		return false;
	}

	private void raiseEndOfLine(final ConvenientReader convenientReader) throws CouldNotParseException
	{
		try
		{
			separatedValueParseEventHandler.endOfLine();
		}
		catch (CouldNotParseLineException e)
		{
			throw new CouldNotParseException(convenientReader.position(), e);
		}
	}

	private static StringBuilder newFieldStringBuilder()
	{
		return new StringBuilder(GuessOfBufferSize);
	}
}
