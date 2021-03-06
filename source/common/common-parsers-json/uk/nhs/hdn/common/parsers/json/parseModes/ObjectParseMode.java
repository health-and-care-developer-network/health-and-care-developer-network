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

package uk.nhs.hdn.common.parsers.json.parseModes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.json.InvalidJsonException;
import uk.nhs.hdn.common.parsers.charaterSets.CharacterSet;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hdn.common.parsers.convenientReaders.PeekingConvenientReader;

import static uk.nhs.hdn.common.parsers.json.CharacterHelper.*;
import static uk.nhs.hdn.common.parsers.json.parseModes.ArrayParseMode.ObjectArrayParseModeInstance;
import static uk.nhs.hdn.common.parsers.json.parseModes.NumberParseMode.ObjectNumberParseModeInstance;
import static uk.nhs.hdn.common.parsers.json.parseModes.literalParseModes.BooleanLiteralParseMode.ObjectFalseBooleanConstantParseMode;
import static uk.nhs.hdn.common.parsers.json.parseModes.literalParseModes.BooleanLiteralParseMode.ObjectTrueBooleanConstantParseMode;
import static uk.nhs.hdn.common.parsers.json.parseModes.literalParseModes.NullLiteralParseMode.ObjectNullConstantParseMode;
import static uk.nhs.hdn.common.parsers.json.parseModes.stringParseModes.KeyStringParseMode.KeyStringParseModeInstance;
import static uk.nhs.hdn.common.parsers.json.parseModes.stringParseModes.ValueStringParseMode.ObjectValueStringParseModeInstance;

public final class ObjectParseMode extends AbstractParseMode
{
	@NotNull
	public static final ParseMode RootObjectParseModeInstance = new ObjectParseMode(true, Whitespace);

	@NotNull
	public static final ParseMode ObjectObjectParseModeInstance = new ObjectParseMode(false, WhitespaceCommaCloseObject);

	@NotNull
	public static final ParseMode ArrayObjectParseModeInstance = new ObjectParseMode(false, WhitespaceCommaCloseArray);

	public ObjectParseMode(final boolean isRootValue, @NotNull final CharacterSet validTerminatingCharacters)
	{
		super(isRootValue, validTerminatingCharacters);
	}

	@Override
	public void parse(@NotNull final JsonParseEventHandler jsonParseEventHandler, @NotNull final PeekingConvenientReader peekingConvenientReader) throws InvalidJsonException
	{
		if (isNot(OpenObject, readRequiredCharacter(peekingConvenientReader)))
		{
			throw new InvalidJsonException("does not start with open object");
		}

		jsonParseEventHandler.startObject();

		parseMembers(jsonParseEventHandler, peekingConvenientReader);

		jsonParseEventHandler.endObject();
	}

	@SuppressWarnings("FeatureEnvy")
	private static void parseMembers(final JsonParseEventHandler jsonParseEventHandler, final PeekingConvenientReader peekingConvenientReader) throws InvalidJsonException
	{
		if (is(soakUpWhitespace(peekingConvenientReader), CloseObject))
		{
			return;
		}
		peekingConvenientReader.pushBackLastCharacter();

		do
		{
			readKey(jsonParseEventHandler, peekingConvenientReader);

			readColonSeparator(peekingConvenientReader);

			readValue(jsonParseEventHandler, peekingConvenientReader);

			final char peekUpToCommaStart = soakUpWhitespace(peekingConvenientReader);

			if (is(peekUpToCommaStart, CloseObject))
			{
				return;
			}

			if (isNot(peekUpToCommaStart, Comma))
			{
				throw new InvalidJsonException("expected a trailing comma in object");
			}

		} while(true);
	}

	private static void readKey(final JsonParseEventHandler jsonParseEventHandler, final PeekingConvenientReader peekingConvenientReader) throws InvalidJsonException
	{
		final char peekUpToKeyStart = soakUpWhitespace(peekingConvenientReader);
		if (isNot(peekUpToKeyStart, QuotationMark))
		{
			throw new InvalidJsonException("object key string does not start with quotation mark");
		}
		peekingConvenientReader.pushBackLastCharacter();
		KeyStringParseModeInstance.parse(jsonParseEventHandler, peekingConvenientReader);
	}

	private static void readColonSeparator(final PeekingConvenientReader peekingConvenientReader) throws InvalidJsonException
	{
		final char peekUpToColon = soakUpWhitespace(peekingConvenientReader);

		if (isNot(peekUpToColon, Colon))
		{
			throw new InvalidJsonException("object key string is not followed with colon");
		}
	}

	private static void readValue(final JsonParseEventHandler jsonParseEventHandler, final PeekingConvenientReader peekingConvenientReader) throws InvalidJsonException
	{
		final char peekUpToValueStart = soakUpWhitespace(peekingConvenientReader);

		peekingConvenientReader.pushBackLastCharacter();

		final ParseMode valueParseMode = valueParseMode(peekUpToValueStart);
		valueParseMode.parse(jsonParseEventHandler, peekingConvenientReader);
	}

	public static char soakUpWhitespace(final PeekingConvenientReader peekingConvenientReader) throws InvalidJsonException
	{
		char peek;
		do
		{
			peek = readRequiredCharacter(peekingConvenientReader);
		}
		while(Whitespace.contains(peek));
		return peek;
	}

	@SuppressWarnings("SwitchStatementWithTooManyBranches")
	@NotNull
	private static ParseMode valueParseMode(final char peekUpToValueStart) throws InvalidJsonException
	{
		switch (peekUpToValueStart)
		{
			case OpenObject:
				return ObjectObjectParseModeInstance;

			case OpenArray:
				return ObjectArrayParseModeInstance;

			case TrueLiteralStart:
				return ObjectTrueBooleanConstantParseMode;

			case FalseLiteralStart:
				return ObjectFalseBooleanConstantParseMode;

			case NullLiteralStart:
				return ObjectNullConstantParseMode;

			case QuotationMark:
				return ObjectValueStringParseModeInstance;

			case ZeroDigit:
			case OneDigit:
			case TwoDigit:
			case ThreeDigit:
			case FourDigit:
			case FiveDigit:
			case SixDigit:
			case SevenDigit:
			case EightDigit:
			case NineDigit:
				return ObjectNumberParseModeInstance;

			default:
				throw new InvalidJsonException("unexpected character in object");
		}
	}
}
