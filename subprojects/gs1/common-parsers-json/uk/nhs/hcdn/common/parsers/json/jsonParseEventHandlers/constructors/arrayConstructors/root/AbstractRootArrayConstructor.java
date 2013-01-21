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

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.*;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.math.BigDecimal;

public abstract class AbstractRootArrayConstructor<X> extends AbstractToString implements ArrayConstructor<X[]>
{
	protected AbstractRootArrayConstructor()
	{
	}

	@Override
	public final void addLiteralBooleanValue(@NotNull final X[] arrayCollector, final int index, final boolean value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedLiteralBooleanValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addLiteralNullValue(@NotNull final X[] arrayCollector, final int index) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedLiteralNullValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addConstantStringValue(@NotNull final X[] arrayCollector, final int index, @NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedConstantStringValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addConstantNumberValue(@NotNull final X[] arrayCollector, final int index, final long value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedConstantNumberValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addConstantNumberValue(@NotNull final X[] arrayCollector, final int index, @NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedConstantNumberValueForSchemaViolationInvalidJsonException();
	}

	@NotNull
	@Override
	public final X[] newCollector()
	{
		return newArrayInstance(1);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public final X collect(@NotNull final X[] collector)
	{
		return collector[0];
	}

	@NotNull
	protected abstract X[] newArrayInstance(final int size);
}
