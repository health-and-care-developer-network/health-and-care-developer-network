/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers.xml;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;
import uk.nhs.hcdn.common.serialisers.CouldNotEncodeDataException;
import uk.nhs.hcdn.common.serialisers.CouldNotWriteDataException;

import java.io.IOException;
import java.io.Writer;

import static java.lang.Character.*;
import static uk.nhs.hcdn.common.StringHelper.padAsDecimal;

@SuppressWarnings("ConstantNamingConvention")
public final class XmlStringWriter extends AbstractToString
{
	private static final char[] x00 = replacement(0x00);
	private static final char[] x01 = replacement(0x01);
	private static final char[] x02 = replacement(0x02);
	private static final char[] x03 = replacement(0x03);
	private static final char[] x04 = replacement(0x04);
	private static final char[] x05 = replacement(0x05);
	private static final char[] x06 = replacement(0x06);
	private static final char[] x07 = replacement(0x07);
	private static final char[] x08 = replacement(0x08);
	private static final char[] x09 = replacement(0x09);
	private static final char[] x0A = replacement(0x0A);
	private static final char[] x0B = replacement(0x0B);
	private static final char[] x0C = replacement(0x0C);
	private static final char[] x0D = replacement(0x0D);
	private static final char[] x0E = replacement(0x0E);
	private static final char[] x0F = replacement(0x0F);
	private static final char[] x10 = replacement(0x10);
	private static final char[] x11 = replacement(0x11);
	private static final char[] x12 = replacement(0x12);
	private static final char[] x13 = replacement(0x13);
	private static final char[] x14 = replacement(0x14);
	private static final char[] x15 = replacement(0x15);
	private static final char[] x16 = replacement(0x16);
	private static final char[] x17 = replacement(0x17);
	private static final char[] x18 = replacement(0x18);
	private static final char[] x19 = replacement(0x19);
	private static final char[] Quote = replacement("&quot;");
	private static final char[] Ampersand = replacement("&amp;");
	private static final char[] Apostrophe = replacement("&apos;");
	private static final char[] LessThan = replacement("&lt;");
	private static final char[] GreaterThan = replacement("&gt;");
	private static final char[] x7F = replacement(0x7F);
	private static final char[] x80 = replacement(0x80);
	private static final char[] x81 = replacement(0x81);
	private static final char[] x82 = replacement(0x82);
	private static final char[] x83 = replacement(0x83);
	private static final char[] x84 = replacement(0x84);
	private static final char[] x85 = replacement(0x85);
	private static final char[] x86 = replacement(0x86);
	private static final char[] x87 = replacement(0x87);
	private static final char[] x88 = replacement(0x88);
	private static final char[] x89 = replacement(0x89);
	private static final char[] x8A = replacement(0x8A);
	private static final char[] x8B = replacement(0x8B);
	private static final char[] x8C = replacement(0x8C);
	private static final char[] x8D = replacement(0x8D);
	private static final char[] x8E = replacement(0x8E);
	private static final char[] x8F = replacement(0x8F);
	private static final char[] x90 = replacement(0x90);
	private static final char[] x91 = replacement(0x91);
	private static final char[] x92 = replacement(0x92);
	private static final char[] x93 = replacement(0x93);
	private static final char[] x94 = replacement(0x94);
	private static final char[] x95 = replacement(0x95);
	private static final char[] x96 = replacement(0x96);
	private static final char[] x97 = replacement(0x97);
	private static final char[] x98 = replacement(0x98);
	private static final char[] x99 = replacement(0x99);
	private static final char[] x9A = replacement(0x9A);
	private static final char[] x9B = replacement(0x9B);
	private static final char[] x9C = replacement(0x9C);
	private static final char[] x9D = replacement(0x9D);
	private static final char[] x9E = replacement(0x9E);
	private static final char[] x9F = replacement(0x9F);

	private static char[] replacement(final int controlCode)
	{
		return replacement("&#" + padAsDecimal(controlCode, 4) + ';');
	}

	private static char[] replacement(@NonNls final String value)
	{
		return value.toCharArray();
	}

	@NotNull @ExcludeFromToString
	private final Writer writer;

	public XmlStringWriter(@NotNull final Writer writer)
	{
		this.writer = writer;
	}

	// Does not check for ':' in name
	// Does not check that name starts (xml, XML or any other variant)
	public void writeNodeName(@NotNull final CharSequence value) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		writeText(value);
	}

	public void writeText(@NotNull final CharSequence value) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		final int length = value.length();
		boolean previousWasHighSurrogate = false;
		for(int index = 0; index < length; index++)
		{
			final char character = value.charAt(index);
			if (previousWasHighSurrogate)
			{
				if (!isLowSurrogate(character))
				{
					throw new CouldNotEncodeDataException("high surrogate not followed by low surrogate");
				}
				previousWasHighSurrogate = false;
			}
			else if (isSurrogate(character))
			{
				if (!isHighSurrogate(character))
				{
					throw new CouldNotEncodeDataException("low surrogate is leading surrogate pair");
				}
				previousWasHighSurrogate = true;
			}
			writeCharacter(character);
		}
		if (previousWasHighSurrogate)
		{
			throw new CouldNotEncodeDataException("string ends with an orphaned high surrogate");
		}
	}

	@SuppressWarnings({"SwitchStatementWithTooManyBranches", "OverlyComplexMethod", "OverlyLongMethod", "MagicNumber", "MagicCharacter"})
	public void writeCharacter(final char character) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		final char[] replacement;
		switch (character)
		{
			case 0x00:
				replacement = x00;
				break;

			case 0x01:
				replacement = x01;
				break;

			case 0x02:
				replacement = x02;
				break;

			case 0x03:
				replacement = x03;
				break;

			case 0x04:
				replacement = x04;
				break;

			case 0x05:
				replacement = x05;
				break;

			case 0x06:
				replacement = x06;
				break;

			case 0x07:
				replacement = x07;
				break;

			case 0x08:
				replacement = x08;
				break;

			case 0x09:
				replacement = x09;
				break;

			case 0x0A:
				replacement = x0A;
				break;

			case 0x0B:
				replacement = x0B;
				break;

			case 0x0C:
				replacement = x0C;
				break;

			case 0x0D:
				replacement = x0D;
				break;

			case 0x0E:
				replacement = x0E;
				break;

			case 0x0F:
				replacement = x0F;
				break;

			case 0x10:
				replacement = x10;
				break;

			case 0x11:
				replacement = x11;
				break;

			case 0x12:
				replacement = x12;
				break;

			case 0x13:
				replacement = x13;
				break;

			case 0x14:
				replacement = x14;
				break;

			case 0x15:
				replacement = x15;
				break;

			case 0x16:
				replacement = x16;
				break;

			case 0x17:
				replacement = x17;
				break;

			case 0x18:
				replacement = x18;
				break;

			case 0x19:
				replacement = x19;
				break;

			case '"':
				replacement = Quote;
				break;

			case '&':
				replacement = Ampersand;
				break;

			case '\'':
				replacement = Apostrophe;
				break;

			case '<':
				replacement = LessThan;
				break;

			case '>':
				replacement = GreaterThan;
				break;

			case 0x7F:
				replacement = x7F;
				break;

			case 0x80:
				replacement = x80;
				break;

			case 0x81:
				replacement = x81;
				break;

			case 0x82:
				replacement = x82;
				break;

			case 0x83:
				replacement = x83;
				break;

			case 0x84:
				replacement = x84;
				break;

			case 0x85:
				replacement = x85;
				break;

			case 0x86:
				replacement = x86;
				break;

			case 0x87:
				replacement = x87;
				break;

			case 0x88:
				replacement = x88;
				break;

			case 0x89:
				replacement = x89;
				break;

			case 0x8A:
				replacement = x8A;
				break;

			case 0x8B:
				replacement = x8B;
				break;

			case 0x8C:
				replacement = x8C;
				break;

			case 0x8D:
				replacement = x8D;
				break;

			case 0x8E:
				replacement = x8E;
				break;

			case 0x8F:
				replacement = x8F;
				break;

			case 0x90:
				replacement = x90;
				break;

			case 0x91:
				replacement = x91;
				break;

			case 0x92:
				replacement = x92;
				break;

			case 0x93:
				replacement = x93;
				break;

			case 0x94:
				replacement = x94;
				break;

			case 0x95:
				replacement = x95;
				break;

			case 0x96:
				replacement = x96;
				break;

			case 0x97:
				replacement = x97;
				break;

			case 0x98:
				replacement = x98;
				break;

			case 0x99:
				replacement = x99;
				break;

			case 0x9A:
				replacement = x9A;
				break;

			case 0x9B:
				replacement = x9B;
				break;

			case 0x9C:
				replacement = x9C;
				break;

			case 0x9D:
				replacement = x9D;
				break;

			case 0x9E:
				replacement = x9E;
				break;

			case 0x9F:
				replacement = x9F;
				break;

			case 0xFFFE:
			case 0xFFFF:
				throw new CouldNotEncodeDataException("0xFFFF and 0xFFFE are not valid in XML");

			default:
				try
				{
					writer.write((int) character);
				}
				catch (IOException e)
				{
					throw new CouldNotWriteDataException(e);
				}
				return;
		}
		try
		{
			writer.write(replacement);
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}
}
