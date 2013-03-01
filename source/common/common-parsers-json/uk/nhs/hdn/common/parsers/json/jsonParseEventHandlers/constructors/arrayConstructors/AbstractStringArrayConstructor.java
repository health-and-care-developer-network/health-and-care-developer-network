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

public abstract class AbstractStringArrayConstructor<X> implements ArrayConstructor<X>
{
	@Override
	public final void addLiteralBooleanValue(@NotNull final X arrayCollector, final int index, final boolean value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedLiteralBooleanValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addLiteralNullValue(@NotNull final X arrayCollector, final int index) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedLiteralNullValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addConstantNumberValue(@NotNull final X arrayCollector, final int index, final long value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedConstantNumberValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addConstantNumberValue(@NotNull final X arrayCollector, final int index, @NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedConstantNumberValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public final void addArrayValue(@NotNull final X arrayCollector, final int index, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedArrayValueForSchemaViolationInvalidJsonException();
	}

	@NotNull
	@Override
	public final ArrayConstructor<?> arrayConstructor(final int index) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedArrayValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public void addObjectValue(@NotNull final X arrayCollector, final int index, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedObjectValueForSchemaViolationInvalidJsonException();
	}

	@NotNull
	@Override
	public ObjectConstructor<?> objectConstructor(final int index) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedObjectValueForSchemaViolationInvalidJsonException();
	}
}
