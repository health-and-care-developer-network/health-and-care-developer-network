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

package uk.nhs.hdn.common.serialisers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public abstract class AbstractValueSerialiser extends AbstractToString implements ValueSerialiser, StartFinish
{
	@NotNull
	private static final String TRUE = "true";

	@NotNull
	private static final String FALSE = "false";

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@NotNull
	protected Charset charset;

	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@NotNull
	@ExcludeFromToString
	protected Writer writer;

	protected AbstractValueSerialiser()
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
			writer.flush();
		}
		catch (IOException e)
		{
			throw new CouldNotWriteDataException(e);
		}
	}

	@SuppressWarnings("ConditionalExpression")
	@Override
	public void writeValue(final boolean value) throws CouldNotWriteValueException
	{
		writeValue(convertBooleanToString(value));
	}

	@NotNull
	protected static String convertBooleanToString(final boolean value) {return value ? TRUE : FALSE;}

	// TODO: Needs to be be pulled out and abstracted so that different rules can apply for different serialisations
	@Override
	public void writeValue(@Nullable final Object value) throws CouldNotWriteValueException
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

		if (value instanceof MapSerialisable[])
		{
			writeValue((MapSerialisable[]) value);
			return;
		}

		if (value instanceof ValueSerialisable[])
		{
			writeValue((ValueSerialisable[]) value);
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

		if (value instanceof Boolean)
		{
			writeValue((boolean) value);
			return;
		}

		if (value instanceof Enum)
		{
			writeValue(((Enum<?>) value).name());
			return;
		}

		if (value instanceof List)
		{
			writeValue((List<?>) value);
		}

		if (value instanceof Set)
		{
			writeValue((Set<?>) value);
		}

		if (value instanceof Map)
		{
			writeValue(new GenericMapSerialisable((Map<?, ?>) value));
		}

		throw new CouldNotWriteValueException(value, format(ENGLISH, "do not know how to write values for this class %1$s", value.getClass().getSimpleName()));
	}
}
