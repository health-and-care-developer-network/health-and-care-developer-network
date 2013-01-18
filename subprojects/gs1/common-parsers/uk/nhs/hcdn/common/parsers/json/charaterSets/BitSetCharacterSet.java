/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.charaterSets;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;

import java.util.Arrays;

import static java.util.Arrays.copyOf;

public final class BitSetCharacterSet extends AbstractToString implements CharacterSet
{
	private static final int MaximumCharacterNumber = 65535;

	@ExcludeFromToString
	private final boolean[] bitSet;

	public BitSetCharacterSet(final int fromInclusive, final int inclusiveTo)
	{
		bitSet = new boolean[MaximumCharacterNumber];
		for(int index = fromInclusive; index <= inclusiveTo; index++)
		{
			bitSet[index] = true;
		}
	}

	public BitSetCharacterSet(@NotNull final char... characters)
	{
		bitSet = new boolean[MaximumCharacterNumber];
		for (final char character : characters)
		{
			bitSet[(int) character] = true;
		}
	}

	public BitSetCharacterSet(@NotNull final BitSetCharacterSet existing, @NotNull final char... characters)
	{
		bitSet = copyOf(existing.bitSet, MaximumCharacterNumber);
		for (final char character : characters)
		{
			bitSet[(int) character] = true;
		}
	}

	@Override
	public boolean contains(final char value)
	{
		return bitSet[(int) value];
	}

	@Override
	public boolean doesNotContain(final char value)
	{
		return !contains(value);
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

		final BitSetCharacterSet that = (BitSetCharacterSet) obj;

		if (!Arrays.equals(bitSet, that.bitSet))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(bitSet);
	}
}
