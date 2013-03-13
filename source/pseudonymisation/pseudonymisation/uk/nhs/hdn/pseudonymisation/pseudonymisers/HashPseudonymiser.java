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
import uk.nhs.hdn.common.MessageDigestHelper;
import uk.nhs.hdn.common.SecureRandomHelper;
import uk.nhs.hdn.common.naming.Normalisable;
import uk.nhs.hdn.pseudonymisation.DuplicatePsuedonymisedValueException;
import uk.nhs.hdn.pseudonymisation.IndexTable;
import uk.nhs.hdn.pseudonymisation.PsuedonymisedValue;

import java.security.SecureRandom;

import static java.util.Arrays.copyOfRange;
import static uk.nhs.hdn.common.CharsetHelper.Utf8;

public final class HashPseudonymiser<N extends Normalisable> extends AbstractPseudonymiser<N>
{
	private final int mostSignificantBytesToKeep;
	@NotNull private final MessageDigestHelper messageDigestHelper;
	@NotNull private final PsuedonymisedValue bytesForNull;
	@NotNull private final SecureRandomHelper secureRandomHelper;
	private final int messageDigestLength;

	public HashPseudonymiser(@NotNull final MessageDigestHelper messageDigestHelper)
	{
		this(messageDigestHelper.digestLength(), messageDigestHelper);
	}

	public HashPseudonymiser(final int mostSignificantBytesToKeep, @NotNull final MessageDigestHelper messageDigestHelper)
	{
		super(mostSignificantBytesToKeep);
		messageDigestLength = messageDigestHelper.digestLength();
		if (mostSignificantBytesToKeep < 4 || mostSignificantBytesToKeep > messageDigestLength)
		{
			throw new IllegalArgumentException("mostSignificantBytesToKeep must be between 4 and 64 (the output size of SHA-512)");
		}
		this.mostSignificantBytesToKeep = mostSignificantBytesToKeep;
		this.messageDigestHelper = messageDigestHelper;
		secureRandomHelper = new SecureRandomHelper(new SecureRandom());
		bytesForNull = new PsuedonymisedValue(secureRandomHelper.randomBytes(size()));
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public void pseudonymise(@Nullable final N valueToPsuedonymise, @NotNull final IndexTable<N> indexTable)
	{
		if (indexTable.has(valueToPsuedonymise, this))
		{
			return;
		}

		// NOTE: We do not use the digest of an empty byte array, as this is different and well-known
		if (valueToPsuedonymise == null)
		{
			try
			{
				indexTable.add(valueToPsuedonymise, this, bytesForNull);
			}
			catch (DuplicatePsuedonymisedValueException e)
			{
				throw new IllegalStateException("Duplicate sequenceValueAsBytes for null", e);
			}
			return;
		}

		final byte[] salt = salt();

		// NOTE: SQL Server examples use nvarchar conversion, which is UCS-2. Stuff that legacy nonsense.
		final byte[] hash = messageDigestHelper.saltAndValueDigest(salt, valueToPsuedonymise.normalised().getBytes(Utf8));

		int collisionOffset = 0;
		while (collisionOffset + mostSignificantBytesToKeep <= messageDigestLength)
		{
			final byte[] sequenceValueAsBytes = copyOfRange(hash, collisionOffset, collisionOffset + mostSignificantBytesToKeep);
			final PsuedonymisedValue psuedonymisedValue = new PsuedonymisedValue(salt, sequenceValueAsBytes);
			try
			{
				indexTable.add(valueToPsuedonymise, this, psuedonymisedValue);
				return;
			}
			catch (DuplicatePsuedonymisedValueException ignored)
			{
				collisionOffset++;
			}
		}
		throw new IllegalArgumentException("Too many collisions");
	}

	private byte[] salt()
	{
		return secureRandomHelper.randomBytes(messageDigestLength);
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

		final HashPseudonymiser<?> that = (HashPseudonymiser<?>) obj;

		if (mostSignificantBytesToKeep != that.mostSignificantBytesToKeep)
		{
			return false;
		}
		if (!bytesForNull.equals(that.bytesForNull))
		{
			return false;
		}
		if (messageDigestHelper != that.messageDigestHelper)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = mostSignificantBytesToKeep;
		result = 31 * result + messageDigestHelper.hashCode();
		result = 31 * result + bytesForNull.hashCode();
		return result;
	}
}
