/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.tuples;

import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("StandardVariableNames")
public class Pair<A, B> extends AbstractToString
{
	@SuppressWarnings({"ClassEscapesDefinedScope", "PublicField"})
	@NotNull
	public final A a;

	@SuppressWarnings({"ClassEscapesDefinedScope", "PublicField"})
	@NotNull
	public final B b;

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <A, B> Pair<A, B> pair(@NotNull final A a, @NotNull final B b)
	{
		return new Pair<>(a, b);
	}

	public Pair(@NotNull final A a, @NotNull final B b)
	{
		this.a = a;
		this.b = b;
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
