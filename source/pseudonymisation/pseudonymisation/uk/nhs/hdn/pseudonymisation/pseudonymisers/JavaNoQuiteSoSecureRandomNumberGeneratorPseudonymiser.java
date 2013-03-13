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

package uk.nhs.hdn.pseudonymisation.pseudonymisers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.SecureRandomHelper;
import uk.nhs.hdn.common.naming.Normalisable;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;
import uk.nhs.hdn.pseudonymisation.DuplicatePsuedonymisedValueException;
import uk.nhs.hdn.pseudonymisation.IndexTable;
import uk.nhs.hdn.pseudonymisation.PsuedonymisedValue;

import java.security.SecureRandom;
import java.util.Arrays;

public final class JavaNoQuiteSoSecureRandomNumberGeneratorPseudonymiser<N extends Normalisable> extends AbstractPseudonymiser<N>
{
	@NotNull @ExcludeFromToString private final SecureRandomHelper secureRandomHelper;
	@NotNull @ExcludeFromToString private final byte[] randomBytesForNull;

	// WARNING! The Java CSPRNG on Windows uses CRYPT_GEN_*, which has been broken in the past
	// WARNING! THe default Java CSPRNG is to use a SHA1 based generator
	// You are strongly advised to use a CSPRNG implementation either based on Yarrow256 (BSD can get Yarrow160 from /dev/urandom) or use Linux's /dev/urandom instead. These are not platform portable but they are better implementations with a lot of eyes on them. Don't be lazy, deal with the real world! Alternatively, use data sourced from random.org or hardware.
	public JavaNoQuiteSoSecureRandomNumberGeneratorPseudonymiser(final int size)
	{
		this(size, new SecureRandom());
	}

	public JavaNoQuiteSoSecureRandomNumberGeneratorPseudonymiser(final int size, @NotNull final SecureRandom secureRandom)
	{
		super(size, false);
		if (size < 4)
		{
			throw new IllegalArgumentException("size should be at least 4");
		}
		secureRandomHelper = new SecureRandomHelper(secureRandom);
		randomBytesForNull = getRandomBytes();
	}

	private byte[] getRandomBytes()
	{
		return secureRandomHelper.randomBytes(size());
	}

	@Override
	public void pseudonymise(@Nullable final N valueToPsuedonymise, @NotNull final IndexTable<N> indexTable)
	{
		if (indexTable.has(valueToPsuedonymise, this))
		{
			return;
		}

		do
		{
			@SuppressWarnings("VariableNotUsedInsideIf") final byte[] randomBytes = valueToPsuedonymise == null ? randomBytesForNull : getRandomBytes();

			final PsuedonymisedValue psuedonymisedValue = new PsuedonymisedValue(randomBytes);
			try
			{
				indexTable.add(valueToPsuedonymise, this, psuedonymisedValue);
				return;
			}
			catch (DuplicatePsuedonymisedValueException e)
			{
				if (valueToPsuedonymise == null && Arrays.equals(randomBytes, randomBytesForNull))
				{
					throw new IllegalStateException("Duplicate randomBytes for null", e);
				}
			}
		}
		while (true);
	}
}
