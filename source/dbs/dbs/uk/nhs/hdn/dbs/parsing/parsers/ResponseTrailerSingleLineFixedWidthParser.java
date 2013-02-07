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

package uk.nhs.hdn.dbs.parsing.parsers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.fixedWidth.FieldSchema;
import uk.nhs.hdn.common.parsers.fixedWidth.SingleLineFixedWidthParser;
import uk.nhs.hdn.common.parsers.parseResultUsers.ParseResultUser;
import uk.nhs.hdn.dbs.response.ResponseTrailer;

import java.util.Map;

import static uk.nhs.hdn.common.parsers.fixedWidth.FieldSchema.*;
import static uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.UnsignedIntegerFieldConverter.UnsignedIntegerFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.DbsDateTimeFieldConverter.DbsDateTimeFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.FileTypeFieldConverter.ResponseFileTypeFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.FileVersionFieldConverter.FileVersionFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.RecordTypeFieldConverter.TrailerRecordTypeFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.SpineDirectoryServiceOrganisationCodeFieldConverter.SpineDirectoryServiceOrganisationCodeFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.TracingServiceCodeFieldConverter.TracingServiceCodeFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fixedWidthLineUsers.ResponseTrailerFixedWidthLineUser.ResponseTrailerFixedWidthLineUserInstance;

public final class ResponseTrailerSingleLineFixedWidthParser extends SingleLineFixedWidthParser<ResponseTrailer>
{
	@NotNull
	private static final FieldSchema[] ResponseTrailerFields = fields
	(
		stringField("Record Type", TrailerRecordTypeFieldConverterInstance, 2),
		stringField("File Type", ResponseFileTypeFieldConverterInstance, 1),
		numberField("File Version", FileVersionFieldConverterInstance, 3),
		stringField("Requesting Organisation Code", SpineDirectoryServiceOrganisationCodeFieldConverterInstance, 14),
		stringField("Tracing Service Code", TracingServiceCodeFieldConverterInstance, 14),
		stringField("File Preparation Time and Date", DbsDateTimeFieldConverterInstance, 14),
		numberField("File Sequence Number", UnsignedIntegerFieldConverterInstance, 8),
		numberField("Number of Response Records", UnsignedIntegerFieldConverterInstance, 6)
	);

	@SuppressWarnings("PublicStaticCollectionField")
	@NotNull
	public static final Map<String, Integer> ResponseTrailerFieldsIndex = index(ResponseTrailerFields);

	public ResponseTrailerSingleLineFixedWidthParser(@NotNull final ParseResultUser<ResponseTrailer> parseResultUser)
	{
		super(ResponseTrailerFixedWidthLineUserInstance, parseResultUser, ResponseTrailerFields);
	}
}
