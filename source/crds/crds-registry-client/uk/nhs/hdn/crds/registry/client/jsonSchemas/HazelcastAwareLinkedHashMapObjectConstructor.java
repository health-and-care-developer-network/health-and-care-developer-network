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

package uk.nhs.hdn.crds.registry.client.jsonSchemas;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashMap;
import uk.nhs.hdn.common.hazelcast.hazelcastDataWriters.HazelcastDataWriter;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.*;
import uk.nhs.hdn.crds.registry.domain.identifiers.Identifier;
import uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor;

import java.math.BigDecimal;

public final class HazelcastAwareLinkedHashMapObjectConstructor<K extends Identifier, V extends HazelcastDataWriter> implements ObjectConstructor<HazelcastAwareLinkedHashMap<K, V>>
{
	@NotNull private final IdentifierConstructor identifierConstructor;
	@NotNull private final ObjectConstructor<?> objectConstructor;

	public HazelcastAwareLinkedHashMapObjectConstructor(@NotNull final IdentifierConstructor identifierConstructor, @NotNull final ObjectConstructor<?> objectConstructor)
	{
		this.identifierConstructor = identifierConstructor;
		this.objectConstructor = objectConstructor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void putObjectValue(@NotNull final HazelcastAwareLinkedHashMap<K, V> objectCollector, @NotNull final String key, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		final K identifier;
		try
		{
			identifier = (K) identifierConstructor.construct(key);
		}
		catch (RuntimeException e)
		{
			throw new SchemaViolationInvalidJsonException("could not convert identifier", e);
		}
		objectCollector.put(identifier, (V) value);
	}

	@NotNull
	@Override
	public ObjectConstructor<?> objectConstructor(@NotNull final String key) throws SchemaViolationInvalidJsonException
	{
		return objectConstructor;
	}

	@NotNull
	@Override
	public ArrayConstructor<?> arrayConstructor(@NotNull final String key) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedArrayValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public void putArrayValue(@NotNull final HazelcastAwareLinkedHashMap<K, V> objectCollector, @NotNull final String key, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedArrayValueForSchemaViolationInvalidJsonException();
	}

	@NotNull
	@Override
	public HazelcastAwareLinkedHashMap<K, V> newCollector()
	{
		return new HazelcastAwareLinkedHashMap<>(1000);
	}

	@Nullable
	@Override
	public Object collect(@NotNull final HazelcastAwareLinkedHashMap<K, V> collector) throws SchemaViolationInvalidJsonException
	{
		return collector;
	}

	@Override
	public void putLiteralBooleanValue(@NotNull final HazelcastAwareLinkedHashMap<K, V> objectCollector, @NotNull final String key, final boolean value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedLiteralBooleanValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public void putLiteralNullValue(@NotNull final HazelcastAwareLinkedHashMap<K, V> objectCollector, @NotNull final String key) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedLiteralNullValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public void putConstantNumberValue(@NotNull final HazelcastAwareLinkedHashMap<K, V> objectCollector, @NotNull final String key, final long value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedConstantNumberValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public void putConstantNumberValue(@NotNull final HazelcastAwareLinkedHashMap<K, V> objectCollector, @NotNull final String key, @NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedConstantNumberValueForSchemaViolationInvalidJsonException();
	}

	@Override
	public void putConstantStringValue(@NotNull final HazelcastAwareLinkedHashMap<K, V> objectCollector, @NotNull final String key, @NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		throw new UnexpectedConstantStringValueForSchemaViolationInvalidJsonException();
	}
}
