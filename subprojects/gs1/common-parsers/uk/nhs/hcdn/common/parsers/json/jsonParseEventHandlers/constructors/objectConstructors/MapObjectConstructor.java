/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class MapObjectConstructor extends AbstractToString implements ObjectConstructor<Map<String, Object>>
{
	@Nullable
	private ArrayConstructor<?> arrayConstructor;

	public MapObjectConstructor()
	{
		arrayConstructor = null;
	}

	public void configure(@NotNull final ArrayConstructor<?> arrayConstructor)
	{
		this.arrayConstructor = arrayConstructor;
	}

	@NotNull
	@Override
	public Map<String, Object> newCollector()
	{
		return new HashMap<>(100);
	}

	@NotNull
	@Override
	public ArrayConstructor<?> arrayConstructor()
	{
		assert arrayConstructor != null;
		return arrayConstructor;
	}

	@NotNull
	@Override
	public ObjectConstructor<?> objectConstructor()
	{
		return this;
	}

	@Override
	public void putLiteralBooleanValue(@NotNull final Map<String, Object> objectCollector, @NotNull final String key, final boolean value)
	{
		objectCollector.put(key, value);
	}

	@Override
	public void putLiteralNullValue(@NotNull final Map<String, Object> objectCollector, @NotNull final String key)
	{
		objectCollector.put(key, null);
	}

	@Override
	public void putConstantStringValue(@NotNull final Map<String, Object> objectCollector, @NotNull final String key, @NotNull final String value)
	{
		objectCollector.put(key, value);
	}

	@Override
	public void putConstantNumberValue(@NotNull final Map<String, Object> objectCollector, @NotNull final String key, final long value)
	{
		objectCollector.put(key, value);
	}

	@Override
	public void putConstantNumberValue(@NotNull final Map<String, Object> objectCollector, @NotNull final String key, @NotNull final BigDecimal value)
	{
		objectCollector.put(key, value);
	}

	@Override
	public void putObjectValue(@NotNull final Map<String, Object> objectCollector, @NotNull final String key, @NotNull final Object value)
	{
		objectCollector.put(key, value);
	}

	@Override
	public void putArrayValue(@NotNull final Map<String, Object> objectCollector, @NotNull final String key, @NotNull final Object value)
	{
		objectCollector.put(key, value);
	}

	@NotNull
	@Override
	public Object collect(@NotNull final Map<String, Object> objectCollector)
	{
		return objectCollector;
	}
}
