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

package uk.nhs.hdn.dbs.parsing.fieldConverters;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.CouldNotConvertFieldValueException;
import uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.DateTimeFieldConverter;
import uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.FieldConverter;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.dbs.DbsDateTime;

import java.util.Date;

import static java.util.Locale.ENGLISH;

public final class DbsDateTimeFieldConverter extends AbstractToString implements FieldConverter<DbsDateTime>
{
	@NotNull
	public static final FieldConverter<?> DbsDateTimeFieldConverterInstance = new DbsDateTimeFieldConverter();

	@NotNull
	private final DateTimeFieldConverter dbsDateTimeFieldConverter;

	private DbsDateTimeFieldConverter()
	{
		dbsDateTimeFieldConverter = new DateTimeFieldConverter("HHmmssYYYYMMDD", ENGLISH);
	}

	@NotNull
	@Override
	public DbsDateTime convert(@NotNull final String fieldValue) throws CouldNotConvertFieldValueException
	{
		@NotNull final Date converted;
		try
		{
			converted = dbsDateTimeFieldConverter.convert(fieldValue);
		}
		catch (CouldNotConvertFieldValueException e)
		{
			throw new CouldNotConvertFieldValueException(fieldValue, DbsDateTime.class, e);
		}
		return new DbsDateTime(converted.getTime());
	}
}
