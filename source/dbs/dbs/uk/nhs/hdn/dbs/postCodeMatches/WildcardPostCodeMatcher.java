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
import uk.nhs.hdn.common.postCodes.PostCode;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class WildcardPostCodeMatcher extends AbstractToString implements PostCodeMatcher
{
	@NonNls
	@NotNull
	private final String leadingPatternExcludingAsterisk;

	public WildcardPostCodeMatcher(@NotNull @NonNls final String leadingPatternExcludingAsterisk)
	{
		if (leadingPatternExcludingAsterisk.length() < 2)
		{
			throw new IllegalArgumentException(format(ENGLISH, "leadingPatternExcludingAsterisk '%1$s' must be at least 2 characters", leadingPatternExcludingAsterisk));
		}
		this.leadingPatternExcludingAsterisk = leadingPatternExcludingAsterisk;
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

		final WildcardPostCodeMatcher that = (WildcardPostCodeMatcher) obj;

		if (!leadingPatternExcludingAsterisk.equals(that.leadingPatternExcludingAsterisk))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return leadingPatternExcludingAsterisk.hashCode();
	}

	@NonNls
	@NotNull
	@Override
	public String pattern()
	{
		return leadingPatternExcludingAsterisk + '*';
	}

	@Override
	public boolean matches(@NotNull final PostCode postCode)
	{
		return postCode.normalised().startsWith(leadingPatternExcludingAsterisk);
	}
}
