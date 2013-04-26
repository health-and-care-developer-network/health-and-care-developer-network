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

package uk.nhs.hdn.common.serialisers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.util.Map;

public final class GenericMapSerialisable extends AbstractToString implements MapSerialisable
{
	@NotNull private final Map<?, ?> value;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public GenericMapSerialisable(@NotNull final Map<?, ?> value)
	{
		this.value = value;
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final GenericMapSerialisable that = (GenericMapSerialisable) obj;

		if (!value.equals(that.value))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		serialiseMapGenerically(mapSerialiser, value);
	}

	public static void serialiseMapGenerically(@NotNull final MapSerialiser mapSerialiser, @NotNull final Map<?, ?> value) throws CouldNotSerialiseMapException
	{
		for (final Map.Entry<?, ?> entry : value.entrySet())
		{
			try
			{
				final Object key = entry.getKey();
				mapSerialiser.writeProperty(key instanceof PropertyNameSerialisable ? ((PropertyNameSerialisable) key).serialiseToPropertyName() : key.toString(), entry.getValue());
			}
			catch (CouldNotWritePropertyException e)
			{
				throw new CouldNotSerialiseMapException(new GenericMapSerialisable(value), e);
			}
		}
	}
}
