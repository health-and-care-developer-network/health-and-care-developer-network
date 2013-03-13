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

package uk.nhs.hdn.pseudonymisation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.util.Arrays;

import static java.util.Arrays.copyOf;
import static uk.nhs.hdn.common.ByteArrayHelper.toBase16Hexadecimal;

public final class PsuedonymisedValue extends AbstractToString
{
	@Nullable private static final byte[] NoSalt = null;

	@NotNull private final byte[] value;
	@Nullable private final byte[] salt;

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	public PsuedonymisedValue(@NotNull final byte[] value)
	{
		this(value, NoSalt);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	public PsuedonymisedValue(@NotNull final byte[] value, @Nullable final byte[] salt)
	{
		this.value = copyOf(value, value.length);
		this.salt = salt == null ? NoSalt : copyOf(salt, salt.length);
	}

	@NotNull
	public byte[] value()
	{
		return copyOf(value, value.length);
	}

	@Nullable
	public byte[] salt()
	{
		if (salt == null)
		{
			return null;
		}
		return copyOf(salt, salt.length);
	}

	@NotNull
	public char[] valueAsBase16Hexadecimal()
	{
		return toBase16Hexadecimal(value);
	}

	@Nullable
	public char[] saltAsBase16Hexadecimal()
	{
		if (salt == null)
		{
			return null;
		}
		return toBase16Hexadecimal(salt);
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final PsuedonymisedValue that = (PsuedonymisedValue) obj;

		if (!Arrays.equals(value, that.value))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(value);
	}

	public boolean hasSalt()
	{
		return salt != null;
	}
}
