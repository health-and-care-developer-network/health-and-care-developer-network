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

	// Only for toString
	private final String characters;

	public BitSetCharacterSet(final int fromInclusive, final int inclusiveTo)
	{
		bitSet = new boolean[MaximumCharacterNumber];
		final StringBuilder builder = new StringBuilder(inclusiveTo - fromInclusive + 1);
		for(int index = fromInclusive; index <= inclusiveTo; index++)
		{
			bitSet[index] = true;
			//noinspection NumericCastThatLosesPrecision
			builder.append((char) index);
		}
		characters = builder.toString();
	}

	public BitSetCharacterSet(@NotNull final char... characters)
	{
		bitSet = new boolean[MaximumCharacterNumber];
		final StringBuilder builder = new StringBuilder(characters.length);
		for (final char character : characters)
		{
			bitSet[(int) character] = true;
			builder.append(character);
		}
		this.characters = builder.toString();
	}

	public BitSetCharacterSet(@NotNull final BitSetCharacterSet existing, @NotNull final char... characters)
	{
		bitSet = copyOf(existing.bitSet, MaximumCharacterNumber);
		final StringBuilder builder = new StringBuilder(existing.characters);
		for (final char character : characters)
		{
			bitSet[(int) character] = true;
			builder.append(character);
		}
		this.characters = builder.toString();
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
