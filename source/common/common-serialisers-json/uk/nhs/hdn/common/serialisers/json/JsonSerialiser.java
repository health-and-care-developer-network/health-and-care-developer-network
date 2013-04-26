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

package uk.nhs.hdn.common.serialisers.json;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;
import uk.nhs.hdn.common.serialisers.*;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

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

	@NotNull
	private final Stack<JsonNodeState> depth;

	@NotNull
	private JsonNodeState current;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@NotNull
	@ExcludeFromToString
	private JsonStringWriter jsonStringWriter;

	public JsonSerialiser()
	{
		depth = new Stack<>();
		current = new JsonNodeState();
	}

	@Override
	public void start(@NotNull final OutputStream outputStream, @NotNull final Charset charset) throws CouldNotWriteDataException
	{
		super.start(outputStream, charset);
		jsonStringWriter = new JsonStringWriter(writer);
	}

	@Override
	public void writeProperty(@NotNull final String name, @NotNull final String value) throws CouldNotWritePropertyException
	{
		try
		{
			if (current.hasSubsequentProperty())
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.setHasSubsequentProperty();
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
			if (current.hasSubsequentProperty())
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.setHasSubsequentProperty();
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
			if (current.hasSubsequentProperty())
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.setHasSubsequentProperty();
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

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends MapSerialisable> void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final S[] values) throws CouldNotWritePropertyException
	{
		try
		{
			if (current.hasSubsequentProperty())
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.setHasSubsequentProperty();
			}
			jsonStringWriter.writeString(name);
			write(DoubleQuoteColon);
			writeValue(values);
		}
		catch (CouldNotWriteDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, values, e);
		}
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends ValueSerialisable> void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final S[] values) throws CouldNotWritePropertyException
	{
		try
		{
			if (current.hasSubsequentProperty())
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.setHasSubsequentProperty();
			}
			jsonStringWriter.writeString(name);
			write(DoubleQuoteColon);
			writeValue(values);
		}
		catch (CouldNotWriteDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, values, e);
		}
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final List<?> values) throws CouldNotWritePropertyException
	{
		try
		{
			if (current.hasSubsequentProperty())
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.setHasSubsequentProperty();
			}
			jsonStringWriter.writeString(name);
			write(DoubleQuoteColon);
			writeValue(values);
		}
		catch (CouldNotWriteDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, values, e);
		}
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final Set<?> values) throws CouldNotWritePropertyException
	{
		try
		{
			if (current.hasSubsequentProperty())
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.setHasSubsequentProperty();
			}
			jsonStringWriter.writeString(name);
			write(DoubleQuoteColon);
			writeValue(values);
		}
		catch (CouldNotWriteDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, values, e);
		}
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, final int value) throws CouldNotWritePropertyException
	{
		try
		{
			if (current.hasSubsequentProperty())
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.setHasSubsequentProperty();
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
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, final long value) throws CouldNotWritePropertyException
	{
		try
		{
			if (current.hasSubsequentProperty())
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.setHasSubsequentProperty();
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
			if (current.hasSubsequentProperty())
			{
				write(CommaDoubleQuote);
			}
			else
			{
				write(DoubleQuote);
				current.setHasSubsequentProperty();
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
			current = new JsonNodeState();
			write(OpenArray);
			final int length = values.length;
			if (length != 0)
			{
				writeValue(values[0]);
				for (int index = 1; index < length; index++)
				{
					write(Comma);
					writeValue(values[index]);
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
			current = new JsonNodeState();
			write(OpenArray);
			final int length = values.length;
			if (length != 0)
			{
				writeValue(values[0]);
				for (int index = 1; index < length; index++)
				{
					write(Comma);
					writeValue(values[index]);
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
	public void writeValue(@NotNull final List<?> values) throws CouldNotWriteValueException
	{
		depth.push(current);
		try
		{
			current = new JsonNodeState();
			write(OpenArray);
			final int length = values.size();
			if (length != 0)
			{
				writeValue(values.get(0));
				for (int index = 1; index < length; index++)
				{
					write(Comma);
					writeValue(values.get(index));
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
	public void writeValue(@NotNull final Set<?> values) throws CouldNotWriteValueException
	{
		depth.push(current);
		try
		{
			current = new JsonNodeState();
			write(OpenArray);
			boolean afterFirst = false;
			for (final Object value : values)
			{
				if (afterFirst)
				{
					write(Comma);
				}
				else
				{
					afterFirst = true;
				}
				writeValue(value);
			}
			write(CloseArray);
		}
		catch (CouldNotWriteDataException e)
		{
			throw new CouldNotWriteValueException(values, e);
		}
		current = depth.pop();
	}

	@SafeVarargs
	@Override
	public final <S extends Serialisable> void writeValue(@NotNull final S... values) throws CouldNotWriteValueException
	{
		depth.push(current);
		try
		{
			current = new JsonNodeState();
			write(OpenArray);
			boolean afterFirst = false;
			for (final Object value : values)
			{
				if (afterFirst)
				{
					write(Comma);
				}
				else
				{
					afterFirst = true;
				}
				writeValue(value);
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
	public void writeValue(final long value) throws CouldNotWriteValueException
	{
		try
		{
			write(Long.toString(value));
		}
		catch (CouldNotWriteDataException e)
		{
			throw new CouldNotWriteValueException(value, e);
		}
	}

	@Override
	public void writeValue(@NotNull final BigDecimal value) throws CouldNotWriteValueException
	{
		try
		{
			write(value.toString());
		}
		catch (CouldNotWriteDataException e)
		{
			throw new CouldNotWriteValueException(value, e);
		}
	}

	@Override
	public void writeValue(@NotNull final String value) throws CouldNotWriteValueException
	{
		try
		{
			write(DoubleQuote);
			jsonStringWriter.writeString(value);
			write(DoubleQuote);
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
			current = new JsonNodeState();
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
	public void writeValue(@NotNull final UUID value) throws CouldNotWriteValueException
	{
		writeValue(value.toString());
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
