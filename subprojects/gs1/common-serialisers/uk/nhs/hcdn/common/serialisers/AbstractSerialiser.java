/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.serialisers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import static java.nio.charset.Charset.forName;

public abstract class AbstractSerialiser extends AbstractToString implements Serialiser
{
	@NotNull
	public static final Charset Utf8 = forName("UTF-8");

	@NotNull
	protected final Charset charset;

	@NotNull
	@ExcludeFromToString
	protected final Writer writer;

	@SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
	protected AbstractSerialiser(@NotNull final OutputStream outputStream, @NotNull final Charset charset)
	{
		this.charset = charset;
		writer = new OutputStreamWriter(outputStream, charset);
	}

	@Override
	public void finish() throws CouldNotWriteDataException
	{
		try
		{
			writer.close();
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}

	@Override
	public final void writeProperty(@NonNls @NotNull final String name, @Nullable final Object value) throws CouldNotWritePropertyException
	{
		if (value == null)
		{
			writePropertyNull(name);
			return;
		}

		if (value instanceof MapSerialisable)
		{
			writeProperty(name, (MapSerialisable) value);
			return;
		}

		if (value instanceof ValueSerialisable)
		{
			writeProperty(name, (ValueSerialisable) value);
			return;
		}

		if (value instanceof String)
		{
			writeProperty(name, (String) value);
			return;
		}

		throw new CouldNotWritePropertyException(name, value, "do not know how to write properties for this class");
	}

	@Override
	public final void writeValue(@Nullable final Object value) throws CouldNotWriteValueException
	{
		if (value == null)
		{
			writeValueNull();
			return;
		}

		if (value instanceof MapSerialisable)
		{
			writeValue((MapSerialisable) value);
			return;
		}

		if (value instanceof ValueSerialisable)
		{
			writeValue((ValueSerialisable) value);
			return;
		}

		if (value instanceof Integer)
		{
			writeValue((int) (Integer) value);
		}

		throw new CouldNotWriteValueException(value, "do not know how to write values for this class");
	}
}
