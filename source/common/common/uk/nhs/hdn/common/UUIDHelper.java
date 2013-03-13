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

import java.util.UUID;

public final class UUIDHelper
{
	@SuppressWarnings({"NumericCastThatLosesPrecision", "MagicNumber"})
	@NotNull
	public static byte[] uuidToByteArray(@NotNull final UUID uuid)
	{
		final long mostSignificantBits = uuid.getMostSignificantBits();
		final long leastSignificantBits = uuid.getLeastSignificantBits();

		return new byte[]
		{
			(byte) (mostSignificantBits >>> 56),
			(byte) (mostSignificantBits >>> 48),
			(byte) (mostSignificantBits >>> 40),
			(byte) (mostSignificantBits >>> 32),
			(byte) (mostSignificantBits >>> 24),
			(byte) (mostSignificantBits >>> 16),
			(byte) (mostSignificantBits >>> 8),
			(byte) mostSignificantBits,
			(byte) (leastSignificantBits >>> 56),
			(byte) (leastSignificantBits >>> 48),
			(byte) (leastSignificantBits >>> 40),
			(byte) (leastSignificantBits >>> 32),
			(byte) (leastSignificantBits >>> 24),
			(byte) (leastSignificantBits >>> 16),
			(byte) (leastSignificantBits >>> 8),
			(byte) leastSignificantBits
		};
	}

	private UUIDHelper()
	{
	}
}
