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

package uk.nhs.hdn.common.serialisers.separatedValues.matchers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.serialisers.FieldTokenName;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValuesLine;

public final class IgnoreChildrenMatcher extends AbstractMatcher
{
	@NotNull
	private static final char[] Empty = new char[0];

	@NotNull
	public static IgnoreChildrenMatcher ignoreChildren(@FieldTokenName @NotNull @NonNls final String name, final int fieldIndex)
	{
		return new IgnoreChildrenMatcher(name, fieldIndex);
	}

	private final int fieldIndex;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	public IgnoreChildrenMatcher(@FieldTokenName @NotNull @NonNls final String name, final int fieldIndex)
	{
		super(name);
		this.fieldIndex = fieldIndex;
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

		final IgnoreChildrenMatcher that = (IgnoreChildrenMatcher) obj;

		if (fieldIndex != that.fieldIndex)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + fieldIndex;
		return result;
	}

	@Override
	@NotNull
	public Matcher matchChild(@NotNull @NonNls final String name)
	{
		return this;
	}

	@Override
	public void recordValue(@NotNull final String rawValue, @NotNull final SeparatedValuesLine separatedValuesLine)
	{
	}

	@NotNull
	@Override
	public char[] separator()
	{
		return Empty;
	}
}
