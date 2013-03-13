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

import java.security.SecureRandom;

public final class SecureRandomHelper
{
	@NotNull private final SecureRandom secureRandom;

	public SecureRandomHelper(@NotNull final SecureRandom secureRandom)
	{
		this.secureRandom = secureRandom;
	}

	@NotNull
	public byte[] randomBytes(final int number)
	{
		final byte[] randomBytes = new byte[number];
		secureRandom.nextBytes(randomBytes);
		return randomBytes;
	}
}
