/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public final class VariableArgumentsHelper
{
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
