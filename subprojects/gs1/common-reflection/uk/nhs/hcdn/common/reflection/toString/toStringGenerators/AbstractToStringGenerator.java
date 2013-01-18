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

package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.VariableArgumentsHelper;

import java.util.Map;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.common.VariableArgumentsHelper.copyOf;

public abstract class AbstractToStringGenerator<T> extends AbstractToString implements ToStringGenerator<T>
{
	@NotNull
	private final Class<? extends T>[] registerFor;

	@SafeVarargs
	protected AbstractToStringGenerator(@NotNull final Class<? extends T>... registerFor)
	{
		this.registerFor = VariableArgumentsHelper.copyOf(registerFor);
	}

	@Override
	public final void register(@NotNull final Map<Class<?>, ToStringGenerator<?>> register)
	{
		for (final Class<? extends T> aClass : registerFor)
		{
			if (register.put(aClass, this) != null)
			{
				throw new IllegalStateException(format(ENGLISH, "Already registered for %1$s", aClass.getSimpleName()));
			}
		}
	}
}
