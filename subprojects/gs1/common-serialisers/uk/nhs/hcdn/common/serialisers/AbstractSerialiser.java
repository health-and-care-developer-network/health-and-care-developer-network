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
import java.math.BigDecimal;
import java.nio.charset.Charset;

public abstract class AbstractSerialiser extends AbstractToString implements Serialiser
{
	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@NotNull
	protected Charset charset;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@NotNull
	@ExcludeFromToString
	protected Writer writer;

	protected AbstractSerialiser()
	{
	}

	@SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
	@Override
	public void start(@NotNull final OutputStream outputStream, @NotNull final Charset charset) throws CouldNotWriteDataException
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

		if (value instanceof Serialisable)
		{
			try
			{
				((Serialisable) value).serialise(this);
			}
			catch (CouldNotSerialiseException e)
			{
				throw new CouldNotWriteValueException(value, e);
			}
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
			writeValue((int) value);
			return;
		}

		if (value instanceof Long)
		{
			writeValue((long) value);
			return;
		}

		if (value instanceof BigDecimal)
		{
			writeValue((BigDecimal) value);
			return;
		}

		if (value instanceof String)
		{
			writeValue((String) value);
			return;
		}

		if (value instanceof Enum)
		{
			writeValue(((Enum<?>) value).name());
			return;
		}

		throw new CouldNotWriteValueException(value, "do not know how to write values for this class");
	}
}
