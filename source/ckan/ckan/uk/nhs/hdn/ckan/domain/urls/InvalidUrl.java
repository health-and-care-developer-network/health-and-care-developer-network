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

package uk.nhs.hdn.ckan.domain.urls;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;

public final class InvalidUrl extends AbstractUrl
{
	@NotNull @NonNls public final String value;
	@NotNull public final MalformedURLException cause;

	public InvalidUrl(@NotNull @NonNls final String value, @NotNull final MalformedURLException cause)
	{
		super(false);
		this.value = value;
		this.cause = cause;
	}

	@NotNull
	@Override
	public String stringValue()
	{
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

		final InvalidUrl that = (InvalidUrl) obj;

		if (!cause.equals(that.cause))
		{
			return false;
		}
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
		result = 31 * result + cause.hashCode();
		return result;
	}
}
