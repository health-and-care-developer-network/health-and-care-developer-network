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

package uk.nhs.hcdn.barcodes.gs1.companyPrefixes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.barcodes.gs1.checkDigits.IncorrectNumberOfDigitsIllegalStateException;
import uk.nhs.hcdn.barcodes.gs1.gs1Prefixes.Gs1Prefix;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.serialisers.*;

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
