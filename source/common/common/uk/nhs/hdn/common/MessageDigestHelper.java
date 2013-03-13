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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hdn.common.naming.ActualName;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.security.MessageDigest.getInstance;

public enum MessageDigestHelper implements ActualName
{
	SHA512("SHA-512"),
	;

	@NotNull private final String javaName;
	@SuppressWarnings("NonSerializableFieldInSerializableClass") private final MessageDigest messageDigest;

	MessageDigestHelper(@NotNull @NonNls final String javaName)
	{
		this.javaName = javaName;
		try
		{
			messageDigest = getInstance(javaName);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new ShouldNeverHappenException(e);
		}
	}

	@NotNull
	public MessageDigest messageDigest()
	{
		try
		{
			return (MessageDigest) messageDigest.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new IllegalStateException(e);
		}
	}

	@NotNull
	@Override
	public String actualName()
	{
		return javaName;
	}

	@Override
	public String toString()
	{
		return javaName;
	}

	public int digestLength()
	{
		return messageDigest.getDigestLength();
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	public byte[] saltAndValueDigest(@NotNull final byte[] salt, @NotNull final byte[] values)
	{
		@SuppressWarnings("LocalVariableHidesMemberVariable") final MessageDigest messageDigest = messageDigest();
		messageDigest.update(salt);
		return messageDigest.digest(values);
	}
}
