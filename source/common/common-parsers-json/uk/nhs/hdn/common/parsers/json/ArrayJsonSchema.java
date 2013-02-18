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

package uk.nhs.hdn.common.parsers.json;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.GenericJsonParseEventHandler;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.JsonParseEventHandler;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.root.ArrayRootArrayConstructor;
import uk.nhs.hdn.common.parsers.convenientReaders.BufferedPeekingConvenientReader;
import uk.nhs.hdn.common.parsers.parseResultUsers.NonNullValueReturningParseResultUser;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import static uk.nhs.hdn.common.parsers.json.parseModes.RootParseMode.RootParseModeInstance;

public class ArrayJsonSchema<V> extends AbstractToString implements JsonSchema<V[]>
{
	private final ArrayConstructor<V[]> schema;

	@SuppressWarnings({"unchecked", "RedundantCast"})
	public ArrayJsonSchema(@NotNull final ArrayRootArrayConstructor<V> schema)
	{
		this.schema = (ArrayConstructor<V[]>) (Object) schema;
	}

	@Override
	@NotNull
	public final V[] parse(@NotNull final Reader reader) throws InvalidJsonException
	{
		final NonNullValueReturningParseResultUser<V[]> resultUser = new NonNullValueReturningParseResultUser<>();
		final JsonParseEventHandler jsonParseEventHandler = new GenericJsonParseEventHandler<>(schema, resultUser);
		try
		{
			RootParseModeInstance.parse(jsonParseEventHandler, new BufferedPeekingConvenientReader(new BufferedReader(reader)));
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
