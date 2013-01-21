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

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.parsers.json.InvalidJsonException;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ListArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.MapObjectConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.jsonParseResultUsers.JsonParseResultUser;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.jsonParseResultUsers.ValueReturningJsonParseResultUser;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.nodeStates.ArrayNodeState;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.nodeStates.NodeState;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.nodeStates.ObjectNodeState;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;
import uk.nhs.hcdn.common.parsers.json.jsonReaders.BufferedJsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Stack;

import static uk.nhs.hcdn.common.parsers.json.parseModes.RootParseMode.RootParseModeInstance;

public final class GenericJsonParseEventHandler<V> implements JsonParseEventHandler
{
	@NotNull
	public static Object genericParse(@NotNull final Reader reader) throws InvalidJsonException
	{
		final ListArrayConstructor listArrayConstructor = new ListArrayConstructor();
		final MapObjectConstructor mapObjectConstructor = new MapObjectConstructor();
		listArrayConstructor.configure(mapObjectConstructor);
		mapObjectConstructor.configure(listArrayConstructor);

		try
		{
			final ValueReturningJsonParseResultUser<List<Object>> objectValueReturningJsonParseResultUser = new ValueReturningJsonParseResultUser<>();
			RootParseModeInstance.parse(new GenericJsonParseEventHandler<>(listArrayConstructor, objectValueReturningJsonParseResultUser), new BufferedJsonReader(new BufferedReader(reader)));
			@Nullable final List<Object> value = objectValueReturningJsonParseResultUser.value();
			assert value != null;
			return value.get(0);
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (IOException ignored)
			{
			}
		}
	}

	@NotNull
	private final ArrayConstructor<V> rootArrayConstructor;
	@NotNull
	private final JsonParseResultUser<V> jsonParseResultUser;
	private final Stack<NodeState> depth;

	@Nullable
	private NodeState current;

	public GenericJsonParseEventHandler(@NotNull final ArrayConstructor<V> rootArrayConstructor, @NotNull final JsonParseResultUser<V> jsonParseResultUser)
	{
		this.rootArrayConstructor = rootArrayConstructor;
		this.jsonParseResultUser = jsonParseResultUser;
		depth = new Stack<>();
		current = null;
	}

	@Override
	public void startRoot()
	{
		current = new ArrayNodeState<>(rootArrayConstructor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void endRoot() throws SchemaViolationInvalidJsonException
	{
		@Nullable final V value = (V) current().collect();
		current = null;
		jsonParseResultUser.use(value);
	}

	@Override
	public void startArray() throws SchemaViolationInvalidJsonException
	{
		depth.push(current);
		current = new ArrayNodeState<>(current().arrayValueStart());
	}

	@Override
	public void endArray() throws SchemaViolationInvalidJsonException
	{
		@Nullable final Object value = current().collect();
		current = depth.pop();
		current.arrayValue(value);
	}

	@Override
	public void startObject() throws SchemaViolationInvalidJsonException
	{
		depth.push(current);
		current = new ObjectNodeState<>(current().objectValueStart());
	}

	@Override
	public void key(@NotNull final String key)
	{
		current().key(key);
	}

	@Override
	public void endObject() throws SchemaViolationInvalidJsonException
	{
		@Nullable final Object value = current().collect();
		current = depth.pop();
		current.objectValue(value);
	}

	@Override
	public void literalBooleanValue(final boolean value) throws SchemaViolationInvalidJsonException
	{
		current().literalBooleanValue(value);
	}

	@Override
	public void literalNullValue() throws SchemaViolationInvalidJsonException
	{
		current().literalNullValue();
	}

	@Override
	public void stringValue(@NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		current().constantStringValue(value);
	}

	@Override
	public void numberValue(final long integerComponent) throws SchemaViolationInvalidJsonException
	{
		current().constantNumberValue(integerComponent);
	}

	@Override
	public void numberValue(@NotNull final BigDecimal fractionComponent) throws SchemaViolationInvalidJsonException
	{
		current().constantNumberValue(fractionComponent);
	}

	@NotNull
	private NodeState current()
	{
		assert current != null;
		return current;
	}
}
