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

public final class LongHelper
{
	@SuppressWarnings("NumericCastThatLosesPrecision")
	public static long power(final long x, @SuppressWarnings("StandardVariableNames") final long n)
	{
		return (long) pow((double) x, (double) n);
	}

	public static boolean isEven(final long value)
	{
		return value % 2L == 0L;
	}

	@SuppressWarnings({"NumericCastThatLosesPrecision", "MagicNumber"})
	@NotNull
	public static byte[] signedLongToByteArray(final long value)
	{
		return new byte[]
		{
			(byte) (value >>> 54),
			(byte) (value >>> 48),
			(byte) (value >>> 40),
			(byte) (value >>> 32),
			(byte) (value >>> 24),
			(byte) (value >>> 16),
			(byte) (value >>> 8),
			(byte) value
		};
	}

	private LongHelper()
	{
	}
}
