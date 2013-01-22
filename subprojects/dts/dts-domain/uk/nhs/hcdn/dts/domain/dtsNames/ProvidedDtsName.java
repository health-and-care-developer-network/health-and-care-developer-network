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

package uk.nhs.hcdn.dts.domain.dtsNames;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.dts.common.AbstractIsUnknownObject;

public final class ProvidedDtsName extends AbstractIsUnknownObject implements DtsName
{
	@NonNls
	@NotNull
	private final String assumedToBeValidDtsName;

	public ProvidedDtsName(@NonNls @NotNull final String assumedToBeValidDtsName)
	{
		super(false);
		this.assumedToBeValidDtsName = assumedToBeValidDtsName;
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
		if (!super.equals(obj))
		{
			return false;
		}

		final ProvidedDtsName that = (ProvidedDtsName) obj;

		if (!assumedToBeValidDtsName.equals(that.assumedToBeValidDtsName))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + assumedToBeValidDtsName.hashCode();
		return result;
	}
}
