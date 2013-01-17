/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.client.json;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.barcodes.gs1.client.json.charaterSets.BitSetCharacterSet;
import uk.nhs.hcdn.barcodes.gs1.client.json.charaterSets.CharacterSet;

public final class CharacterHelper
{
	public static final char QuotationMark = '"';
	public static final char ReverseSolidus = '\\';
	public static final char Solidus = '/';
	public static final char Backspace = '\b';
	public static final char FormFeed = '\f';
	public static final char LineFeed = '\n';
	public static final char CarriageReturn = '\r';
	public static final char Tab = '\t';

	public static final char BackspaceEscapeMarker = 'b';
	public static final char FormFeedEscapeMarker = 'f';
	public static final char LineFeedEscapeMarker = 'n';
	public static final char CarriageReturnEscapeMarker = 'r';
	public static final char TabEscapeMarker = 't';
	public static final char UnicodeEscapeMarker = 'u';

	public static final char ZeroDigit = '0';
	public static final char OneDigit = '1';
	public static final char TwoDigit = '2';
	public static final char ThreeDigit = '3';
	public static final char FourDigit = '4';
	public static final char FiveDigit = '5';
	public static final char SixDigit = '6';
	public static final char SevenDigit = '7';
	public static final char EightDigit = '8';
	public static final char NineDigit = '9';

	public static final char AUpperCaseHexadecimalDigit = 'A';
	public static final char BUpperCaseHexadecimalDigit = 'B';
	public static final char CUpperCaseHexadecimalDigit = 'C';
	public static final char DUpperCaseHexadecimalDigit = 'D';
	public static final char EUpperCaseHexadecimalDigit = 'E';
	public static final char FUpperCaseHexadecimalDigit = 'F';

	public static final char ALowerCaseHexadecimalDigit = 'a';
	public static final char BLowerCaseHexadecimalDigit = 'b';
	public static final char CLowerCaseHexadecimalDigit = 'c';
	public static final char DLowerCaseHexadecimalDigit = 'd';
	public static final char ELowerCaseHexadecimalDigit = 'e';
	public static final char FLowerCaseHexadecimalDigit = 'f';

	public static final char Minus = '-';
	public static final char DecimalPoint = '.';

	@NotNull
	public static final CharacterSet C0ControlCodes = new BitSetCharacterSet(0x0000, 0x001F);

	@NotNull
	public static final CharacterSet Structural = new BitSetCharacterSet('[', '{', ']', '}', ':', ',');

	@NotNull
	public static final BitSetCharacterSet Whitespace = new BitSetCharacterSet(' ', '\t', '\n', '\r');

	@NotNull
	public static final CharacterSet WhitespaceCommaCloseObject = new BitSetCharacterSet(Whitespace, ',', '}');

	@NotNull
	public static final CharacterSet WhitespaceCommaCloseArray = new BitSetCharacterSet(Whitespace, ',', ']');

	@NotNull
	public static final CharacterSet WhitespaceColonArray = new BitSetCharacterSet(Whitespace, ':');

	private CharacterHelper()
	{
	}

	public static boolean is(final char character, final char shouldBe)
	{
		return (int) character == (int) shouldBe;
	}

	public static boolean isNot(final char character, final char shouldNotBe)
	{
		return (int) character != (int) shouldNotBe;
	}
}
