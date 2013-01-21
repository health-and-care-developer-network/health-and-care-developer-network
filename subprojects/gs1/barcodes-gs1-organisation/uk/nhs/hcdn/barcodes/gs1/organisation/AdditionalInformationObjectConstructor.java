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

package uk.nhs.hcdn.barcodes.gs1.organisation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ListArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.MapObjectConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.barcodes.gs1.organisation.AdditionalInformationKey.valueOf;

public final class AdditionalInformationObjectConstructor extends AbstractToString implements ObjectConstructor<Map<AdditionalInformationKey, Object>>
{
	@NotNull
	public static final ObjectConstructor<?> AdditionalInformationObjectConstructorInstance = new AdditionalInformationObjectConstructor();
	private final ListArrayConstructor listArrayConstructor;
	private final MapObjectConstructor mapObjectConstructor;

	private AdditionalInformationObjectConstructor()
	{
		listArrayConstructor = new ListArrayConstructor();
		mapObjectConstructor = new MapObjectConstructor();
		listArrayConstructor.configure(mapObjectConstructor);
		mapObjectConstructor.configure(listArrayConstructor);
	}

	@Override
	public void putObjectValue(@NotNull final Map<AdditionalInformationKey, Object> objectCollector, @NotNull final String key, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, value);
	}

	@Override
	public void putArrayValue(@NotNull final Map<AdditionalInformationKey, Object> objectCollector, @NotNull final String key, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, value);
	}

	@Override
	public void putLiteralBooleanValue(@NotNull final Map<AdditionalInformationKey, Object> objectCollector, @NotNull final String key, final boolean value) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, value);
	}

	@Override
	public void putLiteralNullValue(@NotNull final Map<AdditionalInformationKey, Object> objectCollector, @NotNull final String key) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, null);
	}

	@Override
	public void putConstantStringValue(@NotNull final Map<AdditionalInformationKey, Object> objectCollector, @NotNull final String key, @NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, value);
	}

	@Override
	public void putConstantNumberValue(@NotNull final Map<AdditionalInformationKey, Object> objectCollector, @NotNull final String key, final long value) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, value);
	}

	@Override
	public void putConstantNumberValue(@NotNull final Map<AdditionalInformationKey, Object> objectCollector, @NotNull final String key, @NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException
	{
		addValue(objectCollector, key, value);
	}

	@NotNull
	@Override
	public ArrayConstructor<?> arrayConstructor(@NotNull final String key)
	{
		return listArrayConstructor;
	}

	@NotNull
	@Override
	public ObjectConstructor<?> objectConstructor(@NotNull final String key)
	{
		return mapObjectConstructor;
	}

	@NotNull
	@Override
	public Map<AdditionalInformationKey, Object> newCollector()
	{
		return new EnumMap<>(AdditionalInformationKey.class);
	}

	@Override
	public Object collect(@NotNull final Map<AdditionalInformationKey, Object> collector)
	{
		return new AdditionalInformation(false, collector);
	}

	private static void addValue(final Map<AdditionalInformationKey, Object> objectCollector, final String key, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		objectCollector.put(additionalInformationKey(key), value);
	}

	private static AdditionalInformationKey additionalInformationKey(@NotNull final String key) throws SchemaViolationInvalidJsonException
	{
		try
		{
			return valueOf(key);
		}
		catch (IllegalArgumentException e)
		{
			throw new SchemaViolationInvalidJsonException(format(ENGLISH, "Additional information not recognised for key %1$s", key), e);
		}
	}
}
