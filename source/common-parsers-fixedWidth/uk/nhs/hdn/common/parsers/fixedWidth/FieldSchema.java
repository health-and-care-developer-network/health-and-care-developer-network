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

package uk.nhs.hdn.common.parsers.fixedWidth;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.parsers.CouldNotParseException;
import uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.CouldNotConvertFieldValueException;
import uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.FieldConverter;
import uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.MultipleValuesFieldConverter;
import uk.nhs.hdn.common.parsers.fixedWidth.fixedWidthFields.*;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.NullForEmptyFieldConverter.nullForEmpty;
import static uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.StringFieldConverter.StringFieldConverterInstance;

public final class FieldSchema extends AbstractToString
{
	@SuppressWarnings("MagicCharacter")
	public static FieldSchema multipleValuesField(@NotNull @NonNls final String description, @NotNull final FieldConverter<?> fieldConverter, final int width)
	{
		return stringField(description, new MultipleValuesFieldConverter<>(' ', true, fieldConverter), width);
	}

	@NotNull
	public static FieldSchema notSupportedField(final int width)
	{
		return new FieldSchema("", new DiscardFixedWidthField(width), StringFieldConverterInstance);
	}

	@NotNull
	public static FieldSchema ignoredAsProvidedInTheRequestField(@NotNull @NonNls final String description, final int width)
	{
		return new FieldSchema(description, new UnpaddedFixedWidthField(width), StringFieldConverterInstance);
	}

	@NotNull
	public static FieldSchema nullableField(@NotNull @NonNls final String description, @NotNull final FieldConverter<?> fieldConverter, final int width)
	{
		return stringField(description, nullForEmpty(fieldConverter), width);
	}

	@SuppressWarnings("MagicCharacter")
	@NotNull
	public static FieldSchema stringField(@NotNull @NonNls final String description, @NotNull final FieldConverter<?> fieldConverter, final int width)
	{
		return new FieldSchema(description, new RightPaddedFixedWidthField(' ', width), fieldConverter);
	}

	@SuppressWarnings("MagicCharacter")
	@NotNull
	public static FieldSchema numberField(@NotNull @NonNls final String description, @NotNull final FieldConverter<?> fieldConverter, final int width)
	{
		return new FieldSchema(description, new LeftPaddedFixedWidthField('0', width), fieldConverter);
	}

	@NotNull
	public static FieldSchema[] fields(@NotNull final FieldSchema... fields)
	{
		return fields;
	}

	@NotNull
	public static Map<String, Integer> index(@NotNull final FieldSchema... fieldSchemas)
	{
		final int length = fieldSchemas.length;
		final Map<String, Integer> index = new HashMap<>(length);
		for (int position = 0; position < length; position++)
		{
			fieldSchemas[position].addToIndex(index, position);
		}
		return unmodifiableMap(index);
	}

	@NotNull @NonNls
	private final String description;
	@NotNull
	private final FixedWidthField fixedWidthField;
	@NotNull
	private final FieldConverter<?> fieldConverter;

	public FieldSchema(@NotNull @NonNls final String description, @NotNull final FixedWidthField fixedWidthField, @NotNull final FieldConverter<?> fieldConverter)
	{
		this.description = description;
		this.fixedWidthField = fixedWidthField;
		this.fieldConverter = fieldConverter;
	}

	@Nullable
	public Object convert(@NotNull final String value) throws CouldNotConvertFieldValueException
	{
		return fieldConverter.convert(value);
	}

	@Nullable
	public Object parseAndConvert(@NotNull final BufferedReader bufferedReader) throws IOException, CouldNotParseException, CouldNotConvertFieldValueException
	{
		return convert(fixedWidthField.parse(bufferedReader));
	}

	private void addToIndex(@NotNull final Map<String, Integer> index, final int position)
	{
		if (description.isEmpty())
		{
			return;
		}
		if (index.put(description, position) != null)
		{
			throw new IllegalStateException("Already indexed");
		}
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final FieldSchema that = (FieldSchema) obj;

		if (!description.equals(that.description))
		{
			return false;
		}
		if (!fieldConverter.equals(that.fieldConverter))
		{
			return false;
		}
		if (!fixedWidthField.equals(that.fixedWidthField))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = description.hashCode();
		result = 31 * result + fixedWidthField.hashCode();
		result = 31 * result + fieldConverter.hashCode();
		return result;
	}
}
