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

package uk.nhs.hdn.common;

import org.jetbrains.annotations.NotNull;

public final class ByteArrayHelper
{
	@NotNull private static final char[] HexadecimalCharacterMap = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	private ByteArrayHelper()
	{
	}

	@SuppressWarnings("MagicNumber")
	@NotNull
	public static char[] toBase16Hexadecimal(@NotNull final byte... values)
	{
		final int length = values.length;
		final char[] hexadecimal = new char[length * 2];
		for (int index = 0; index < length; index++)
		{
			final int unsignedByte = values[index] & 0xFF;
			final int outIndex = index * 2;
			hexadecimal[outIndex] = HexadecimalCharacterMap[unsignedByte >>> 4];
			hexadecimal[outIndex + 1] = HexadecimalCharacterMap[unsignedByte & 0x0F];
		}
		return hexadecimal;
	}
}
