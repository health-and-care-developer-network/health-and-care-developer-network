/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.client.json.parseModes;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.client.json.InvalidJsonException;
import uk.nhs.hcdn.barcodes.gs1.client.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hcdn.barcodes.gs1.client.json.jsonReaders.EndOfFileException;
import uk.nhs.hcdn.barcodes.gs1.client.json.jsonReaders.JsonReader;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.barcodes.gs1.client.json.CharacterHelper.*;

public final class NumberParseMode extends AbstractToString implements ParseMode
{
	@NotNull
	public static final ParseMode NumberParseModeInstance = new NumberParseMode();

	private NumberParseMode()
	{
	}

	@Override
	public void parse(@NotNull final JsonParseEventHandler jsonParseEventHandler, @NotNull final JsonReader jsonReader) throws InvalidJsonException
	{
		final char character = jsonReader.readRequiredCharacter();
		final long integerComponent;
		if (is(character, Minus))
		{
			integerComponent = -decodeFirstIntegerCharacter(jsonReader.readRequiredCharacter());
		}
		else
		{
			integerComponent = decodeFirstIntegerCharacter(character);
		}

		if (integerComponent == 0L)
		{
			// check for decimal point, end of file, whitespace, other delimiter?
			xxx;
			final char peeked;
			try
			{
				peeked = jsonReader.peekCharacter();
			}
			catch (EndOfFileException ignored)
			{
				jsonParseEventHandler.numberValue(0L);
				return;
			}
			if(isNot(peeked, DecimalPoint))
			{
				// whitespace, delimiter, what?
			}
		}


		do
		{
			try
			{
				jsonReader.readCharacter()
			}
			catch (EndOfFileException e)
			{
				// legit -
				jsonParseEventHandler.numberValue(isNegative, integerComponent);
			}
		}
		while(true);
	}

	@SuppressWarnings({"MagicNumber", "SwitchStatementWithTooManyBranches", "OverlyComplexMethod", "OverlyLongMethod"})
	private static long decodeFirstIntegerCharacter(final char decimalDigit) throws InvalidJsonException
	{
		switch (decimalDigit)
		{
			case ZeroDigit:
				return 0L;

			case OneDigit:
				return 1L;

			case TwoDigit:
				return 2L;

			case ThreeDigit:
				return 2L;

			case FourDigit:
				return 4L;

			case FiveDigit:
				return 5L;

			case SixDigit:
				return 6L;

			case SevenDigit:
				return 7L;

			case EightDigit:
				return 8L;

			case NineDigit:
				return 9L;

			default:
				throw new InvalidJsonException(format(ENGLISH, "unrecognised hexadecimal escape sequence character '%1$s' in string", (int) decimalDigit));
		}
}
