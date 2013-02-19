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

package uk.nhs.hdn.common.reflection.toString.delegates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.exceptions.ShouldNeverHappenException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class ConstructorDelegate<V> implements Delegate<V>
{
	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <V> Delegate<V> constructorDelegate(@NotNull final Class<V> classToConstruct, @NotNull final Class<?>... parameterTypes)
	{
		final Constructor<V> constructor;
		try
		{
			constructor = classToConstruct.getConstructor(parameterTypes);
		}
		catch (NoSuchMethodException e)
		{
			throw new IllegalStateException(e);
		}
		return new ConstructorDelegate<>(constructor);
	}

	@NotNull
	private final Constructor<V> constructor;

	public ConstructorDelegate(@NotNull final Constructor<V> constructor)
	{
		this.constructor = constructor;
		constructor.setAccessible(true);
	}

	@Nullable
	@Override
	public V invoke(@NotNull final Object... arguments)
	{
		try
		{
			return constructor.newInstance(arguments);
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new ShouldNeverHappenException(e);
		}
		catch (InvocationTargetException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
