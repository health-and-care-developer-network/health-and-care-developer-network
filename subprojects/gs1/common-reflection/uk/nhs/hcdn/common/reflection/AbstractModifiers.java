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

package uk.nhs.hcdn.common.reflection;

import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;

public abstract class AbstractModifiers extends AbstractToString
{
	private static final int HashMagicScalar = 31;

	@ExcludeFromToString private final boolean isFinal;
	@ExcludeFromToString private final boolean isNative;
	@ExcludeFromToString private final boolean isPrivate;
	@ExcludeFromToString private final boolean isProtected;
	@ExcludeFromToString private final boolean isPublic;
	@ExcludeFromToString private final boolean isStatic;
	@ExcludeFromToString private final boolean isSynchronized;
	@SuppressWarnings("FieldCanBeLocal") // used by AbstractToString
	@NonNls @NotNull private final String stringRepresentation;

	protected AbstractModifiers(final int modifiers)
	{
		isFinal = Modifier.isFinal(modifiers);
		isNative = Modifier.isNative(modifiers);
		isPrivate = Modifier.isPrivate(modifiers);
		isProtected = Modifier.isProtected(modifiers);
		isPublic = Modifier.isPublic(modifiers);
		isStatic = Modifier.isStatic(modifiers);
		isSynchronized = Modifier.isSynchronized(modifiers);
		stringRepresentation = Modifier.toString(modifiers);
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

		final AbstractModifiers that = (AbstractModifiers) obj;

		if (isFinal != that.isFinal)
		{
			return false;
		}
		if (isNative != that.isNative)
		{
			return false;
		}
		if (isPrivate != that.isPrivate)
		{
			return false;
		}
		if (isProtected != that.isProtected)
		{
			return false;
		}
		if (isPublic != that.isPublic)
		{
			return false;
		}
		if (isStatic != that.isStatic)
		{
			return false;
		}
		if (isSynchronized != that.isSynchronized)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = isFinal ? 1 : 0;
		result = addBooleanToHash(result, isNative);
		result = addBooleanToHash(result, isPrivate);
		result = addBooleanToHash(result, isProtected);
		result = addBooleanToHash(result, isPublic);
		result = addBooleanToHash(result, isStatic);
		result = addBooleanToHash(result, isSynchronized);
		return result;
	}

	@SuppressWarnings("ConditionalExpression")
	protected static int addBooleanToHash(final int result, final boolean field)
	{
		return (HashMagicScalar * result) + (field ? 1 : 0);
	}

	public final boolean isFinal()
	{
		return isFinal;
	}

	public final boolean isNative()
	{
		return isNative;
	}

	public final boolean isPrivate()
	{
		return isPrivate;
	}

	public final boolean isProtected()
	{
		return isProtected;
	}

	public final boolean isPublic()
	{
		return isPublic;
	}

	public final boolean isStatic()
	{
		return isStatic;
	}

	public final boolean isSynchronized()
	{
		return isSynchronized;
	}

	public final boolean isInstance()
	{
		return !isStatic;
	}

	public final boolean isPublicInstanceNativeFinal()
	{
		return isPublic && !isStatic && isNative && isFinal;
	}

	public final boolean isPublicInstanceNative()
	{
		return isPublic && !isStatic && isNative;
	}

	public final boolean isPublicStatic()
	{
		return isPublic && isStatic;
	}

	public final boolean isPublicStaticNative()
	{
		return isPublic && isStatic && isNative;
	}

	public final boolean isPublicFinalStatic()
	{
		return isPublic && isStatic && isFinal;
	}

	public final boolean isPublicInstance()
	{
		return isPublic && !isStatic;
	}

	public final boolean isPrivateInstance()
	{
		return isPrivate && !isStatic;
	}

	public final boolean isPrivateFinalStatic()
	{
		return isPrivate && isStatic && isFinal;
	}

	public final boolean isPrivateFinalStaticNotNative()
	{
		return isPrivateFinalStatic() && !isNative;
	}
}
