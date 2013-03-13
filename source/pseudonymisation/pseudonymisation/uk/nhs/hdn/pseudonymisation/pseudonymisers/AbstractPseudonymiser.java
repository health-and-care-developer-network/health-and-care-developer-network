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

import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.naming.Normalisable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;

public abstract class AbstractPseudonymiser<N extends Normalisable> extends AbstractToString implements Pseudonymiser<N>
{
	@ExcludeFromToString private final int size;
	@ExcludeFromToString private final boolean hasSalt;

	protected AbstractPseudonymiser(final int size, final boolean hasSalt)
	{
		this.size = size;
		this.hasSalt = hasSalt;
	}

	@Override
	public final int size()
	{
		return size;
	}

	@Override
	public boolean hasSalt()
	{
		return hasSalt;
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

		final AbstractPseudonymiser<?> that = (AbstractPseudonymiser<?>) obj;

		if (hasSalt != that.hasSalt)
		{
			return false;
		}
		if (size != that.size)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = size;
		result = 31 * result + (hasSalt ? 1 : 0);
		return result;
	}
}
