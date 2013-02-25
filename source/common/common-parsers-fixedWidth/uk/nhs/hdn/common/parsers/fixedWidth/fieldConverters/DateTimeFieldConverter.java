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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateTimeFieldConverter extends AbstractToString implements FieldConverter<Date>
{
	@NotNull @NonNls
	private final String simpleDateFormat;
	@NotNull
	private final Locale locale;

	public DateTimeFieldConverter(@NotNull @NonNls final String simpleDateFormat, @NotNull final Locale locale)
	{
		this.simpleDateFormat = simpleDateFormat;
		this.locale = locale;
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

		final DateTimeFieldConverter that = (DateTimeFieldConverter) obj;

		if (!locale.equals(that.locale))
		{
			return false;
		}
		if (!simpleDateFormat.equals(that.simpleDateFormat))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = simpleDateFormat.hashCode();
		result = 31 * result + locale.hashCode();
		return result;
	}

	@NotNull
	@Override
	public Date convert(@NotNull final String fieldValue) throws CouldNotConvertFieldValueException
	{
		try
		{
			return new SimpleDateFormat(simpleDateFormat, locale).parse(fieldValue);
		}
		catch (ParseException e)
		{
			throw new CouldNotConvertFieldValueException(fieldValue, Date.class, e);
		}
	}
}
