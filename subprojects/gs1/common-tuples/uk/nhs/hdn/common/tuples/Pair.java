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

package uk.nhs.hdn.common.tuples;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

@SuppressWarnings("StandardVariableNames")
public class Pair<A, B> implements Tuple, Map.Entry<A, B>
{
	@SuppressWarnings({"ClassEscapesDefinedScope", "PublicField"})
	@NotNull
	public final A a;

	@NonNls
	@SuppressWarnings({"ClassEscapesDefinedScope", "PublicField"})
	@NotNull
	public final B b;

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <A, B> Pair<A, B> pair(@NonNls @NotNull final A a, @NonNls @NotNull final B b)
	{
		return new Pair<>(a, b);
	}

	public Pair(@NotNull final A a, @NotNull final B b)
	{
		this.a = a;
		this.b = b;
	}

	public Pair(@NotNull final Map.Entry<A, B> entry)
	{
		@Nullable final A key = entry.getKey();
		@Nullable final B value = entry.getValue();
		if (key == null || value == null)
		{
			throw new IllegalArgumentException("entry must have non-null key and value");
		}
		a = key;
		b = value;
	}

	@NotNull
	@Override
	public String toString()
	{
		return format(ENGLISH, "%1$s(%2$s, %3$s)", getClass().getSimpleName(), a, b);
	}

	@Override
	public int cardinality()
	{
		return 2;
	}

	@NotNull
	public final <C> Triple<A, B, C> with(@NotNull final C c)
	{
		return new Triple<>(a, b, c);
	}

	public final void putUniquelyInMap(@NotNull final Map<A, B> map)
	{
		if (map.put(a, b) != null)
		{
			throw new IllegalArgumentException("Already present in map");
		}
	}

	@Override
	public A getKey()
	{
		return a;
	}

	@Override
	@NotNull
	public B getValue()
	{
		return b;
	}

	@Override
	public B setValue(final B value)
	{
		throw new UnsupportedOperationException("Immutable");
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

		final Pair<?, ?> pair = (Pair<?, ?>) obj;

		if (!a.equals(pair.a))
		{
			return false;
		}
		if (!b.equals(pair.b))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = a.hashCode();
		result = 31 * result + b.hashCode();
		return result;
	}
}
