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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class FieldModifiers extends AbstractModifiers
{
	private final boolean isStrict;
	private final boolean isTransient;
	private final boolean isVolatile;

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static FieldModifiers fieldModifiers(@SuppressWarnings("TypeMayBeWeakened") @NotNull final Field field)
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

	public boolean isPublicFinalInstanceAndSerializable()
	{
		return isPublicFinalInstance() && isSerializable();
	}

	public boolean isPrivateOrProtectedOrPublicFinalInstanceAndSerializable()
	{
		return isPrivateFinalInstanceAndSerializable() || isProtectedFinalInstanceAndSerializable() || isPublicFinalInstanceAndSerializable();
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
