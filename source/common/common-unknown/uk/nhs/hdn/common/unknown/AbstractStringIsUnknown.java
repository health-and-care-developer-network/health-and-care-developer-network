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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractStringIsUnknown extends AbstractIsUnknown
{
	@NonNls
	@NotNull
	private final String value;

	protected AbstractStringIsUnknown(@NonNls @NotNull final String value)
	{
		super(value.isEmpty());
		this.value = value;
	}

	@NotNull
	public final String value()
	{
		if (isUnknown())
		{
			throw new IsUnknownException();
		}
		return value;
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

		final AbstractStringIsUnknown that = (AbstractStringIsUnknown) obj;

		if (!value.equals(that.value))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + value.hashCode();
		return result;
	}
}
