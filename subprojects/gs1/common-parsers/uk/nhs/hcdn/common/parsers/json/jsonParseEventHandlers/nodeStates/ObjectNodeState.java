/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
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
