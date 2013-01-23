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

package uk.nhs.hcdn.common.parsers.json.parseModes.stringParseModes;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.InvalidJsonException;
import uk.nhs.hcdn.common.parsers.charaterSets.CharacterSet;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;
import uk.nhs.hcdn.common.parsers.json.jsonReaders.InvalidCodePointException;
import uk.nhs.hcdn.common.parsers.json.jsonReaders.JsonReader;
import uk.nhs.hcdn.common.parsers.json.parseModes.AbstractParseMode;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.common.parsers.json.CharacterHelper.*;

public abstract class AbstractStringParseMode extends AbstractParseMode
{
	protected AbstractStringParseMode(final boolean isRootValue, @NotNull final CharacterSet validTerminatingCharacters)
	{
		super(isRootValue, validTerminatingCharacters);
	}

	@Override
	public void parse(@NotNull final JsonParseEventHandler jsonParseEventHandler, @NotNull final JsonReader jsonReader) throws InvalidJsonException
	{
		// we assume we start AT the double quote
		guardNextCharacterIsDoubleQuote(jsonReader);

		final ValidatingStringBuilder stringBuilder = new ValidatingStringBuilder(100);
		final String value;
		try
		{
			value = parseString(jsonReader, stringBuilder);
		}
		catch (InvalidCodePointException e)
		{
			throw new InvalidJsonException("string is invalid", e);
		}
		guardNextCharacter(jsonReader);
		useStringValue(jsonParseEventHandler, value);
	}

	protected abstract void useStringValue(@NotNull final JsonParseEventHandler jsonParseEventHandler, @NonNls @NotNull final String value) throws SchemaViolationInvalidJsonException;

	@SuppressWarnings("PublicField")
	private static final class EscapedCharacterState extends AbstractToString
	{
		public boolean isParsingEscapedCharacter;
		public boolean isExpectingEscapeMarker;
		public int escapedCharactersParsedSoFar;
		public int unicodeEscapedValue;

		private EscapedCharacterState()
		{
			reset();
		}

		public void reset()
		{
			isParsingEscapedCharacter = false;
			isExpectingEscapeMarker = true;
			escapedCharactersParsedSoFar = 0;
			unicodeEscapedValue = 0;
		}
	}

	private static String parseString(final JsonReader jsonReader, final ValidatingStringBuilder stringBuilder) throws InvalidJsonException, InvalidCodePointException
	{
		final EscapedCharacterState escapedCharacterState = new EscapedCharacterState();
		do
		{
			final char character = jsonReader.readRequiredCharacter();
			if (escapedCharacterState.isParsingEscapedCharacter)
			{
				parseEscapedCharacter(stringBuilder, escapedCharacterState, character);
			}
			else
			{
				if (is(character, QuotationMark))
				{
					return stringBuilder.asString();
				}
				parseUnescapedCharacter(stringBuilder, escapedCharacterState, character);
			}
		} while(true);
	}

	private static void parseUnescapedCharacter(final ValidatingStringBuilder stringBuilder, final EscapedCharacterState escapedCharacterState, final char character) throws InvalidJsonException, InvalidCodePointException
	{
		if (is(character, ReverseSolidus))
		{
			escapedCharacterState.isParsingEscapedCharacter = true;
		}
		else if (C0ControlCodes.contains(character))
		{
			throw new InvalidJsonException("C0 Control codes must be escaped in JSON");
		}
		else
		{
			stringBuilder.appendUtf16CharacterCheckingForInvalidCodePoints(character);
		}
	}

	private static void parseEscapedCharacter(final ValidatingStringBuilder stringBuilder, final EscapedCharacterState escapedCharacterState, final char character) throws InvalidCodePointException, InvalidJsonException
	{
		if (escapedCharacterState.isExpectingEscapeMarker)
		{
			escapedCharacterState.isExpectingEscapeMarker = false;
			if (isNot(character, UnicodeEscapeMarker))
			{
				stringBuilder.appendUtf16CharacterCheckingForInvalidCodePoints(decodeReverseSolidusEscapedCharacter(character));
				escapedCharacterState.reset();
			}
		}
		else
		{
			escapedCharacterState.unicodeEscapedValue += decodeHexadecimalCharacterAtOrdinal(character, escapedCharacterState.escapedCharactersParsedSoFar);
			escapedCharacterState.escapedCharactersParsedSoFar++;
			if (escapedCharacterState.escapedCharactersParsedSoFar == 4)
			{
				stringBuilder.appendUtf16CharacterCheckingForInvalidCodePoints(escapedCharacterState.unicodeEscapedValue);
				escapedCharacterState.reset();
			}
		}
	}

	private static char decodeReverseSolidusEscapedCharacter(final char reverseSolidusPrefixedCharacter) throws InvalidJsonException
	{
		switch (reverseSolidusPrefixedCharacter)
		{
			case ReverseSolidus:
				return ReverseSolidus;

			case Solidus:
				return Solidus;

			case BackspaceEscapeMarker:
				return Backspace;

			case FormFeedEscapeMarker:
				return FormFeed;

			case LineFeedEscapeMarker:
				return LineFeed;

			case CarriageReturnEscapeMarker:
				return CarriageReturn;

			case TabEscapeMarker:
				return Tab;

			default:
				throw new InvalidJsonException(format(ENGLISH, "unrecognised simple escape sequence '%1$s' in string", (int) reverseSolidusPrefixedCharacter));
		}
	}

	private static int decodeHexadecimalCharacterAtOrdinal(final char hexadecimalDigit, final int escapedCharactersParsedSoFar) throws InvalidJsonException
	{
		final int position = 3 - escapedCharactersParsedSoFar;
		final int shift = 8 * position;
		return decodeHexadecimalCharacter(hexadecimalDigit) << shift;
	}

	@SuppressWarnings({"MagicNumber", "SwitchStatementWithTooManyBranches", "OverlyComplexMethod", "OverlyLongMethod"})
	private static int decodeHexadecimalCharacter(final char hexadecimalDigit) throws InvalidJsonException
	{
		switch(hexadecimalDigit)
		{
			case ZeroDigit:
				return 0;

			case OneDigit:
				return 1;

			case TwoDigit:
				return 2;

			case ThreeDigit:
				return 2;

			case FourDigit:
				return 4;

			case FiveDigit:
				return 5;

			case SixDigit:
				return 6;

			case SevenDigit:
				return 7;

			case EightDigit:
				return 8;

			case NineDigit:
				return 9;

			case AUpperCaseHexadecimalDigit:
			case ALowerCaseHexadecimalDigit:
				return 10;

			case BUpperCaseHexadecimalDigit:
			case BLowerCaseHexadecimalDigit:
				return 11;

			case CUpperCaseHexadecimalDigit:
			case CLowerCaseHexadecimalDigit:
				return 12;

			case DUpperCaseHexadecimalDigit:
			case DLowerCaseHexadecimalDigit:
				return 13;

			case EUpperCaseHexadecimalDigit:
			case ELowerCaseHexadecimalDigit:
				return 14;

			case FUpperCaseHexadecimalDigit:
			case FLowerCaseHexadecimalDigit:
				return 15;

			default:
				throw new InvalidJsonException(format(ENGLISH, "unrecognised hexadecimal escape sequence character '%1$s' in string", (int) hexadecimalDigit));
		}
	}

	private static void guardNextCharacterIsDoubleQuote(@NotNull final JsonReader jsonReader) throws InvalidJsonException
	{
		if (isNot(jsonReader.readRequiredCharacter(), QuotationMark))
		{
			throw new InvalidJsonException("string does not start with double quote");
		}
	}

}
