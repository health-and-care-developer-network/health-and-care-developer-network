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

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.nodeStates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.math.BigDecimal;

public final class ObjectNodeState<A> extends AbstractToString implements NodeState
{
	@NotNull
	private final ObjectConstructor<A> objectConstructor;

	@NotNull
	private final A objectCollector;

	@Nullable
	private String currentKey;

	public ObjectNodeState(@NotNull final ObjectConstructor<A> objectConstructor)
	{
		this.objectConstructor = objectConstructor;
		objectCollector = objectConstructor.newCollector();
		currentKey = null;
	}

	@Override
	public void key(@NotNull final String key)
	{
		if (currentKey != null)
		{
			throw new IllegalStateException("Already has key");
		}
		currentKey = key;
	}

	@NotNull
	@Override
	public ArrayConstructor<?> arrayValueStart() throws SchemaViolationInvalidJsonException
	{
		return objectConstructor.arrayConstructor(currentKeyPreserve());
	}

	@NotNull
	@Override
	public ObjectConstructor<?> objectValueStart() throws SchemaViolationInvalidJsonException
	{
		return objectConstructor.objectConstructor(currentKeyPreserve());
	}

	@Override
	public void objectValue(@Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		objectConstructor.putObjectValue(objectCollector, currentKey(), value);
	}

	@Override
	public void arrayValue(@Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		objectConstructor.putArrayValue(objectCollector, currentKey(), value);
	}

	@Override
	public void literalBooleanValue(final boolean value) throws SchemaViolationInvalidJsonException
	{
		objectConstructor.putLiteralBooleanValue(objectCollector, currentKey(), value);
	}

	@Override
	public void literalNullValue() throws SchemaViolationInvalidJsonException
	{
		objectConstructor.putLiteralNullValue(objectCollector, currentKey());
	}

	@Override
	public void constantStringValue(@NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		objectConstructor.putConstantStringValue(objectCollector, currentKey(), value);
	}

	@Override
	public void constantNumberValue(final long value) throws SchemaViolationInvalidJsonException
	{
		objectConstructor.putConstantNumberValue(objectCollector, currentKey(), value);
	}

	@Override
	public void constantNumberValue(@NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException
	{
		objectConstructor.putConstantNumberValue(objectCollector, currentKey(), value);
	}

	@Override
	@Nullable
	public Object collect() throws SchemaViolationInvalidJsonException
	{
		return objectConstructor.collect(objectCollector);
	}

	@NotNull
	private String currentKeyPreserve()
	{
		assert currentKey != null;
		return currentKey;
	}

	@NotNull
	private String currentKey()
	{
		assert currentKey != null;
		final String key = currentKey;
		currentKey = null;
		return key;
	}
}
