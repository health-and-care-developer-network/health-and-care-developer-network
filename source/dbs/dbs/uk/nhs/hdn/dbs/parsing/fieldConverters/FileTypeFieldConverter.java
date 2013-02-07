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
import uk.nhs.hdn.dbs.FileType;

import static uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.UnsignedIntegerFieldConverter.UnsignedIntegerFieldConverterInstance;
import static uk.nhs.hdn.dbs.FileType.Request;
import static uk.nhs.hdn.dbs.FileType.Response;
import static uk.nhs.hdn.dbs.FileType.fileType;

public final class FileTypeFieldConverter implements FieldConverter<FileType>
{
	@NotNull
	public static final FieldConverter<?> RequestFileTypeFieldConverterInstance = new FileTypeFieldConverter(Request);

	@NotNull
	public static final FieldConverter<?> ResponseFileTypeFieldConverterInstance = new FileTypeFieldConverter(Response);

	@NotNull
	private final FileType acceptableFileType;

	private FileTypeFieldConverter(@NotNull final FileType acceptableFileType)
	{
		this.acceptableFileType = acceptableFileType;
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

		final FileTypeFieldConverter that = (FileTypeFieldConverter) obj;

		if (acceptableFileType != that.acceptableFileType)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return acceptableFileType.hashCode();
	}

	@NotNull
	@Override
	public FileType convert(@NotNull final String fieldValue) throws CouldNotConvertFieldValueException
	{
		final int value;
		try
		{
			//noinspection ConstantConditions
			value = (Integer) UnsignedIntegerFieldConverterInstance.convert(fieldValue);
		}
		catch (CouldNotConvertFieldValueException e)
		{
			throw new CouldNotConvertFieldValueException(fieldValue, FileType.class, e);
		}
		@Nullable final FileType fileType = fileType(value);
		if (fileType == null)
		{
			throw new CouldNotConvertFieldValueException(fieldValue, FileType.class, "was not a valid file type");
		}
		if (fileType != acceptableFileType)
		{
			throw new CouldNotConvertFieldValueException(fieldValue, FileType.class, "was not an acceptable file type");
		}
		return fileType;
	}
}
