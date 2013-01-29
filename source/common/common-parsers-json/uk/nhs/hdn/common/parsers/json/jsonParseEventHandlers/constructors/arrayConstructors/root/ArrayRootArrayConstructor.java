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

package uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.arrayCreators.ArrayCreator;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.ArrayValueTypeMismatchForSchemaViolationInvalidJsonException;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.NullArrayValueNotAllowedForSchemaViolationInvalidJsonException;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.UnexpectedObjectValueForSchemaViolationInvalidJsonException;

public class ArrayRootArrayConstructor<V> extends AbstractRootArrayConstructor<V[]>
{
	@NotNull
	private final Class<V[]> vClass;
	private final boolean nullDisallowed;
	@NotNull
	private final ArrayCreator<V> arrayCreator;
	@NotNull
	private final ArrayConstructor<?> arrayConstructor;

	public ArrayRootArrayConstructor(final boolean nullPermitted, @NotNull final ArrayCreator<V> arrayCreator, @NotNull final ArrayConstructor<?> arrayConstructor)
	{
		vClass = arrayCreator.arrayType();
		this.arrayCreator = arrayCreator;
		nullDisallowed = !nullPermitted;
		this.arrayConstructor = arrayConstructor;
	}

	@Override
	public final void addObjectValue(@NotNull final V[][] arrayCollector, final int index, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedObjectValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addArrayValue(@NotNull final V[][] arrayCollector, final int index, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		if (value == null && nullDisallowed)
		{
			throw new NullArrayValueNotAllowedForSchemaViolationInvalidJsonException();
		}
		try
		{
			arrayCollector[0] = vClass.cast(value);
		}
		catch (ClassCastException e)
		{
			throw new ArrayValueTypeMismatchForSchemaViolationInvalidJsonException(e);
		}
	}

	@NotNull
	@Override
	public final ArrayConstructor<?> arrayConstructor(final int index)
	{
		return arrayConstructor;
	}

	@NotNull
	@Override
	public final ObjectConstructor<?> objectConstructor(final int index) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedObjectValueForSchemaViolationInvalidJsonException();
	}

	@NotNull
	@Override
	protected final V[][] newArrayInstance(final int size)
	{
		return arrayCreator.newInstance2(size);
	}
}
