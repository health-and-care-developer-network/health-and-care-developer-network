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

package uk.nhs.hdn.barcodes.gs1.companyPrefixes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.digits.Digits;
import uk.nhs.hdn.common.digits.IncorrectNumberOfDigitsIllegalStateException;
import uk.nhs.hdn.barcodes.gs1.gs1Prefixes.Gs1Prefix;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.*;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public abstract class AbstractCompanyPrefix extends AbstractToString implements ValueSerialisable
{
	@NotNull
	protected final Digits digits;

	protected AbstractCompanyPrefix(@NotNull final Digits digits)
	{
		if (digits.size() < 4)
		{
			throw new IncorrectNumberOfDigitsIllegalStateException(4);
		}
		this.digits = digits;
	}

	@Override
	public final void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		digits.serialiseValue(valueSerialiser);
	}

	@NotNull
	public final Gs1Prefix gs1Prefix()
	{
		return new Gs1Prefix(digits.slice(1, 4));
	}

	@NotNull
	public final Digits digitsWithoutGs1Prefix()
	{
		return digits.slice(4, digits.size() + 1);
	}

	@SuppressWarnings("FeatureEnvy")
	@NotNull
	public Digits extractDataAfterPrefix(@NotNull final Digits digits)
	{
		if (!digits.hasSizeBetween(this.digits.size() + 1, this.digits.size() + 100))
		{
			throw new IllegalArgumentException(format(ENGLISH, "%1$s does not prefix %2$s because size is different", this, digits));
		}
		final int size = this.digits.size();
		for(int index = 0; index < size; index++)
		{
			if (digits.digitAt(index) != this.digits.digitAt(index))
			{
				throw new IllegalArgumentException(format(ENGLISH, "%1$s does not prefix %2$s", this, digits));
			}
		}
		return digits.slice(this.digits.size() + 1, digits.size() + 1);
	}

	@NotNull
	public Digits withDataAfterPrefix(@NotNull final Digits digits)
	{
		return this.digits.add(digits);
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

		final AbstractCompanyPrefix that = (AbstractCompanyPrefix) obj;

		if (!digits.equals(that.digits))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return digits.hashCode();
	}
}
