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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.common.serialisers.separatedValues.fieldEscapers.FieldEscaper;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.Matcher;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher;

import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Stack;

import static java.lang.System.out;
import static uk.nhs.hdn.common.CharsetHelper.Utf8;
import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;
import static uk.nhs.hdn.common.serialisers.separatedValues.fieldEscapers.CommaSeparatedFieldEscaper.CommaSeparatedFieldEscaperInstance;
import static uk.nhs.hdn.common.serialisers.separatedValues.fieldEscapers.TabSeparatedFieldEscaper.TabSeparatedFieldEscaperInstance;

public final class SeparatedValueSerialiser extends AbstractSerialiser
{
	@NotNull
	public static SeparatedValueSerialiser tabSeparatedValueSerialiser(@SuppressWarnings("TypeMayBeWeakened") @NotNull final RecurseMatcher root, final boolean writeHeaderLine, @NotNull final String... headings)
	{
		return new SeparatedValueSerialiser(TabSeparatedFieldEscaperInstance, root, writeHeaderLine, headings);
	}

	@NotNull
	public static SeparatedValueSerialiser commaSeparatedValueSerialiser(@SuppressWarnings("TypeMayBeWeakened") @NotNull final RecurseMatcher root, final boolean writeHeaderLine, @NotNull final String... headings)
	{
		return new SeparatedValueSerialiser(CommaSeparatedFieldEscaperInstance, root, writeHeaderLine, headings);
	}

	@NotNull
	private Matcher current;
	private final boolean writeHeaderLine;
	@NotNull
	private final String[] headings;
	@NotNull
	private final Stack<Matcher> stack;
	private final int numberOfFields;
	@NotNull
	private final FieldEscaper fieldEscaper;
	@Nullable
	private SeparatedValuesLine separatedValuesLine;

	public SeparatedValueSerialiser(@NotNull final FieldEscaper fieldEscaper, @SuppressWarnings("TypeMayBeWeakened") @NotNull final RecurseMatcher root, final boolean writeHeaderLine, @NotNull final String... headings)
	{
		current = root;
		this.writeHeaderLine = writeHeaderLine;
		this.headings = copyOf(headings);
		numberOfFields = headings.length;
		this.fieldEscaper = fieldEscaper;
		stack = new Stack<>();
	}

	@SafeVarargs
	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "FinalMethodInFinalClass"})
	public final <S extends Serialisable> void printValuesOnStandardOut(@NotNull final S... values)
	{
		try
		{
			start(out, Utf8);
			writeValue(values);
			finish();
		}
		catch (CouldNotWriteDataException | CouldNotWriteValueException e)
		{
			throw new IllegalStateException("Could not write tuples", e);
		}
	}

	@Override
	public void start(@NotNull final OutputStream outputStream, @NotNull final Charset charset) throws CouldNotWriteDataException
	{
		super.start(outputStream, charset);
		if (writeHeaderLine)
		{
			final SimpleSeparatedValuesLine headerLine = new SimpleSeparatedValuesLine(numberOfFields);
			for(int index = 0; index < numberOfFields; index++)
			{
				headerLine.recordValue(index, headings[index]);
			}
			try
			{
				headerLine.writeLine(writer, fieldEscaper);
			}
			catch (CouldNotEncodeDataException e)
			{
				throw new CouldNotWriteDataException(e);
			}
		}
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends MapSerialisable> void writeValue(@NotNull final S[] values) throws CouldNotWriteValueException
	{
		if (separatedValuesLine != null)
		{
			throw new CouldNotWriteValueException(values, "writing of nested MapSerialisable arrays is not supported");
		}
		for (final S value : values)
		{
			separatedValuesLine = new SimpleSeparatedValuesLine(numberOfFields);

			writeValue(value);

			try
			{
				separatedValuesLine.writeLine(writer, fieldEscaper);
			}
			catch (CouldNotEncodeDataException | CouldNotWriteDataException e)
			{
				throw new CouldNotWriteValueException(value, e);
			}
			separatedValuesLine = null;
		}
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends ValueSerialisable> void writeValue(@NotNull final S[] values) throws CouldNotWriteValueException
	{
		if (separatedValuesLine != null)
		{
			final FlatteningValueSerialiser flatteningValueSerialiser = new FlatteningValueSerialiser();
			final StringWriter writer1 = new StringWriter(100);
			flatteningValueSerialiser.start(writer1, Utf8);
			flatteningValueSerialiser.writeValue(values);
			final String flattenedValue = writer1.toString();
			writeValue(flattenedValue);
			return;
		}
		for (final S value : values)
		{
			separatedValuesLine = new SimpleSeparatedValuesLine(numberOfFields);

			writeValue(value);

			try
			{
				separatedValuesLine.writeLine(writer, fieldEscaper);
			}
			catch (CouldNotEncodeDataException | CouldNotWriteDataException e)
			{
				throw new CouldNotWriteValueException(value, e);
			}
			separatedValuesLine = null;
		}
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final String value) throws CouldNotWritePropertyException
	{
		final Matcher matcher = current.matchChild(name);
		matcher.recordValue(value, separatedValuesLine);
	}

	@Override
	public void writePropertyNull(@FieldTokenName @NonNls @NotNull final String name) throws CouldNotWritePropertyException
	{
		writeProperty(name, "");
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final MapSerialisable value) throws CouldNotWritePropertyException
	{
		final Matcher matcher = current.matchChild(name);
		stack.push(current);
		current = matcher;
		try
		{
			writeValue(value);
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, value, e);
		}
		current = stack.pop();
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final ValueSerialisable value) throws CouldNotWritePropertyException
	{
		final Matcher matcher = current.matchChild(name);
		stack.push(current);
		current = matcher;
		try
		{
			writeValue(value);
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, value, e);
		}
		current = stack.pop();
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, final int value) throws CouldNotWritePropertyException
	{
		writeProperty(name, Integer.toString(value));
	}

	@Override
	public void writeValue(@NotNull final String value) throws CouldNotWriteValueException
	{
		current.recordValue(value, separatedValuesLine);
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
	public void writeValueNull() throws CouldNotWriteValueException
	{
		writeValue("");
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

}
