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

package uk.nhs.hdn.barcodes.gs1.gs1Prefixes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.digits.Digit;
import uk.nhs.hdn.common.digits.Digits;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import static uk.nhs.hdn.common.digits.Digits.digits;

public final class UnknownGs1PrefixAssignment extends AbstractToString implements Gs1PrefixAssignment
{
	@NotNull
	private final Digits digits;

	public UnknownGs1PrefixAssignment(final int prefix)
	{
		digits = digits(Integer.toString(prefix));
	}

	@Override
	public boolean isISMN(@NotNull final Digit third, @NotNull final Digit fourth)
	{
		return false;
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

		final UnknownGs1PrefixAssignment that = (UnknownGs1PrefixAssignment) obj;

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

	@NotNull
	@Override
	public String actualName()
	{
		return "(Unknown)";
	}
}
