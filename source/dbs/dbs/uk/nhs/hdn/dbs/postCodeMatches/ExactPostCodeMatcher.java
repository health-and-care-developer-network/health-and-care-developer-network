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

package uk.nhs.hdn.dbs.postCodeMatches;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.postCodes.PostCode;

public final class ExactPostCodeMatcher extends AbstractToString implements PostCodeMatcher
{
	@NotNull
	private final PostCode postCode;

	public ExactPostCodeMatcher(@NotNull final PostCode postCode)
	{
		this.postCode = postCode;
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

		final ExactPostCodeMatcher that = (ExactPostCodeMatcher) obj;

		if (!postCode.equals(that.postCode))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return postCode.hashCode();
	}

	@NonNls
	@NotNull
	@Override
	public String pattern()
	{
		return postCode.normalised();
	}

	@Override
	public boolean matches(@NotNull final PostCode postCode)
	{
		return this.postCode.equals(postCode);
	}
}
