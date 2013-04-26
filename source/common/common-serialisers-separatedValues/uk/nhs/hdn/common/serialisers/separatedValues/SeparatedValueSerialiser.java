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

import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import static java.lang.System.out;
import static uk.nhs.hdn.common.CharsetHelper.Utf8;
import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;
import static uk.nhs.hdn.common.serialisers.separatedValues.fieldEscapers.CommaSeparatedFieldEscaper.CommaSeparatedFieldEscaperInstance;
import static uk.nhs.hdn.common.serialisers.separatedValues.fieldEscapers.SanitisingTabSeparatedFieldEscaper.SanitisingTabSeparatedFieldEscaperInstance;

@SuppressWarnings({"ClassNamePrefixedWithPackageName", "ClassWithTooManyMethods"})
public final class SeparatedValueSerialiser extends AbstractSerialiser
{
	@NotNull
	public static SeparatedValueSerialiser tabSeparatedValueSerialiser(@NotNull final Matcher root, final boolean writeHeaderLine, @NotNull final String... headings)
	{
		return new SeparatedValueSerialiser(SanitisingTabSeparatedFieldEscaperInstance, root, writeHeaderLine, headings);
	}

	@NotNull
	public static SeparatedValueSerialiser commaSeparatedValueSerialiser(@NotNull final Matcher root, final boolean writeHeaderLine, @NotNull final String... headings)
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

	public SeparatedValueSerialiser(@NotNull final FieldEscaper fieldEscaper, @NotNull final Matcher root, final boolean writeHeaderLine, @NotNull final String... headings)
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
		writeOut(out, values);
	}

	@SuppressWarnings("FinalMethodInFinalClass")
	@SafeVarargs
	public final <S extends Serialisable> void writeOut(@NotNull final OutputStream outputStream, @NotNull final S... values)
	{
		try
		{
			start(outputStream, Utf8);
			writeValue(values);
			finish();
		}
		catch (CouldNotWriteDataException | CouldNotWriteValueException e)
		{
			throw new IllegalStateException("Could not write values", e);
		}
	}

	@Override
	public void start(@NotNull final OutputStream outputStream, @NotNull final Charset charset) throws CouldNotWriteDataException
	{
		super.start(outputStream, charset);
		if (writeHeaderLine)
		{
			final FixedArraySeparatedValuesLine headerLine = new FixedArraySeparatedValuesLine(numberOfFields);
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
			writeNestedMapSerialisableValues(values);
		}
		for (final S value : values)
		{
			separatedValuesLine = new FixedArraySeparatedValuesLine(numberOfFields);

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
			writeNestedValueSerialisableValues(values);
			return;
		}
		for (final S value : values)
		{
			separatedValuesLine = new FixedArraySeparatedValuesLine(numberOfFields);

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
	public void writeValue(@NotNull final List<?> values) throws CouldNotWriteValueException
	{
		if (separatedValuesLine != null)
		{
			writeNestedValueObjectValues(values);
			return;
		}
		for (final Object value : values)
		{
			separatedValuesLine = new FixedArraySeparatedValuesLine(numberOfFields);

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
	public void writeValue(@NotNull final Set<?> values) throws CouldNotWriteValueException
	{
		if (separatedValuesLine != null)
		{
			writeNestedValueObjectValues(values);
			return;
		}
		for (final Object value : values)
		{
			separatedValuesLine = new FixedArraySeparatedValuesLine(numberOfFields);

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

	@SuppressWarnings("FinalMethodInFinalClass")
	@SafeVarargs
	@Override
	public final <S extends Serialisable> void writeValue(@NotNull final S... values) throws CouldNotWriteValueException
	{
		if (separatedValuesLine != null)
		{
			writeNestedValueObjectValues(values);
			return;
		}
		for (final Object value : values)
		{
			separatedValuesLine = new FixedArraySeparatedValuesLine(numberOfFields);

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
	private <S extends MapSerialisable> void writeNestedMapSerialisableValues(final S[] values, final char... separator) throws CouldNotWriteValueException
	{
		final FlatteningValueSerialiser flatteningValueSerialiser = new FlatteningValueSerialiser(separator);
		final StringWriter writer1 = new StringWriter(100);
		flatteningValueSerialiser.start(writer1, Utf8);
		flatteningValueSerialiser.writeValue(values);
		final String flattenedValue = writer1.toString();
		writeValue(flattenedValue);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	private <S extends ValueSerialisable> void writeNestedValueSerialisableValues(final S[] values, final char... separator) throws CouldNotWriteValueException
	{
		final FlatteningValueSerialiser flatteningValueSerialiser = new FlatteningValueSerialiser(separator);
		final StringWriter writer1 = new StringWriter(100);
		flatteningValueSerialiser.start(writer1, Utf8);
		flatteningValueSerialiser.writeValue(values);
		final String flattenedValue = writer1.toString();
		writeValue(flattenedValue);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	private <S extends ValueSerialisable> void writeNestedValueObjectValues(final List<?> values, final char... separator) throws CouldNotWriteValueException
	{
		final FlatteningValueSerialiser flatteningValueSerialiser = new FlatteningValueSerialiser(separator);
		final StringWriter writer1 = new StringWriter(100);
		flatteningValueSerialiser.start(writer1, Utf8);
		flatteningValueSerialiser.writeValue(values);
		final String flattenedValue = writer1.toString();
		writeValue(flattenedValue);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	private <S extends ValueSerialisable> void writeNestedValueObjectValues(final Set<?> values, final char... separator) throws CouldNotWriteValueException
	{
		final FlatteningValueSerialiser flatteningValueSerialiser = new FlatteningValueSerialiser(separator);
		final StringWriter writer1 = new StringWriter(100);
		flatteningValueSerialiser.start(writer1, Utf8);
		flatteningValueSerialiser.writeValue(values);
		final String flattenedValue = writer1.toString();
		writeValue(flattenedValue);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	private <S extends Serialisable> void writeNestedValueObjectValues(final S[] values, final char... separator) throws CouldNotWriteValueException
	{
		final FlatteningValueSerialiser flatteningValueSerialiser = new FlatteningValueSerialiser(separator);
		final StringWriter writer1 = new StringWriter(100);
		flatteningValueSerialiser.start(writer1, Utf8);
		flatteningValueSerialiser.writeValue(values);
		final String flattenedValue = writer1.toString();
		writeValue(flattenedValue);
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final String value)
	{
		final Matcher matcher = current.matchChild(name);
		matcher.recordValue(value, separatedValuesLine);
	}

	@Override
	public void writePropertyNull(@FieldTokenName @NonNls @NotNull final String name)
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
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, final int value)
	{
		writeProperty(name, Integer.toString(value));
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, final long value)
	{
		writeProperty(name, Long.toString(value));
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends MapSerialisable> void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final S[] values) throws CouldNotWritePropertyException
	{
		final Matcher matcher = current.matchChild(name);
		stack.push(current);
		current = matcher;
		try
		{
			writeNestedMapSerialisableValues(values, current.separator());
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, values, e);
		}
		current = stack.pop();
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public <S extends ValueSerialisable> void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final S[] values) throws CouldNotWritePropertyException
	{
		final Matcher matcher = current.matchChild(name);
		stack.push(current);
		current = matcher;
		try
		{
			writeNestedValueSerialisableValues(values, current.separator());
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, values, e);
		}
		current = stack.pop();
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final List<?> values) throws CouldNotWritePropertyException
	{
		final Matcher matcher = current.matchChild(name);
		stack.push(current);
		current = matcher;
		try
		{
			writeNestedValueObjectValues(values, current.separator());
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, values, e);
		}
		current = stack.pop();
	}

	@Override
	public void writeProperty(@FieldTokenName @NonNls @NotNull final String name, @NotNull final Set<?> values) throws CouldNotWritePropertyException
	{
		final Matcher matcher = current.matchChild(name);
		stack.push(current);
		current = matcher;
		try
		{
			writeNestedValueObjectValues(values, current.separator());
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotWritePropertyException(name, values, e);
		}
		current = stack.pop();
	}

	@Override
	public void writeValue(@NotNull final String value)
	{
		current.recordValue(value, separatedValuesLine);
	}

	@Override
	public void writeValue(final int value)
	{
		writeValue(Integer.toString(value));
	}

	@Override
	public void writeValue(final long value)
	{
		writeValue(Long.toString(value));
	}

	@Override
	public void writeValue(@NotNull final BigDecimal value)
	{
		writeValue(value.toString());
	}

	@Override
	public void writeValueNull()
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

	@Override
	public void writeValue(@NotNull final UUID value) throws CouldNotWriteValueException
	{
		writeValue(value.toString());
	}
}
