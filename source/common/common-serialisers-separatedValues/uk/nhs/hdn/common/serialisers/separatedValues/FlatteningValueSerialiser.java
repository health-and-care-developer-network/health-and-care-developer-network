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

package uk.nhs.hdn.common.serialisers.separatedValues;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.serialisers.*;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.Charset;

public final class FlatteningValueSerialiser extends AbstractValueSerialiser
{
	public void start(@NotNull final Writer writer, @NotNull final Charset charset)
	{
		this.writer = writer;
		this.charset = charset;
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends MapSerialisable> void writeValue(@NotNull final S[] values) throws CouldNotWriteValueException
	{
		for (final S value : values)
		{
			writeValue(value);
		}
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends ValueSerialisable> void writeValue(@NotNull final S[] values) throws CouldNotWriteValueException
	{
		for (final S value : values)
		{
			writeValue(value);
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
			writer.write(value);
		}
		catch (IOException e)
		{
			throw new CouldNotWriteValueException(this, new CouldNotWriteDataException(e));
		}
	}

	@Override
	public void writeValue(@NotNull final MapSerialisable value) throws CouldNotWriteValueException
	{
		throw new CouldNotWriteValueException(value, "maps can not be flattened");
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
		writeValue("");
	}
}
