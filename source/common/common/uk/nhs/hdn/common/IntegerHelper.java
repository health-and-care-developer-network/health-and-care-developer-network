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

import static java.lang.StrictMath.pow;

public final class IntegerHelper
{
	@SuppressWarnings("NumericCastThatLosesPrecision")
	public static int power(final int x, final int n)
	{
		return (int) pow((double) x, (double) n);
	}

	public static boolean isEven(final int value)
	{
		return value % 2 == 0;
	}

	@SuppressWarnings({"NumericCastThatLosesPrecision", "MagicNumber"})
	@NotNull
	public static byte[] signedIntegerToByteArray(final int value)
	{
		return new byte[]
		{
			(byte) (value >>> 24),
			(byte) (value >>> 16),
			(byte) (value >>> 8),
			(byte) value
		};
	}

	private IntegerHelper()
	{
	}
}
