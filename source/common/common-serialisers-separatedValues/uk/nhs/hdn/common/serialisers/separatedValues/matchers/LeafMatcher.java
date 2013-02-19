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

import static java.lang.String.format;
import static java.util.Arrays.copyOf;
import static java.util.Locale.ENGLISH;

public final class LeafMatcher extends AbstractMatcher
{
	@NotNull
	private final char[] separator;

	@NotNull
	public static Matcher leaf(@FieldTokenName @NonNls @NotNull final String name, final int fieldIndex, @NotNull final char... separator)
	{
		return new LeafMatcher(name, fieldIndex, separator);
	}

	private final int fieldIndex;

	public LeafMatcher(@FieldTokenName@NonNls @NotNull final String name, final int fieldIndex, @NotNull final char... separator)
	{
		super(name);
		this.fieldIndex = fieldIndex;
		this.separator = copyOf(separator, separator.length);
	}

	@NotNull
	@Override
	public Matcher matchChild(@FieldTokenName @NotNull @NonNls final String name)
	{
		throw new IllegalArgumentException(format(ENGLISH, "This is a LeafMatcher, there is not a child %1$s", name));
	}

	@Override
	public void recordValue(@NotNull final String rawValue, @NotNull final SeparatedValuesLine separatedValuesLine)
	{
		separatedValuesLine.recordValue(fieldIndex, rawValue);
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	@NotNull
	@Override
	public char[] separator()
	{
		return separator;
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

		final LeafMatcher that = (LeafMatcher) obj;

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
}
