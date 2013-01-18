/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.nodeStates;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.math.BigDecimal;

public final class ArrayNodeState<A> extends AbstractToString implements NodeState<A>
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
	public ArrayConstructor<?> arrayValueStart()
	{
		return arrayConstructor.arrayConstructor();
	}

	@Override
	public void arrayValue(@NotNull final Object value)
	{
		arrayConstructor.addArrayValue(arrayCollector, nextIndex, value);
	}

	@NotNull
	@Override
	public ObjectConstructor<?> objectValueStart()
	{
		return arrayConstructor.objectConstructor();
	}

	@Override
	public void objectValue(@NotNull final Object value)
	{
		arrayConstructor.addObjectValue(arrayCollector, nextIndex, value);
	}

	@Override
	public void literalBooleanValue(final boolean value)
	{
		arrayConstructor.addLiteralBooleanValue(arrayCollector, nextIndex, value);
		nextIndex++;
	}

	@Override
	public void literalNullValue()
	{
		arrayConstructor.addLiteralNullValue(arrayCollector, nextIndex);
		nextIndex++;
	}

	@Override
	public void constantStringValue(@NotNull final String value)
	{
		arrayConstructor.addConstantStringValue(arrayCollector, nextIndex, value);
		nextIndex++;
	}

	@Override
	public void constantNumberValue(final long value)
	{
		arrayConstructor.addConstantNumberValue(arrayCollector, nextIndex, value);
	}

	@Override
	public void constantNumberValue(@NotNull final BigDecimal value)
	{
		arrayConstructor.addConstantNumberValue(arrayCollector, nextIndex, value);
	}

	@Override
	@NotNull
	public Object collect()
	{
		return arrayConstructor.collect(arrayCollector);
	}
}
