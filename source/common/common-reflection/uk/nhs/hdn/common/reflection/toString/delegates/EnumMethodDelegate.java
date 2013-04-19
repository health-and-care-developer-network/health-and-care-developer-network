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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static uk.nhs.hdn.common.reflection.MethodModifiers.methodModifiers;

public final class EnumMethodDelegate<E extends Enum<E>, V> implements Delegate<V>
{
	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <E extends Enum<E>, V> Delegate<V> enumMethodDelegate(@NotNull final E enumValue, @NotNull final String methodName, @NotNull final Class<?>... parameterTypes)
	{
		final Method method;
		try
		{
			method = enumValue.getDeclaringClass().getDeclaredMethod(methodName, parameterTypes);
		}
		catch (NoSuchMethodException e)
		{
			throw new IllegalStateException(e);
		}
		return new EnumMethodDelegate<>(enumValue, method);
	}

	@NotNull
	private final Method constructor;

	public EnumMethodDelegate(@NotNull final E enumValue, @NotNull final Method constructor)
	{
		if (!methodModifiers(constructor).isInstance())
		{
			throw new IllegalArgumentException("constructor is not instance");
		}
		this.constructor = constructor;
	}

	@SuppressWarnings({"unchecked", "ThrowInsideCatchBlockWhichIgnoresCaughtException"})
	@Nullable
	@Override
	public V invoke(@NotNull final Object... arguments)
	{
		try
		{
			return (V) constructor.invoke(null, arguments);
		}
		catch (IllegalAccessException e)
		{
			throw new ShouldNeverHappenException(e);
		}
		catch (InvocationTargetException e)
		{
			throw new IllegalStateException(e.getCause());
		}
	}
}
