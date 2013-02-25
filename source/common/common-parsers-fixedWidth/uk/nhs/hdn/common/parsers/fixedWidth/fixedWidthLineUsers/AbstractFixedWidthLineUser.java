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

package uk.nhs.hdn.common.parsers.fixedWidth.fixedWidthLineUsers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.parsers.fixedWidth.CouldNotConvertFieldsException;

public abstract class AbstractFixedWidthLineUser<V> implements FixedWidthLineUser<V>
{
	protected AbstractFixedWidthLineUser()
	{
	}

	@Nullable
	protected static <V> V nullableField(@NotNull final Object[] collectedFields, @NotNull final Class<V> clazz, final int fieldIndex) throws CouldNotConvertFieldsException
	{
		try
		{
			return clazz.cast(collectedFields[fieldIndex]);
		}
		catch (ClassCastException e)
		{
			throw new CouldNotConvertFieldsException(e);
		}
	}

	@NotNull
	protected static <V> V nonNullField(@NotNull final Object[] collectedFields, @NotNull final Class<V> clazz, final int fieldIndex) throws CouldNotConvertFieldsException
	{
		@Nullable final Object value = collectedFields[fieldIndex];
		if (value == null)
		{
			throw new CouldNotConvertFieldsException("null is not permitted for a constituent field");
		}
		try
		{
			return clazz.cast(value);
		}
		catch (ClassCastException e)
		{
			throw new CouldNotConvertFieldsException(e);
		}
	}
}
