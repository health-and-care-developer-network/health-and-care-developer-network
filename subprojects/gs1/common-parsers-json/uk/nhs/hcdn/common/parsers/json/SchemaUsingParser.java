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

package uk.nhs.hcdn.common.parsers.json;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.GenericJsonParseEventHandler;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.jsonParseResultUsers.NonNullValueReturningJsonParseResultUser;
import uk.nhs.hcdn.common.parsers.json.jsonReaders.BufferedJsonReader;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import static uk.nhs.hcdn.common.parsers.json.parseModes.RootParseMode.RootParseModeInstance;

public class SchemaUsingParser<V> extends AbstractToString
{
	private final ArrayConstructor<V[]> schema;

	@SuppressWarnings({"unchecked", "RedundantCast"})
	public SchemaUsingParser(@NotNull final ArrayRootArrayConstructor<V> schema)
	{
		this.schema = (ArrayConstructor<V[]>) (Object) schema;
	}

	@NotNull
	public final V[] parse(@NotNull final Reader reader) throws InvalidJsonException
	{
		final NonNullValueReturningJsonParseResultUser<V[]> resultUser = new NonNullValueReturningJsonParseResultUser<>();
		final JsonParseEventHandler jsonParseEventHandler = new GenericJsonParseEventHandler<>(schema, resultUser);
		try
		{
			RootParseModeInstance.parse(jsonParseEventHandler, new BufferedJsonReader(new BufferedReader(reader)));
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
		return resultUser.value();
	}
}
