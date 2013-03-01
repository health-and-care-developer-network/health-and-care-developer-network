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

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class RecurseMatcher extends AbstractMatcher
{
	@NotNull
	public static RecurseMatcher rootMatcher(@NotNull final Matcher... children)
	{
		return new RecurseMatcher("", children);
	}

	@NotNull
	public static Matcher recurse(@FieldTokenName @NotNull @NonNls final String name, @NotNull final Matcher... children)
	{
		return new RecurseMatcher(name, children);
	}

	@NotNull
	private final Map<String, Matcher> children;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	public RecurseMatcher(@FieldTokenName @NotNull @NonNls final String name, @NotNull final Matcher... children)
	{
		super(name);
		this.children = new HashMap<String, Matcher>()
		{
			{
				for (final Matcher child : children)
				{
					child.register(this);
				}
			}

			@Override
			@Nullable
			public Matcher put(@NotNull final String key, @NotNull final Matcher value)
			{
				if (super.put(key, value) != null)
				{
					throw new IllegalArgumentException(format(ENGLISH, "name %1$s is a duplicate", name));
				}
				return null;
			}
		};
	}

	@Override
	@NotNull
	public Matcher matchChild(@NotNull @NonNls final String name)
	{
		@Nullable final Matcher child = children.get(name);
		if (child == null)
		{
			throw new IllegalArgumentException(format(ENGLISH, "no matcher known for name %1$s", name));
		}
		return child;
	}

	@Override
	public void recordValue(@NotNull final String rawValue, @NotNull final SeparatedValuesLine separatedValuesLine)
	{
		throw new IllegalArgumentException(format(ENGLISH, "This matcher is not a leaf and can not record the value %1$s", rawValue));
	}

	@NotNull
	@Override
	public char[] separator()
	{
		throw new IllegalArgumentException("This matcher is not a leaf and can not have a separator");
	}
}
