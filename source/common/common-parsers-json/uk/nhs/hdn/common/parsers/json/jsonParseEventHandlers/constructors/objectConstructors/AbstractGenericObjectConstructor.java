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

package uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ListArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.math.BigDecimal;
import java.util.Map;

public abstract class AbstractGenericObjectConstructor<X> extends AbstractToString implements ObjectConstructor<Map<X, Object>>
{
	@NotNull
	private final ListArrayConstructor listArrayConstructor;
	@NotNull
	private final MapObjectConstructor mapObjectConstructor;

	protected AbstractGenericObjectConstructor()
	{
		listArrayConstructor = new ListArrayConstructor();
		mapObjectConstructor = new MapObjectConstructor();
		listArrayConstructor.configure(mapObjectConstructor);
		mapObjectConstructor.configure(listArrayConstructor);
	}

	@Override
	public final void putObjectValue(@NotNull final Map<X, Object> objectCollector, @NotNull final String key, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, value);
	}

	@Override
	public final void putArrayValue(@NotNull final Map<X, Object> objectCollector, @NotNull final String key, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, value);
	}

	@Override
	public final void putLiteralBooleanValue(@NotNull final Map<X, Object> objectCollector, @NotNull final String key, final boolean value) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, value);
	}

	@Override
	public final void putLiteralNullValue(@NotNull final Map<X, Object> objectCollector, @NotNull final String key) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, null);
	}

	@Override
	public final void putConstantStringValue(@NotNull final Map<X, Object> objectCollector, @NotNull final String key, @NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, value);
	}

	@Override
	public final void putConstantNumberValue(@NotNull final Map<X, Object> objectCollector, @NotNull final String key, final long value) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, value);
	}

	@Override
	public final void putConstantNumberValue(@NotNull final Map<X, Object> objectCollector, @NotNull final String key, @NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, value);
	}

	@NotNull
	@Override
	public final ArrayConstructor<?> arrayConstructor(@NotNull final String key)
	{
		return listArrayConstructor;
	}

	@NotNull
	@Override
	public final ObjectConstructor<?> objectConstructor(@NotNull final String key)
	{
		return mapObjectConstructor;
	}

	private void addValue(final Map<X, Object> objectCollector, final String key, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		objectCollector.put(convertKey(key), value);
	}

	@NotNull
	protected abstract X convertKey(@NotNull final String key) throws SchemaViolationInvalidJsonException;
}
