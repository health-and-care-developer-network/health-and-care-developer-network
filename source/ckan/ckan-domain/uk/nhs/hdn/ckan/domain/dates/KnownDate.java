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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;

import java.util.Date;

public final class KnownDate extends AbstractDate
{
	@MillisecondsSince1970 public final long firstMillisecondOfDay;

	public KnownDate(@MillisecondsSince1970 final long firstMillisecondOfDay)
	{
		super(false);
		this.firstMillisecondOfDay = firstMillisecondOfDay;
	}

	@NotNull
	@Override
	public String toString()
	{
		final Date date = new Date(firstMillisecondOfDay);
		return simpleDateFormat().format(date);
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

		final KnownDate date = (KnownDate) obj;

		if (firstMillisecondOfDay != date.firstMillisecondOfDay)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return (int) (firstMillisecondOfDay ^ (firstMillisecondOfDay >>> 32));
	}
}
