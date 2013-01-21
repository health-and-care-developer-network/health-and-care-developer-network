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

package uk.nhs.hcdn.barcodes.gs1.gs1Prefixes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.barcodes.Digits;
import uk.nhs.hcdn.common.comparison.ComparisonResult;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.tuples.ComparablePair;

import static uk.nhs.hcdn.common.tuples.ComparablePair.comparablePair;

public final class Gs1Prefix extends AbstractToString implements Comparable<Gs1Prefix>
{
	@NotNull
	private final Digits threeDigits;

	@NotNull
	private final Gs1PrefixAssignment gs1PrefixAssignment;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	public Gs1Prefix(@NotNull final Digits threeDigits)
	{
		this.threeDigits = threeDigits;
		gs1PrefixAssignment = KnownGs1PrefixAssignment.gs1PrefixAssignment(this);
	}

	@NotNull
	public Gs1PrefixAssignment gs1PrefixAssignment()
	{
		return gs1PrefixAssignment;
	}

	@NotNull
	public ComparablePair<Gs1Prefix> to(@NotNull final Digits digits)
	{
		return comparablePair(this, new Gs1Prefix(digits));
	}

	@Override
	@ComparisonResult
	public int compareTo(@NotNull final Gs1Prefix o)
	{
		return threeDigits.compareTo(o.threeDigits);
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

		final Gs1Prefix that = (Gs1Prefix) obj;

		if (!threeDigits.equals(that.threeDigits))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return threeDigits.hashCode();
	}

	public int to0To999()
	{
		return threeDigits.to0To999();
	}
}
