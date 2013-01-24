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

package uk.nhs.hcdn.common.tuples;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

@SuppressWarnings("StandardVariableNames")
public class Quadruple<A, B, C, D> implements Tuple
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

	@SuppressWarnings({"ClassEscapesDefinedScope", "PublicField"})
	@NotNull
	public final D d;

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <A, B, C, D> Quadruple<A, B, C, D> quadruple(@NotNull final A a, @NotNull final B b, @NotNull final C c, @NotNull final D d)
	{
		return new Quadruple<>(a, b, c, d);
	}

	public Quadruple(@NotNull final A a, @NotNull final B b, @NotNull final C c, @NotNull final D d)
	{
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	@Override
	public int cardinality()
	{
		return 4;
	}

	@SuppressWarnings("RefusedBequest")
	@NotNull
	@Override
	public String toString()
	{
		return format(ENGLISH, "%1$s(%2$s, %3$s, %4$s, %5$s)", getClass().getSimpleName(), a, b, c, d);
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

		final Quadruple<?, ?, ?, ?> that = (Quadruple<?, ?, ?, ?>) obj;

		if (!a.equals(that.a))
		{
			return false;
		}
		if (!b.equals(that.b))
		{
			return false;
		}
		if (!c.equals(that.c))
		{
			return false;
		}
		if (!d.equals(that.d))
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
		result = 31 * result + d.hashCode();
		return result;
	}
}
