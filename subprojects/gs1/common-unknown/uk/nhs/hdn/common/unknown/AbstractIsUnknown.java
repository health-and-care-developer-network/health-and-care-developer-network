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

package uk.nhs.hdn.common.unknown;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public class AbstractIsUnknown extends AbstractToString implements IsUnknown
{
	@ExcludeFromToString
	private final boolean isUnknown;

	protected AbstractIsUnknown(final boolean isUnknown)
	{
		this.isUnknown = isUnknown;
	}

	@Override
	@NotNull
	public String toString()
	{
		if (isUnknown)
		{
			return format(ENGLISH, "Unknown%1$s", getClass().getSimpleName());
		}
		return super.toString();
	}

	@Override
	public final boolean isUnknown()
	{
		return isUnknown;
	}

	@Override
	public final boolean isKnown()
	{
		return !isUnknown;
	}

	@Override
	public final boolean isSameKnownessAs(@NotNull final IsUnknown that)
	{
		if (isUnknown)
		{
			return that.isUnknown();
		}
		else
		{
			return that.isKnown();
		}
	}

	@Override
	public final boolean isDifferentKnownessAs(@NotNull final IsUnknown that)
	{
		return !isSameKnownessAs(that);
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

		final AbstractIsUnknown that = (AbstractIsUnknown) obj;

		if (isUnknown != that.isUnknown)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return isUnknown ? 1 : 0;
	}
}
