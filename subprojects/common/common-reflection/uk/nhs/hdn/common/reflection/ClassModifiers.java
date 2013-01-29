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

package uk.nhs.hdn.common.reflection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;

public final class ClassModifiers extends AbstractModifiers
{
	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <T> ClassModifiers classModifiers(@NotNull final Class<T> clazz)
	{
		return new ClassModifiers(clazz.getModifiers());
	}

	private final boolean isAbstract;
	private final boolean isInterface;

	public ClassModifiers(final int modifiers)
	{
		super(modifiers);
		isAbstract = Modifier.isAbstract(modifiers);
		isInterface = Modifier.isInterface(modifiers);
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass()))
		{
			return false;
		}
		if (!super.equals(obj))
		{
			return false;
		}

		final ClassModifiers that = (ClassModifiers) obj;

		if (isAbstract != that.isAbstract)
		{
			return false;
		}
		if (isInterface != that.isInterface)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = addBooleanToHash(result, isAbstract);
		result = addBooleanToHash(result, isInterface);
		return result;
	}

	public boolean isAbstract()
	{
		return isAbstract;
	}

	public boolean isInterface()
	{
		return isInterface;
	}

	public boolean isConcrete()
	{
		return !isAbstract && !isInterface;
	}

	public boolean isConcreteAndFinal()
	{
		return isConcrete() && isFinal();
	}
}
