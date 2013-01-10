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
	public static String pad(final int ordinal, final int padding)
	{
		final String unpadded = Integer.toString(ordinal);
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
