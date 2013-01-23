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

package uk.nhs.hcdn.common.serialisers.xml;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;
import uk.nhs.hcdn.common.serialisers.*;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.common.CharsetHelper.Utf8;

public final class XmlSerialiser extends AbstractSerialiser
{
	public static void serialise(@NotNull final String rootNodeName, @NotNull final MapSerialisable graph, @NotNull final OutputStream outputStream) throws CouldNotSerialiseException
	{
		serialise(rootNodeName, graph, outputStream, true, Utf8);
	}

	public static void serialise(@NotNull final String rootNodeName, @NotNull final MapSerialisable graph, @NotNull final OutputStream outputStream, final boolean xmlDeclaration) throws CouldNotSerialiseException
	{
		serialise(rootNodeName, graph, outputStream, xmlDeclaration, Utf8);
	}

	public static void serialise(@NotNull final String rootNodeName, @NotNull final MapSerialisable graph, @NotNull final OutputStream outputStream, final boolean xmlDeclaration, @NotNull final Charset charset) throws CouldNotSerialiseException
	{
		final XmlSerialiser xmlSerialiser = new XmlSerialiser(rootNodeName, xmlDeclaration);
		try
		{
			xmlSerialiser.start(outputStream, charset);
			xmlSerialiser.writeValue(graph);
			xmlSerialiser.finish();
		}
		catch (CouldNotWriteDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseException(graph, e);
		}
	}

	private static final int LessThan = (int) '<';
	private static final int GreaterThan = (int) '>';
	private static final char[] LessThanSlash = characters("</");
	private static final char[] SlashGreaterThan = characters("/>");
	private static final String ListElementNodeName = "element";

	private static char[] characters(final String value)
	{
		return value.toCharArray();
	}

	@NotNull
	private final String rootNodeName;
	private final boolean xmlDeclaration;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@NotNull @ExcludeFromToString
	private XmlStringWriter xmlStringWriter;

	public XmlSerialiser(@NonNls @NotNull final String rootNodeName, final boolean xmlDeclaration)
	{
		this.rootNodeName = rootNodeName;
		this.xmlDeclaration = xmlDeclaration;
	}

	@Override
	public void start(@NotNull final OutputStream outputStream, @NotNull final Charset charset) throws CouldNotWriteDataException
	{
		super.start(outputStream, charset);
		xmlStringWriter = new XmlStringWriter(writer);
		if (xmlDeclaration)
		{
			try
			{
				writer.write(format(ENGLISH, "<?xml version=\"1.0\" encoding=\"%1$s\" standalone=\"yes\"?>", charset.name().toUpperCase(ENGLISH)));
			}
			catch (IOException e)
			{
				throw new CouldNotWriteDataException(e);
			}
		}
		try
		{
			writeOpen(rootNodeName);
		}
		catch (CouldNotEncodeDataException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}

	@Override
	public void finish() throws CouldNotWriteDataException
	{
		try
		{
			writeClose(rootNodeName);
		}
		catch (CouldNotEncodeDataException e)
		{
			throw new CouldNotWriteDataException(e);
		}
		finally
		{
			super.finish();
		}
	}

	@Override
	public void writeProperty(@NotNull final String name, @NotNull final String value) throws CouldNotWritePropertyException
	{
		if (value.isEmpty())
		{
			writeEmptyProperty(name);
			return;
		}
		try
		{
			writeOpen(name);
			writeText(value);
			writeClose(name);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException e)
		{
			throw new CouldNotWritePropertyException(name, value, e);
		}
	}

	@Override
	public void writeProperty(@NotNull final String name, @NotNull final MapSerialisable value) throws CouldNotWritePropertyException
	{
		try
		{
			writeOpen(name);
			writeValue(value);
			writeClose(name);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, value, e);
		}
	}

	@Override
	public void writeProperty(@NotNull final String name, @NotNull final ValueSerialisable value) throws CouldNotWritePropertyException
	{
		try
		{
			writeOpen(name);
			writeValue(value);
			writeClose(name);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException | CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, value, e);
		}
	}

	@Override
	public void writePropertyNull(@NonNls @NotNull final String name) throws CouldNotWritePropertyException
	{
		writeEmptyProperty(name);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends MapSerialisable> void writeValue(@NotNull final S[] values) throws CouldNotWriteValueException
	{
		try
		{
			for (final S value : values)
			{
				if (value == null)
				{
					writeEmptyProperty(ListElementNodeName);
				}
				else
				{
					writeProperty(ListElementNodeName, value);
				}
			}
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotWriteValueException(values, e);
		}
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends ValueSerialisable> void writeValue(@NotNull final S[] values) throws CouldNotWriteValueException
	{
		try
		{
			for (final S value : values)
			{
				if (value == null)
				{
					writeEmptyProperty(ListElementNodeName);
				}
				else
				{
					writeProperty(ListElementNodeName, value);
				}
			}
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotWriteValueException(values, e);
		}
	}

	@Override
	public void writeValue(final int value) throws CouldNotWriteValueException
	{
		writeValue(Integer.toString(value));
	}

	@Override
	public void writeValue(final long value) throws CouldNotWriteValueException
	{
		writeValue(Long.toString(value));
	}

	@Override
	public void writeValue(@NotNull final BigDecimal value) throws CouldNotWriteValueException
	{
		writeValue(value.toString());
	}

	@Override
	public void writeValue(@NotNull final String value) throws CouldNotWriteValueException
	{
		try
		{
			writeText(value);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException e)
		{
			throw new CouldNotWriteValueException(value, e);
		}
	}

	@Override
	public void writeValue(@NotNull final MapSerialisable value) throws CouldNotWriteValueException
	{
		try
		{
			value.serialiseMap(this);
		}
		catch (CouldNotSerialiseMapException e)
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
	public void writeValueNull()
	{
	}

	private void writeOpen(final CharSequence name) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		write(LessThan);
		writeNodeName(name);
		write(GreaterThan);
	}

	private void writeClose(final CharSequence name) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		write(LessThanSlash);
		writeNodeName(name);
		write(GreaterThan);
	}

	private void writeEmpty(final CharSequence name) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		write(LessThan);
		writeNodeName(name);
		write(SlashGreaterThan);
	}

	private void writeEmptyProperty(final String name) throws CouldNotWritePropertyException
	{
		try
		{
			writeEmpty(name);
		}
		catch (CouldNotWriteDataException | CouldNotEncodeDataException e)
		{
			throw new CouldNotWritePropertyException(name, "", e);
		}
	}

	private void writeNodeName(final CharSequence name) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		xmlStringWriter.writeNodeName(name);
	}

	private void writeText(final CharSequence value) throws CouldNotWriteDataException, CouldNotEncodeDataException
	{
		xmlStringWriter.writeText(value);
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
}
