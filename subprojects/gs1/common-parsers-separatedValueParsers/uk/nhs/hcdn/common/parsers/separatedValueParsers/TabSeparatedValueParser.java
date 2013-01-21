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

package uk.nhs.hcdn.common.parsers.separatedValueParsers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.MillisecondsSince1970;
import uk.nhs.hcdn.common.parsers.CouldNotParseException;
import uk.nhs.hcdn.common.parsers.Parser;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.fieldParsers.CouldNotParseFieldException;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.lineParsers.CouldNotParseLineException;
import uk.nhs.hcdn.common.parsers.separatedValueParsers.separatedValuesParseEventHandlers.SeparatedValueParseEventHandler;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.io.BufferedReader;
import java.io.IOException;

public final class TabSeparatedValueParser extends AbstractToString implements Parser
{
	private static final int EndOfStream = -1;
	private static final char HorizontalTab = '\t';
	private static final char CarriageReturn = '\r';
	private static final char LineFeed = '\n';
	private static final int GuessOfBufferSize = 4096;

	@NotNull
	private final SeparatedValueParseEventHandler separatedValueParseEventHandler;

	public TabSeparatedValueParser(@NotNull final SeparatedValueParseEventHandler separatedValueParseEventHandler)
	{
		this.separatedValueParseEventHandler = separatedValueParseEventHandler;
	}

	@Override
	@SuppressWarnings("NestedAssignment")
	public void parse(@NotNull final BufferedReader bufferedReader, @MillisecondsSince1970 final long lastModified) throws IOException, CouldNotParseException
	{
		separatedValueParseEventHandler.start(lastModified);
		int offset = 0;
		int characterAsInt;
		StringBuilder fieldStringBuilder = newStringBuilder();
		boolean expectingLineFeed = false;
		boolean afterStartOfNewLine = false;
		while ((characterAsInt = bufferedReader.read()) != EndOfStream)
		{
			@SuppressWarnings("NumericCastThatLosesPrecision") final char character = (char) characterAsInt;
			switch (characterAsInt)
			{
				case CarriageReturn:
					expectingLineFeed = true;
					fieldStringBuilder = endOfLine(offset, fieldStringBuilder);
					afterStartOfNewLine = false;
					break;

				case HorizontalTab:
					guardExpectingLineFeed(offset, expectingLineFeed);
					fieldStringBuilder = raiseField(offset, fieldStringBuilder);
					afterStartOfNewLine = true;
					break;

				case LineFeed:
					if (expectingLineFeed)
					{
						expectingLineFeed = false;
					}
					else
					{
						fieldStringBuilder = endOfLine(offset, fieldStringBuilder);
					}
					afterStartOfNewLine = false;
					break;

				default:
					guardExpectingLineFeed(offset, expectingLineFeed);
					fieldStringBuilder.append(character);
					afterStartOfNewLine = true;
					break;
			}
			offset++;
		}
		guardExpectingLineFeed(offset, expectingLineFeed);
		if (afterStartOfNewLine)
		{
			endOfLine(offset, fieldStringBuilder);
		}
		separatedValueParseEventHandler.end();
	}

	private static void guardExpectingLineFeed(final int offset, final boolean expectingLineFeed) throws CouldNotParseException
	{
		if (expectingLineFeed)
		{
			throw new CouldNotParseException(offset, "CR line terminated files are not supported");
		}
	}

	private StringBuilder raiseField(final int offset, final StringBuilder fieldStringBuilder) throws CouldNotParseException
	{
		try
		{
			separatedValueParseEventHandler.field(fieldStringBuilder.toString());
		}
		catch (CouldNotParseFieldException e)
		{
			throw new CouldNotParseException(offset, e);
		}
		return newStringBuilder();
	}

	private StringBuilder endOfLine(final int offset, final StringBuilder fieldStringBuilder) throws CouldNotParseException
	{
		final StringBuilder newStringBuilder;
		try
		{
			newStringBuilder = raiseField(offset, fieldStringBuilder);
		}
		catch (CouldNotParseException e)
		{
			throw new CouldNotParseException(offset, e);
		}
		try
		{
			separatedValueParseEventHandler.endOfLine();
		}
		catch (CouldNotParseLineException e)
		{
			throw new CouldNotParseException(offset, e);
		}
		return newStringBuilder;
	}

	private static StringBuilder newStringBuilder()
	{
		return new StringBuilder(GuessOfBufferSize);
	}
}
