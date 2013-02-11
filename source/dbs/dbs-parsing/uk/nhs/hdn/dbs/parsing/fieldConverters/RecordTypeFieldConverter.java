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
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.CouldNotConvertFieldValueException;
import uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.FieldConverter;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.dbs.FileFormat;
import uk.nhs.hdn.dbs.RecordType;

import java.util.Set;

import static java.util.EnumSet.of;
import static uk.nhs.hdn.dbs.RecordType.*;

public final class RecordTypeFieldConverter extends AbstractToString implements FieldConverter<RecordType>
{
	@NotNull public static final FieldConverter<?> HeaderRecordTypeFieldConverterInstance = new RecordTypeFieldConverter(Header);
	@NotNull public static final FieldConverter<?> RequestBodyRecordTypeFieldConverterInstance = new RecordTypeFieldConverter(RequestBody);
	@NotNull public static final FieldConverter<?> TrailerRecordTypeFieldConverterInstance = new RecordTypeFieldConverter(Trailer);
	@NotNull public static final FieldConverter<?> ResponseBodyRecordTypeFieldConverterInstance = new RecordTypeFieldConverter(validRecordTypesForResponseBody());

	@NotNull private final Set<RecordType> acceptableRecordTypes;

	private RecordTypeFieldConverter(@NotNull final RecordType acceptableRecordType)
	{
		this(of(acceptableRecordType));
	}

	private RecordTypeFieldConverter(@NotNull final Set<RecordType> acceptableRecordTypes)
	{
		this.acceptableRecordTypes = acceptableRecordTypes;
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

		final RecordTypeFieldConverter that = (RecordTypeFieldConverter) obj;

		if (!acceptableRecordTypes.equals(that.acceptableRecordTypes))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return acceptableRecordTypes.hashCode();
	}

	@NotNull
	@Override
	public RecordType convert(@NotNull final String fieldValue) throws CouldNotConvertFieldValueException
	{
		@Nullable final RecordType fileVersion = recordType(fieldValue);
		if (fileVersion == null)
		{
			throw new CouldNotConvertFieldValueException(fieldValue, FileFormat.class, "was not a valid record type");
		}
		if (!acceptableRecordTypes.contains(fileVersion))
		{
			throw new CouldNotConvertFieldValueException(fieldValue, FileFormat.class, "was not an unacceptable record type");
		}
		return fileVersion;
	}
}
