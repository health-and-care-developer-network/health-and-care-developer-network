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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public final class KnownUrl extends AbstractUrl
{
	@NotNull
	public final URL value;

	public KnownUrl(@NotNull final URL value)
	{
		super(false);
		this.value = value;
	}

	@NotNull
	@Override
	public String stringValue()
	{
		return value.toString();
	}

	@SuppressWarnings({"ChainedMethodCall", "CallToStringEquals"})
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

		final KnownUrl that = (KnownUrl) obj;

		if (!value.toString().equals(that.value.toString()))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return value.toString().hashCode();
	}
}
