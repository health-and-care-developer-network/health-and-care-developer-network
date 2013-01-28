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
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static uk.nhs.hdn.common.reflection.FieldModifiers.fieldModifiers;

public final class ClassInformation extends AbstractToString
{
	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static ClassInformation classInformation(@NotNull final Class<?> clazz)
	{
		return new ClassInformation(clazz);
	}

	@NotNull
	private final Class<?> clazz;

	@ExcludeFromToString
	private final ClassModifiers classModifiers;

	public ClassInformation(@NotNull final Class<?> clazz)
	{
		this.clazz = clazz;
		classModifiers = ClassModifiers.classModifiers(clazz);
	}

	@SuppressWarnings("rawtypes")
	@NotNull
	public String simpleName()
	{
		ClassInformation classInformation = this;
		while (classInformation.isLocalOrAnonymousClass())
		{
			classInformation = new ClassInformation(clazz.getSuperclass());
		}
		return classInformation.clazz.getSimpleName();
	}

	public boolean isLocalOrAnonymousClass()
	{
		return clazz.isLocalClass() || clazz.isAnonymousClass();
	}

	public boolean isRegularClassAndIsNotEnum()
	{
		return isRegularClass() && !clazz.isEnum();
	}

	public boolean isConcrete()
	{
		return isRegularClassAndIsNotEnum() && classModifiers.isConcrete();
	}

	public boolean isRegularClass()
	{
		return !clazz.isArray() && !clazz.isPrimitive() && !clazz.isInterface();
	}

	@NotNull
	public List<Field> allPrivateOrProtectedOrPublicFinalFields()
	{
		if (!isRegularClass())
		{
			throw new IllegalStateException("mostDerivedClass must be a regular class");
		}

		final Stack<Class<?>> leastDerivedOrder = new Stack<>();
		Class<?> currentClass = clazz;
		while (!currentClass.equals(Object.class))
		{
			leastDerivedOrder.push(currentClass);
			currentClass = currentClass.getSuperclass();
		}

		//noinspection ClassExtendsConcreteCollection
		return new ArrayList<Field>()
		{
			{
				while (!leastDerivedOrder.empty())
				{
					getFields(leastDerivedOrder.pop());
				}
			}

			private void getFields(@NotNull final Class<?> aClass)
			{
				for (final Field declaredField : aClass.getDeclaredFields())
				{
					if (fieldModifiers(declaredField).isPrivateOrProtectedOrPublicFinalInstanceAndSerializable())
					{
						declaredField.setAccessible(true);
						add(declaredField);
					}
				}
			}
		};
	}
}
