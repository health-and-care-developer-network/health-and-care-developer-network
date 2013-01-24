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

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

@SuppressWarnings("StandardVariableNames")
public final class Empty implements Tuple
{
	@NotNull
	public static final Empty Tuple = new Empty();

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static Empty empty()
	{
		return Tuple;
	}

	private Empty()
	{
	}

	@Override
	public int cardinality()
	{
		return 0;
	}

	@SuppressWarnings("MethodMayBeStatic")
	@NotNull
	public <A> Single<A> with(@NonNls @NotNull final A a)
	{
		return new Single<>(a);
	}

	@NotNull
	@Override
	public String toString()
	{
		return format(ENGLISH, "%1$s()", getClass().getSimpleName());
	}
}
