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

package uk.nhs.hdn.dbs.response.parsing.parsers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.parsers.fixedWidth.FieldSchema;
import uk.nhs.hdn.common.parsers.fixedWidth.SingleLineFixedWidthParser;
import uk.nhs.hdn.common.parsers.parseResultUsers.ParseResultUser;
import uk.nhs.hdn.dbs.FileVersion;
import uk.nhs.hdn.dbs.response.parsing.fixedWidthLineUsers.ResponseBodyFixedWidthLineUser;
import uk.nhs.hdn.dbs.response.ResponseBody;

import java.util.Map;

import static uk.nhs.hdn.common.parsers.fixedWidth.FieldSchema.*;
import static uk.nhs.hdn.common.parsers.fixedWidth.fieldConverters.UnsignedIntegerFieldConverter.UnsignedIntegerFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.AddressLineFieldConverter.AddressLineFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.DbsDateFieldConverter.DbsDateFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.FactOfDeathFieldConverter.FactOfDeathFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.GenderFieldConverter.GenderFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.LocalPatientIdentifierFieldConverter.LocalPatientIdentifierFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.NameFragmentFieldConverter.NameFragmentFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.NhsNumberFieldConverter.NhsNumberFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.PostCodeFieldConverter.PostCodeFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.RecordTypeFieldConverter.ResponseBodyRecordTypeFieldConverterInstance;
import static uk.nhs.hdn.dbs.parsing.fieldConverters.SpineDirectoryServiceGpPracticeCodeFieldConverter.SpineDirectoryServiceGpNationalCodeFieldConverterInstance;

public final class ResponseBodySingleLineFixedWidthParser extends SingleLineFixedWidthParser<ResponseBody>
{
	@SuppressWarnings("PublicStaticArrayField")
	@NotNull
	public static final FieldSchema[] ResponseBodyFields = fields
	(
		stringField("Record Type", ResponseBodyRecordTypeFieldConverterInstance, 2),
		stringField("Local PID", LocalPatientIdentifierFieldConverterInstance, 20),
		numberField("Number of Multiple Matches", UnsignedIntegerFieldConverterInstance, 3),
		nullableField("Trace Result NHS Number", NhsNumberFieldConverterInstance, 10),
		ignoredAsProvidedInTheRequestField("Date of Birth", 8),
		ignoredAsProvidedInTheRequestField("Date of Death", 8),
		ignoredAsProvidedInTheRequestField("Old NHS Number", 17),
		ignoredAsProvidedInTheRequestField("NHS Number", 10),
		ignoredAsProvidedInTheRequestField("Family Name", 35),
		ignoredAsProvidedInTheRequestField("Previous / Alternative Family Name", 35),
		ignoredAsProvidedInTheRequestField("Given Name", 35),
		ignoredAsProvidedInTheRequestField("Previous / Alternative Given Name", 35),
		ignoredAsProvidedInTheRequestField("Gender", 1),
		ignoredAsProvidedInTheRequestField("Address Line 1", 35),
		ignoredAsProvidedInTheRequestField("Address Line 2", 35),
		ignoredAsProvidedInTheRequestField("Address Line 3", 35),
		ignoredAsProvidedInTheRequestField("Address Line 4", 35),
		ignoredAsProvidedInTheRequestField("Address Line 5", 35),
		ignoredAsProvidedInTheRequestField("Post Code", 8), // This is a PostCodeMatcher, not a post code
		ignoredAsProvidedInTheRequestField("Previous Address Line 1", 35),
		ignoredAsProvidedInTheRequestField("Previous Address Line 2", 35),
		ignoredAsProvidedInTheRequestField("Previous Address Line 3", 35),
		ignoredAsProvidedInTheRequestField("Previous Address Line 4", 35),
		ignoredAsProvidedInTheRequestField("Previous Address Line 5", 35),
		ignoredAsProvidedInTheRequestField("Previous Post Code", 8), // This is a PostCodeMatcher, not a post code
		ignoredAsProvidedInTheRequestField("Registered GP", 8),
		ignoredAsProvidedInTheRequestField("Registered GP Practice", 6),
		ignoredAsProvidedInTheRequestField("Previous Registered GP", 8),
		ignoredAsProvidedInTheRequestField("Previous Registered GP Practice", 6),
		nullableField("Returned Date of Birth", DbsDateFieldConverterInstance, 8),
		notSupportedField(8),
		nullableField("Returned Fact of Death", FactOfDeathFieldConverterInstance, 3),
		notSupportedField(8),
		nullableField("Returned Date of Death", DbsDateFieldConverterInstance, 8),
		notSupportedField(8),
		nullableField("Returned Family Name", NameFragmentFieldConverterInstance, 35),
		notSupportedField(8),
		nullableField("Returned Given Name", NameFragmentFieldConverterInstance, 35),
		notSupportedField(8),
		nullableField("Returned Alternative or Previous Surname", NameFragmentFieldConverterInstance, 35),
		notSupportedField(8),
		multipleValuesField("Returned Other Given Name(s)", NameFragmentFieldConverterInstance, 35),
		notSupportedField(8),
		nullableField("Returned Gender", GenderFieldConverterInstance, 1),
		notSupportedField(8),
		nullableField("Returned Address Line 1", AddressLineFieldConverterInstance, 35),
		notSupportedField(8),
		nullableField("Returned Address Line 2", AddressLineFieldConverterInstance, 35),
		notSupportedField(8),
		nullableField("Returned Address Line 3", AddressLineFieldConverterInstance, 35),
		notSupportedField(8),
		nullableField("Returned Address Line 4", AddressLineFieldConverterInstance, 35),
		notSupportedField(8),
		nullableField("Returned Address Line 5", AddressLineFieldConverterInstance, 35),
		notSupportedField(8),
		nullableField("Returned Post Code", PostCodeFieldConverterInstance, 35), // This is a Post Code
		notSupportedField(8),
		notSupportedField(8),
		notSupportedField(8),
		nullableField("Returned Registered GP Practice Code", SpineDirectoryServiceGpNationalCodeFieldConverterInstance, 6),
		notSupportedField(8),
		notSupportedField(5)
	);

	@SuppressWarnings("PublicStaticCollectionField")
	@NotNull
	public static final Map<String, Integer> ResponseBodyFieldsIndex = index(ResponseBodyFields);

	public ResponseBodySingleLineFixedWidthParser(@NotNull final FileVersion fileVersion, @NotNull final ParseResultUser<ResponseBody> parseResultUser)
	{
		super(new ResponseBodyFixedWidthLineUser(fileVersion), parseResultUser, ResponseBodyFields);
	}
}
