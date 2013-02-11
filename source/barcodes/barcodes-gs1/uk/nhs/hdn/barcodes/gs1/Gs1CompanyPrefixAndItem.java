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

package uk.nhs.hdn.barcodes.gs1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.digits.Digits;
import uk.nhs.hdn.barcodes.gs1.companyPrefixes.Gs1CompanyPrefix;
import uk.nhs.hdn.barcodes.gs1.gs1Prefixes.Gs1Prefix;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

public final class Gs1CompanyPrefixAndItem extends AbstractToString
{
	@NotNull
	private final Digits digits;

	public Gs1CompanyPrefixAndItem(@NotNull final Digits digits)
	{
		this.digits = digits;
	}

	public Gs1CompanyPrefixAndItem(@NotNull final Gs1CompanyPrefix gs1CompanyPrefix, @NotNull final Digits item)
	{
		digits = gs1CompanyPrefix.withDataAfterPrefix(item);
	}

	@NotNull
	public Gs1Prefix gs1Prefix()
	{
		return new Gs1Prefix(digits.firstThree());
	}

	@NotNull
	public Digits digitsWithoutGs1Prefix(final int withAMaximumOfTrailingDigits)
	{
		return digits.slice(4, 4 + withAMaximumOfTrailingDigits);
	}

	@NotNull
	public Digits item(@NotNull final Gs1CompanyPrefix gs1CompanyPrefix)
	{
		return gs1CompanyPrefix.extractDataAfterPrefix(digits);
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

		final Gs1CompanyPrefixAndItem that = (Gs1CompanyPrefixAndItem) obj;

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
