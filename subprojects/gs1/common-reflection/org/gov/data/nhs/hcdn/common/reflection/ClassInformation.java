package org.gov.data.nhs.hcdn.common.reflection;

import org.gov.data.nhs.hcdn.common.reflection.toString.AbstractToString;
import org.gov.data.nhs.hcdn.common.reflection.toString.ExcludeFromToString;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.gov.data.nhs.hcdn.common.reflection.ClassModifiers.classModifiers;
import static org.gov.data.nhs.hcdn.common.reflection.FieldModifiers.fieldModifiers;

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
		classModifiers = classModifiers(clazz);
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
	public List<Field> allPrivateFinalFields()
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
					if (fieldModifiers(declaredField).isPrivateOrProtectedFinalInstanceAndSerializable())
					{
						declaredField.setAccessible(true);
						add(declaredField);
					}
				}
			}
		};
	}
}
