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

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

@SuppressWarnings("StandardVariableNames")
public class Triple<A, B, C> implements Tuple
{
	@SuppressWarnings({"ClassEscapesDefinedScope", "PublicField"})
	@NotNull
	public final A a;

	@SuppressWarnings({"ClassEscapesDefinedScope", "PublicField"})
	@NotNull
	public final B b;

	@SuppressWarnings({"ClassEscapesDefinedScope", "PublicField"})
	@NotNull
	public final C c;

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <A, B, C> Triple<A, B, C> triple(@NotNull final A a, @NotNull final B b, @NotNull final C c)
	{
		return new Triple<>(a, b, c);
	}

	public Triple(@NotNull final A a, @NotNull final B b, @NotNull final C c)
	{
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public int cardinality()
	{
		return 3;
	}

	@NotNull
	public final <D> Quadruple<A, B, C, D> with(@NonNls @NotNull final D d)
	{
		return new Quadruple<>(a, b, c, d);
	}

	@SuppressWarnings("RefusedBequest")
	@NotNull
	@Override
	public String toString()
	{
		return format(ENGLISH, "%1$s(%2$s, %3$s, %4$s)", getClass().getSimpleName(), a, b, c);
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

		final Triple<?, ?, ?> triple = (Triple<?, ?, ?>) obj;

		if (!a.equals(triple.a))
		{
			return false;
		}
		if (!b.equals(triple.b))
		{
			return false;
		}
		if (!c.equals(triple.c))
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
		result = 31 * result + c.hashCode();
		return result;
	}
}
