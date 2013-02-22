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

package uk.nhs.hdn.ckan.domain.dates;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UnrecognisedDate extends AbstractDate
{
	@NotNull @NonNls private final String unrecognisedDateFormatString;

	public UnrecognisedDate(@NotNull @NonNls final String unrecognisedDateFormatString)
	{
		super(true);
		this.unrecognisedDateFormatString = unrecognisedDateFormatString;
	}

	@NotNull
	@Override
	public String toString()
	{
		return unrecognisedDateFormatString;
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

		final UnrecognisedDate that = (UnrecognisedDate) obj;

		if (!unrecognisedDateFormatString.equals(that.unrecognisedDateFormatString))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return unrecognisedDateFormatString.hashCode();
	}
}
