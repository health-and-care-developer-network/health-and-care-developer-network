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

import java.io.PrintStream;
import java.math.BigDecimal;

import static java.lang.System.err;
import static java.lang.System.out;

public final class ConsoleLoggingJsonParseEventHandler implements JsonParseEventHandler
{
	@NotNull
	public static final JsonParseEventHandler StandardOutConsoleDebuggingJsonParseEventHandler = new ConsoleLoggingJsonParseEventHandler(out);
	public static final JsonParseEventHandler StandardErrorConsoleDebuggingJsonParseEventHandler = new ConsoleLoggingJsonParseEventHandler(err);

	private final PrintStream stream;

	public ConsoleLoggingJsonParseEventHandler(@NotNull final PrintStream printStream)
	{
		stream = printStream;
	}

	@Override
	public void startRoot()
	{
		stream.println("startRoot");
		stream.flush();
	}

	@Override
	public void endRoot()
	{
		stream.println("endRoot");
		stream.flush();
	}

	@Override
	public void startObject()
	{
		stream.println("startObject");
		stream.flush();
	}

	@Override
	public void endObject()
	{
		stream.println("endObject");
		stream.flush();
	}

	@Override
	public void startArray()
	{
		stream.println("startArray");
		stream.flush();
	}

	@Override
	public void endArray()
	{
		stream.println("endArray");
		stream.flush();
	}

	@Override
	public void literalNullValue()
	{
		stream.println("nullValue()");
		stream.flush();
	}

	@Override
	public void literalBooleanValue(final boolean value)
	{
		stream.println("booleanValue(" + Boolean.toString(value) + ")");
		stream.flush();
	}

	@Override
	public void numberValue(final long integerComponent)
	{
		stream.println("numberValue(" + Long.toString(integerComponent) + ")");
		stream.flush();
	}

	@Override
	public void numberValue(@NotNull final BigDecimal fractionComponent)
	{
		stream.println("numberValue(" + fractionComponent.toPlainString() + ")");
		stream.flush();
	}

	@Override
	public void stringValue(@NotNull final String value)
	{
		stream.println("stringValue(" + value + ")");
		stream.flush();
	}

	@Override
	public void key(@NotNull final String key)
	{
		stream.println("key(" + key + ")");
		stream.flush();
	}
}
