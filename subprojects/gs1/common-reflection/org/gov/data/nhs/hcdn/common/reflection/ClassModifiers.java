package org.gov.data.nhs.hcdn.common.reflection;

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
