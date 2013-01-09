package org.gov.data.nhs.hcdn.common;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.copyOf;

public final class EnumeratedVariableArgumentsHelper
{
	@SafeVarargs
	@NotNull
	public static <E extends Enum<E>> Set<E> unmodifiableSetOf(@NotNull final E... oneOrMoreEnumerationConstants)
	{
		return unmodifiableSet(copyOf(asList(oneOrMoreEnumerationConstants)));
	}

	private EnumeratedVariableArgumentsHelper()
	{
	}
}
