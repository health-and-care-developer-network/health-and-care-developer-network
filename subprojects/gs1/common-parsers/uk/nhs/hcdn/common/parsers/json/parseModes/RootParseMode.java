/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.parseModes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.InvalidJsonException;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hcdn.common.parsers.json.jsonReaders.EndOfFileException;
import uk.nhs.hcdn.common.parsers.json.jsonReaders.JsonReader;

import static uk.nhs.hcdn.common.parsers.json.CharacterHelper.*;
import static uk.nhs.hcdn.common.parsers.json.parseModes.ArrayParseMode.RootArrayParseModeInstance;
import static uk.nhs.hcdn.common.parsers.json.parseModes.NumberParseMode.RootNumberParseModeInstance;
import static uk.nhs.hcdn.common.parsers.json.parseModes.ObjectParseMode.RootObjectParseModeInstance;
import static uk.nhs.hcdn.common.parsers.json.parseModes.ObjectParseMode.soakUpWhitespace;
import static uk.nhs.hcdn.common.parsers.json.parseModes.literalParseModes.BooleanLiteralParseMode.RootFalseBooleanConstantParseMode;
import static uk.nhs.hcdn.common.parsers.json.parseModes.literalParseModes.BooleanLiteralParseMode.RootTrueBooleanConstantParseMode;
import static uk.nhs.hcdn.common.parsers.json.parseModes.literalParseModes.NullLiteralParseMode.RootNullConstantParseMode;
import static uk.nhs.hcdn.common.parsers.json.parseModes.stringParseModes.ValueStringParseMode.RootValueStringParseModeInstance;

public final class RootParseMode implements ParseMode
{
	@NotNull
	public static final ParseMode RootParseModeInstance = new RootParseMode();

	@Override
	public void parse(@NotNull final JsonParseEventHandler jsonParseEventHandler, @NotNull final JsonReader jsonReader) throws InvalidJsonException
	{
		final char peekUpToValueStart = soakUpWhitespace(jsonReader);

		final ParseMode valueParseMode = valueParseMode(peekUpToValueStart);
		jsonReader.pushBackLastCharacter();

		jsonParseEventHandler.startRoot();

		valueParseMode.parse(jsonParseEventHandler, jsonReader);

		validateTrailingWhitespace(jsonReader);

		jsonParseEventHandler.endRoot();
	}

	private static void validateTrailingWhitespace(final JsonReader jsonReader) throws InvalidJsonException
	{
		do
		{
			try
			{
				if (Whitespace.doesNotContain(jsonReader.readCharacter()))
				{
					throw new InvalidJsonException("Trailing non-whitespace");
				}
			}
			catch (EndOfFileException ignored)
			{
				return;
			}
		}
		while(true);
	}

	@SuppressWarnings("SwitchStatementWithTooManyBranches")
	@NotNull
	private static ParseMode valueParseMode(final char peekUpToValueStart) throws InvalidJsonException
	{
		switch (peekUpToValueStart)
		{
			case OpenObject:
				return RootObjectParseModeInstance;

			case OpenArray:
				return RootArrayParseModeInstance;

			case TrueLiteralStart:
				return RootTrueBooleanConstantParseMode;

			case FalseLiteralStart:
				return RootFalseBooleanConstantParseMode;

			case NullLiteralStart:
				return RootNullConstantParseMode;

			case QuotationMark:
				return RootValueStringParseModeInstance;

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
				return RootNumberParseModeInstance;

			default:
				throw new InvalidJsonException("unexpected character in root");
		}
	}
}
