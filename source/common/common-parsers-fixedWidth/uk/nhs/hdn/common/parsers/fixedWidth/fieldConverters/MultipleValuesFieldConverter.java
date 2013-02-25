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

package uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class MultipleValuesFieldConverter<V> extends AbstractToString implements FieldConverter<List<V>>
{
	private final int separator;
	private final boolean ignoreEmptyValues;
	@NotNull
	private final FieldConverter<V> fieldConverter;

	public MultipleValuesFieldConverter(final char separator, final boolean ignoreEmptyValues, @NotNull final FieldConverter<V> fieldConverter)
	{
		this.ignoreEmptyValues = ignoreEmptyValues;
		this.fieldConverter = fieldConverter;
		this.separator = (int) separator;
	}

	@NotNull
	@Override
	public List<V> convert(@NotNull final String fieldValue) throws CouldNotConvertFieldValueException
	{
		final List<V> values = new ArrayList<>(10);
		int lastSplitAtIndex = -1;
		final int length = fieldValue.length();
		for(int index = 0; index < length; index++)
		{
			final int character = (int) fieldValue.charAt(index);
			if (character == separator)
			{
				final String split = fieldValue.substring(lastSplitAtIndex + 1, index);
				convertValue(split, values);
				lastSplitAtIndex = index;
			}
		}
		final String split = fieldValue.substring(lastSplitAtIndex + 1, length);
		convertValue(split, values);
		return values;
	}

	private void convertValue(final String split, final Collection<V> values) throws CouldNotConvertFieldValueException
	{
		if (ignoreEmptyValues && split.isEmpty())
		{
			return;
		}
		values.add(fieldConverter.convert(split));
	}
}
