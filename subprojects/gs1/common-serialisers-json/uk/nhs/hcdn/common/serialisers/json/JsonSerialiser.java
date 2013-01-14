/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers.json;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;
import uk.nhs.hcdn.common.serialisers.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Stack;

import static uk.nhs.hcdn.common.serialisers.json.NodeValue.List;
import static uk.nhs.hcdn.common.serialisers.json.NodeValue.Map;

public class JsonSerialiser extends AbstractSerialiser
{
	private static final int DoubleQuote = (int) '\"';
	private static final int Comma = (int) ',';
	private static final char[] DoubleQuoteColonDoubleQuote = "\":\"".toCharArray();
	private static final char[] CommaDoubleQuote = ",\"".toCharArray();
	private static final char[] DoubleQuoteColon = "\":".toCharArray();
	private static final int OpenObject = (int) '{';
	private static final int CloseObject = (int) '}';
	private static final int OpenArray = (int) '[';
	private static final int CloseArray = (int) ']';
	private static final char[] Null = "null".toCharArray();

	@NotNull @ExcludeFromToString
	private final JsonStringWriter jsonStringWriter;

	@NotNull
	private final Stack<NodeState> depth;

	@NotNull
	private NodeState current;

	public JsonSerialiser(@NotNull final OutputStream outputStream)
	{
		this(outputStream, Utf8);
	}

	public JsonSerialiser(@NotNull final OutputStream outputStream, @NotNull final Charset charset)
	{
		super(outputStream, charset);
		jsonStringWriter = new JsonStringWriter(writer);
		depth = new Stack<>();
		current = new NodeState(Map);
	}

	@Override
	public void start() throws CouldNotWriteDataException
	{
	}

	public static final class NodeState
	{
		@NotNull
		private final NodeValue nodeValue;
		public boolean subsequentProperty;

		public NodeState(@NotNull final NodeValue nodeValue)
		{
			this.nodeValue = nodeValue;
			subsequentProperty = false;
		}
	}

	@Override
	public void writeProperty(@NotNull final String name, @NotNull final String value) throws CouldNotWritePropertyException
	{
		try
		{
			if (current.subsequentProperty)
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.subsequentProperty = true;
			}
			jsonStringWriter.writeString(name);
			write(DoubleQuoteColonDoubleQuote);
			jsonStringWriter.writeString(value);
			write(DoubleQuote);
		}
		catch (CouldNotWriteDataException e)
		{
			throw new CouldNotWritePropertyException(name, value, e);
		}
	}

	@Override
	public void writeProperty(@NotNull final String name, @NotNull final MapSerialisable value) throws CouldNotWritePropertyException
	{
		try
		{
			if (current.subsequentProperty)
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.subsequentProperty = true;
			}
			jsonStringWriter.writeString(name);
			write(DoubleQuoteColon);
			writeValue(value);
		}
		catch (CouldNotWriteDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, value, e);
		}
	}

	@Override
	public void writeProperty(@NotNull final String name, @NotNull final ValueSerialisable value) throws CouldNotWritePropertyException
	{
		try
		{
			if (current.subsequentProperty)
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.subsequentProperty = true;
			}
			jsonStringWriter.writeString(name);
			write(DoubleQuoteColon);
			writeValue(value);
		}
		catch (CouldNotWriteDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, value, e);
		}
	}

	@Override
	public void writePropertyNull(@NonNls @NotNull final String name) throws CouldNotWritePropertyException
	{
		try
		{
			if (current.subsequentProperty)
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.subsequentProperty = true;
			}
			jsonStringWriter.writeString(name);
			write(DoubleQuoteColon);
			writeValueNull();
		}
		catch (CouldNotWriteDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, e);
		}
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends MapSerialisable> void writeValue(@NotNull final S[] values) throws CouldNotWriteValueException
	{
		depth.push(current);
		try
		{
			current = new NodeState(List);
			write(OpenArray);
			final int length = values.length;
			if (length != 0)
			{
				writeValue(values[0]);
				for (int index = 1; index < length; index++)
				{
					write(Comma);
					writeValue(values[0]);
				}
			}
			write(CloseArray);
		}
		catch (CouldNotWriteDataException e)
		{
			throw new CouldNotWriteValueException(values, e);
		}
		current = depth.pop();
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends ValueSerialisable> void writeValue(@NotNull final S[] values) throws CouldNotWriteValueException
	{
		depth.push(current);
		try
		{
			current = new NodeState(List);
			write(OpenArray);
			final int length = values.length;
			if (length != 0)
			{
				writeValue(values[0]);
				for (int index = 1; index < length; index++)
				{
					write(Comma);
					writeValue(values[0]);
				}
			}
			write(CloseArray);
		}
		catch (CouldNotWriteDataException e)
		{
			throw new CouldNotWriteValueException(values, e);
		}
		current = depth.pop();
	}

	@Override
	public void writeValue(final int value) throws CouldNotWriteValueException
	{
		try
		{
			write(Integer.toString(value));
		}
		catch (CouldNotWriteDataException e)
		{
			throw new CouldNotWriteValueException(value, e);
		}
	}

	@Override
	public void writeValue(@NotNull final MapSerialisable value) throws CouldNotWriteValueException
	{
		try
		{
			depth.push(current);
			current = new NodeState(Map);
			write(OpenObject);
			value.serialiseMap(this);
			write(CloseObject);
			depth.pop();
		}
		catch (CouldNotWriteDataException | CouldNotSerialiseMapException e)
		{
			throw new CouldNotWriteValueException(value, e);
		}
	}

	@Override
	public void writeValue(@NotNull final ValueSerialisable value) throws CouldNotWriteValueException
	{
		try
		{
			value.serialiseValue(this);
		}
		catch (CouldNotSerialiseValueException e)
		{
			throw new CouldNotWriteValueException(value, e);
		}
	}

	@Override
	public void writeValueNull() throws CouldNotWriteValueException
	{
		try
		{
			write(Null);
		}
		catch (CouldNotWriteDataException e)
		{
			throw new CouldNotWriteValueException("null", e);
		}
	}

	private void write(final int character) throws CouldNotWriteDataException
	{
		try
		{
			writer.write(character);
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	private void write(final String value) throws CouldNotWriteDataException
	{
		try
		{
			writer.write(value);
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	private void write(final char[] character) throws CouldNotWriteDataException
	{
		try
		{
			writer.write(character);
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}
}
