/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.reflection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class FieldModifiers extends AbstractModifiers
{
	private final boolean isStrict;
	private final boolean isTransient;
	private final boolean isVolatile;

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static FieldModifiers fieldModifiers(@NotNull final Field field)
	{
		return new FieldModifiers(field.getModifiers());
	}

	private FieldModifiers(final int modifiers)
	{
		super(modifiers);
		isStrict = Modifier.isStrict(modifiers);
		isTransient = Modifier.isTransient(modifiers);
		isVolatile = Modifier.isVolatile(modifiers);
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

		final FieldModifiers that = (FieldModifiers) obj;

		if (isStrict != that.isStrict)
		{
			return false;
		}
		if (isTransient != that.isTransient)
		{
			return false;
		}
		if (isVolatile != that.isVolatile)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = addBooleanToHash(result, isStrict);
		result = addBooleanToHash(result, isTransient);
		result = addBooleanToHash(result, isVolatile);
		return result;
	}

	public boolean isStrict()
	{
		return isStrict;
	}

	public boolean isTransient()
	{
		return isTransient;
	}

	public boolean isVolatile()
	{
		return isVolatile;
	}

	public boolean isPrivateFinalInstance()
	{
		return isPrivateInstance() && isFinal();
	}

	public boolean isPublicFinalInstance()
	{
		return isPublicInstance() && isFinal();
	}

	public boolean isProtectedFinalInstance()
	{
		return isProtected() && isFinal() && isInstance();
	}

	public boolean isPrivateFinalInstanceAndSerializable()
	{
		return isPrivateFinalInstance() && isSerializable();
	}

	public boolean isProtectedFinalInstanceAndSerializable()
	{
		return isProtectedFinalInstance() && isSerializable();
	}

	public boolean isPrivateOrProtectedFinalInstanceAndSerializable()
	{
		return isPrivateFinalInstanceAndSerializable() || isProtectedFinalInstanceAndSerializable();
	}

	public boolean isInstanceAndSerializable()
	{
		return isInstance() && isSerializable();
	}

	private boolean isSerializable()
	{
		return !isTransient;
	}
}
