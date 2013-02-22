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

package uk.nhs.hdn.common;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public final class VariableArgumentsHelper
{
	@SafeVarargs
	@NotNull
	public static <E> E[] of(@NonNls @NotNull final E... values)
	{
		return values;
	}

	@SafeVarargs
	@NotNull
	public static <E> E[] copyOf(@NotNull final E... values)
	{
		final int length = values.length;
		if (length == 0)
		{
			return values;
		}
		return Arrays.copyOf(values, length);
	}

	@SafeVarargs
	@NotNull
	public static <E> Set<E> unmodifiableSetOf(@NotNull final E... values)
	{
		return unmodifiableSet(setOf(values));
	}

	@SuppressWarnings("ClassExtendsConcreteCollection")
	@SafeVarargs
	@NotNull
	public static <E> Set<E> setOf(final E... values)
	{
		return new HashSet<E>(values.length)
		{{
			for (final E value : values)
			{
				add(value);
			}
		}};
	}

	private VariableArgumentsHelper()
	{
	}
}
