/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
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
