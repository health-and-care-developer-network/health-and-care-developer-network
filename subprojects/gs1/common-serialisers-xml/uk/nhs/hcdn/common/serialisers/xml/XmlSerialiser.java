/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers.xml;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;
import uk.nhs.hcdn.common.serialisers.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class XmlSerialiser extends AbstractSerialiser
{
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

	@NotNull @ExcludeFromToString
	private final XmlStringWriter xmlStringWriter;

	public XmlSerialiser(@NotNull final OutputStream outputStream, @NotNull final String rootNodeName)
	{
		this(outputStream, Utf8, rootNodeName);
	}

	public XmlSerialiser(@NotNull final OutputStream outputStream, @NotNull final Charset charset, @NotNull final String rootNodeName)
	{
		super(outputStream, charset);
		this.rootNodeName = rootNodeName;
		xmlStringWriter = new XmlStringWriter(writer);
	}

	@Override
	public void start() throws CouldNotWriteDataException
	{
		try
		{
			writer.write(format(ENGLISH, "<?xml version=\"1.0\" encoding=\"%1$s\"?>", charset.name().toUpperCase(ENGLISH)));
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
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
		try
		{
			writeText(Integer.toString(value));
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
