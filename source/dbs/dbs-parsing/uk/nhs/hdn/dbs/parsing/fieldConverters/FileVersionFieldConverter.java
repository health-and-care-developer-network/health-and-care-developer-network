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
import uk.nhs.hdn.dbs.FileFormat;
import uk.nhs.hdn.dbs.FileVersion;

import static uk.nhs.hdn.dbs.FileVersion.fileVersion;

public final class FileVersionFieldConverter implements FieldConverter<FileVersion>
{
	@NotNull
	public static final FieldConverter<?> FileVersionFieldConverterInstance = new FileVersionFieldConverter();

	private FileVersionFieldConverter()
	{
	}

	@NotNull
	@Override
	public FileVersion convert(@NotNull final String fieldValue) throws CouldNotConvertFieldValueException
	{
		@Nullable final FileVersion fileVersion = fileVersion(fieldValue);
		if (fileVersion == null)
		{
			throw new CouldNotConvertFieldValueException(fieldValue, FileFormat.class, "was not a valid file version");
		}
		return fileVersion;
	}
}
