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

package uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.nodeStates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.math.BigDecimal;

public final class ArrayNodeState<A> extends AbstractToString implements NodeState
{
	@NotNull
	private final ArrayConstructor<A> arrayConstructor;

	@NotNull
	private final A arrayCollector;

	private int nextIndex;

	public ArrayNodeState(@NotNull final ArrayConstructor<A> arrayConstructor)
	{
		this.arrayConstructor = arrayConstructor;
		arrayCollector = arrayConstructor.newCollector();
		nextIndex = 0;
	}

	@Override
	public void key(@NotNull final String key)
	{
		throw new UnsupportedOperationException("keys don't exist for arrays");
	}

	@NotNull
	@Override
	public ArrayConstructor<?> arrayValueStart() throws SchemaViolationInvalidJsonException
	{
		return arrayConstructor.arrayConstructor(nextIndex);
	}

	@Override
	public void arrayValue(@Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		arrayConstructor.addArrayValue(arrayCollector, nextIndex, value);
	}

	@NotNull
	@Override
	public ObjectConstructor<?> objectValueStart() throws SchemaViolationInvalidJsonException
	{
		return arrayConstructor.objectConstructor(nextIndex);
	}

	@Override
	public void objectValue(@Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		arrayConstructor.addObjectValue(arrayCollector, nextIndex, value);
	}

	@Override
	public void literalBooleanValue(final boolean value) throws SchemaViolationInvalidJsonException
	{
		arrayConstructor.addLiteralBooleanValue(arrayCollector, nextIndex, value);
		nextIndex++;
	}

	@Override
	public void literalNullValue() throws SchemaViolationInvalidJsonException
	{
		arrayConstructor.addLiteralNullValue(arrayCollector, nextIndex);
		nextIndex++;
	}

	@Override
	public void constantStringValue(@NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		arrayConstructor.addConstantStringValue(arrayCollector, nextIndex, value);
		nextIndex++;
	}

	@Override
	public void constantNumberValue(final long value) throws SchemaViolationInvalidJsonException
	{
		arrayConstructor.addConstantNumberValue(arrayCollector, nextIndex, value);
	}

	@Override
	public void constantNumberValue(@NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException
	{
		arrayConstructor.addConstantNumberValue(arrayCollector, nextIndex, value);
	}

	@Override
	@Nullable
	public Object collect() throws SchemaViolationInvalidJsonException
	{
		return arrayConstructor.collect(arrayCollector);
	}
}
