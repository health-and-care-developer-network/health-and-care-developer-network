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

import static java.lang.Integer.parseInt;

public final class UnsignedIntegerFieldConverter extends AbstractToString implements FieldConverter<Integer>
{
	@NotNull
	public static final FieldConverter<?> UnsignedIntegerFieldConverterInstance = new UnsignedIntegerFieldConverter();

	private static final int PlusSign = (int) '+';
	private static final int MinusSign = (int) '-';

	private UnsignedIntegerFieldConverter()
	{
	}

	@NotNull
	@Override
	public Integer convert(@NotNull final String fieldValue) throws CouldNotConvertFieldValueException
	{
		if (fieldValue.isEmpty())
		{
			// If 0-padded, will have been stripped back to empty
			return 0;
		}
		final int first = (int) fieldValue.charAt(0);
		if (first == MinusSign || first == PlusSign)
		{
			throw new CouldNotConvertFieldValueException(fieldValue, Integer.class, "it starts with a plus sign or a minus sign");
		}
		try
		{
			return parseInt(fieldValue);
		}
		catch (NumberFormatException e)
		{
			throw new CouldNotConvertFieldValueException(fieldValue, Integer.class, e);
		}
	}
}
