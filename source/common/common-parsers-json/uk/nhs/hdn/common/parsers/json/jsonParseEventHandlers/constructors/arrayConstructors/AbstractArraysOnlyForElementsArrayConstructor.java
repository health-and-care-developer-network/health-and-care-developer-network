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

package uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractArraysOnlyForElementsArrayConstructor<X> implements ArrayConstructor<List<X>>
{
	@NotNull
	private final Class<X> xClass;
	private final boolean nullDisallowed;

	protected AbstractArraysOnlyForElementsArrayConstructor(@NotNull final Class<X> xClass, final boolean nullPermitted)
	{
		this.xClass = xClass;
		nullDisallowed = !nullPermitted;
	}

	@Override
	public final void addLiteralBooleanValue(@NotNull final List<X> arrayCollector, final int index, final boolean value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedLiteralBooleanValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addLiteralNullValue(@NotNull final List<X> arrayCollector, final int index) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedLiteralNullValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addConstantStringValue(@NotNull final List<X> arrayCollector, final int index, @NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedConstantStringValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addConstantNumberValue(@NotNull final List<X> arrayCollector, final int index, final long value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedConstantNumberValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addConstantNumberValue(@NotNull final List<X> arrayCollector, final int index, @NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedConstantNumberValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addArrayValue(@NotNull final List<X> arrayCollector, final int index, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		if (value == null && nullDisallowed)
		{
			throw new NullObjectValueNotAllowedForSchemaViolationInvalidJsonException();
		}
		final X x;
		try
		{
			x = xClass.cast(value);
		}
		catch (ClassCastException e)
		{
			throw new ObjectValueTypeMismatchForSchemaViolationInvalidJsonException(e);
		}
		arrayCollector.add(x);
	}

	@NotNull
	@Override
	public final ObjectConstructor<?> objectConstructor(final int index) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedObjectValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addObjectValue(@NotNull final List<X> arrayCollector, final int index, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedObjectValueForSchemaViolationInvalidJsonException();
	}

	@NotNull
	@Override
	public final List<X> newCollector()
	{
		return new ArrayList<>(100);
	}
}
