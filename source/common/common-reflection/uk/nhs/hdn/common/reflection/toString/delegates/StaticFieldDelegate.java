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

import java.lang.reflect.Field;

public final class StaticFieldDelegate<V> implements Delegate<V>
{
	@NotNull
	private final Field constructor;

	public StaticFieldDelegate(@NotNull final Field constructor)
	{
		this.constructor = constructor;
		constructor.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public V invoke(@NotNull final Object... arguments)
	{
		if (arguments.length != 0)
		{
			throw new IllegalArgumentException("arguments should be empty");
		}
		try
		{
			return (V) constructor.get(null);
		}
		catch (IllegalAccessException e)
		{
			throw new ShouldNeverHappenException(e);
		}
	}
}
