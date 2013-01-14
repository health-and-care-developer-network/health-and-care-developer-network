/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public final class StringHelper
{
	private static final char ZeroDigit = '0';

	private StringHelper()
	{
	}

	@NonNls
	@NotNull
	public static String padAsDecimal(final int ordinal, final int padding)
	{
		final String unpadded = Integer.toString(ordinal);
		return pad(unpadded, padding);
	}

	@NonNls
	@NotNull
	public static String padAsHexadecimal(final int ordinal, final int padding)
	{
		final String unpadded = Integer.toHexString(ordinal);
		return pad(unpadded, padding);
	}

	@NotNull
	public static String pad(@NotNull @NonNls final String unpadded, final int padding)
	{
		if (unpadded.length() == padding)
		{
			return unpadded;
		}
		int i = padding - unpadded.length();
		final char[] padded = new char[padding];
		unpadded.getChars(0, unpadded.length(), padded, i);
		while (i > 0)
		{
			i--;
			padded[i] = ZeroDigit;
		}
		return new String(padded);
	}
}
