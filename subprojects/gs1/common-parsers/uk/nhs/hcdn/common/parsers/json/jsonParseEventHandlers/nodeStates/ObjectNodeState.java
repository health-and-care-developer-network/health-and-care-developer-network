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
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.math.BigDecimal;

public final class ObjectNodeState<A> extends AbstractToString implements NodeState<A>
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
	public ArrayConstructor<?> arrayValueStart()
	{
		return objectConstructor.arrayConstructor();
	}

	@NotNull
	@Override
	public ObjectConstructor<?> objectValueStart()
	{
		return objectConstructor.objectConstructor();
	}

	@Override
	public void objectValue(@NotNull final Object value)
	{
		objectConstructor.putObjectValue(objectCollector, currentKey(), value);
	}

	@Override
	public void arrayValue(@NotNull final Object value)
	{
		objectConstructor.putArrayValue(objectCollector, currentKey(), value);
	}

	@Override
	public void literalBooleanValue(final boolean value)
	{
		objectConstructor.putLiteralBooleanValue(objectCollector, currentKey(), value);
	}

	@Override
	public void literalNullValue()
	{
		objectConstructor.putLiteralNullValue(objectCollector, currentKey());
	}

	@Override
	public void constantStringValue(@NotNull final String value)
	{
		objectConstructor.putConstantStringValue(objectCollector, currentKey(), value);
	}

	@Override
	public void constantNumberValue(final long value)
	{
		objectConstructor.putConstantNumberValue(objectCollector, currentKey(), value);
	}

	@Override
	public void constantNumberValue(@NotNull final BigDecimal value)
	{
		objectConstructor.putConstantNumberValue(objectCollector, currentKey(), value);
	}

	@Override
	@NotNull
	public Object collect()
	{
		return objectConstructor.collect(objectCollector);
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
