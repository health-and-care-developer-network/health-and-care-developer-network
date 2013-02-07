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

package uk.nhs.hdn.dbs.parsing.fixedWidthLineUsers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.fixedWidth.CouldNotConvertFieldsException;
import uk.nhs.hdn.common.parsers.fixedWidth.fixedWidthLineUsers.AbstractFixedWidthLineUser;
import uk.nhs.hdn.common.parsers.fixedWidth.fixedWidthLineUsers.FixedWidthLineUser;
import uk.nhs.hdn.dbs.*;
import uk.nhs.hdn.dbs.response.ResponseHeader;

import static uk.nhs.hdn.dbs.parsing.parsers.ResponseHeaderSingleLineFixedWidthParser.ResponseHeaderFieldsIndex;

public final class ResponseHeaderFixedWidthLineUser extends AbstractFixedWidthLineUser<ResponseHeader>
{
	private static final int FileVersion = index("File Version");
	private static final int FileFormat = index("File Format");
	private static final int RequestingOrganisationCode = index("Requesting Organisation Code");
	private static final int TracingServiceCode = index("Tracing Service Code");
	private static final int FilePreparationTimeAndDate = index("File Preparation Time and Date");
	private static final int FileSequenceNumber = index("File Sequence Number");
	private static final int NumberOfResponseRecords = index("Number of Response Records");

	private static int index(@NotNull @NonNls final String key)
	{
		return ResponseHeaderFieldsIndex.get(key);
	}

	@NotNull
	public static final FixedWidthLineUser<ResponseHeader> ResponseHeaderFixedWidthLineUserInstance = new ResponseHeaderFixedWidthLineUser();

	private ResponseHeaderFixedWidthLineUser()
	{
	}

	@NotNull
	@Override
	public ResponseHeader use(final int zeroBasedLineIndex, @NotNull final Object... collectedFields) throws CouldNotConvertFieldsException
	{
		return new ResponseHeader
		(
			nonNullField(collectedFields, FileVersion.class, FileVersion),
			nonNullField(collectedFields, FileFormat.class, FileFormat),
			nonNullField(collectedFields, SpineDirectoryServiceOrganisationCode.class, RequestingOrganisationCode),
			nonNullField(collectedFields, TracingServiceCode.class, TracingServiceCode),
			nonNullField(collectedFields, DbsDateTime.class, FilePreparationTimeAndDate),
			nonNullField(collectedFields, Integer.class, FileSequenceNumber),
			nonNullField(collectedFields, Integer.class, NumberOfResponseRecords)
		);
	}
}
