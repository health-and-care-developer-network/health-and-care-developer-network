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
	public static String padAsDecimal(final long ordinal, final int padding)
	{
		final String unpadded = Long.toString(ordinal);
		return pad(unpadded, padding);
	}

	@NonNls
	@NotNull
	public static String padAsHexadecimal(final int ordinal, final int padding)
	{
		final String unpadded = Integer.toHexString(ordinal);
		return pad(unpadded, padding);
	}

	@NonNls
	@NotNull
	public static String padAsHexadecimal(final long ordinal, final int padding)
	{
		final String unpadded = Long.toHexString(ordinal);
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
