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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

@SuppressWarnings("StandardVariableNames")
public class Single<A> implements Tuple
{
	@SuppressWarnings({"ClassEscapesDefinedScope", "PublicField"})
	@NotNull
	public final A a;

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <A> Single<A> single(@NotNull final A a)
	{
		return new Single<>(a);
	}

	public Single(@NotNull final A a)
	{
		this.a = a;
	}

	@NotNull
	@Override
	public String toString()
	{
		return format(ENGLISH, "%1$s(%2$s)", getClass().getSimpleName(), a);
	}

	@Override
	public int cardinality()
	{
		return 1;
	}

	@NotNull
	public final <B> Pair<A, B> with(@NonNls @NotNull final B b)
	{
		return new Pair<>(a, b);
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

		final Single<?> single = (Single<?>) obj;

		if (!a.equals(single.a))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return a.hashCode();
	}
}
