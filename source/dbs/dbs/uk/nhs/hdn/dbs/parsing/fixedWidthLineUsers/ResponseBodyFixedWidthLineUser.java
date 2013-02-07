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
import uk.nhs.hdn.common.postCodes.PostCode;
import uk.nhs.hdn.dbs.*;
import uk.nhs.hdn.dbs.response.KnownResponseBody;
import uk.nhs.hdn.dbs.response.ResponseBody;
import uk.nhs.hdn.dbs.response.UnknownResponseBody;
import uk.nhs.hdn.dbs.response.practiceAndAddressData.KnownPracticeAndAddressData;
import uk.nhs.hdn.dbs.response.practiceAndAddressData.PracticeAndAddressData;
import uk.nhs.hdn.number.NhsNumber;

import java.util.List;

import static uk.nhs.hdn.dbs.FileVersion.NoPracticeOrPatientAddressReturned;
import static uk.nhs.hdn.dbs.parsing.parsers.ResponseBodySingleLineFixedWidthParser.ResponseBodyFieldsIndex;
import static uk.nhs.hdn.dbs.response.practiceAndAddressData.UnknownPracticeAndAddressData.UnknownPracticeAndAddressDataInstance;

public final class ResponseBodyFixedWidthLineUser extends AbstractFixedWidthLineUser<ResponseBody>
{
	private static final int RecordType = index("Record Type");
	private static final int LocalPatientIdentifier = index("Local PID");
	private static final int NumberOfMultipleMatches = index("Number of Multiple Matches");
	private static final int TraceResultNhsNumber = index("Trace Result NHS Number");
	private static final int ReturnedDateOfBirth = index("Returned Date of Birth");
	private static final int ReturnedFactOfDeath = index("Returned Fact of Death");
	private static final int ReturnedDateOfDeath = index("Returned Date of Death");
	private static final int ReturnedFamilyName = index("Returned Family Name");
	private static final int ReturnedGivenName = index("Returned Given Name");
	private static final int ReturnedAlternativeOrPreviousSurname = index("Returned Alternative or Previous Surname");
	private static final int ReturnedOtherGivenNames = index("Returned Other Given Name(s)");
	private static final int ReturnedGender = index("Returned Gender");

	private static final int ReturnedAddressLine1 = index("Returned Address Line 1");
	private static final int ReturnedAddressLine2 = index("Returned Address Line 2");
	private static final int ReturnedAddressLine3 = index("Returned Address Line 3");
	private static final int ReturnedAddressLine4 = index("Returned Address Line 4");
	private static final int ReturnedAddressLine5 = index("Returned Address Line 5");
	private static final int ReturnedPostCode = index("Returned Post Code");
	private static final int ReturnedRegisteredGpPracticeCode = index("Returned Registered GP Practice Code");

	private static int index(@NotNull @NonNls final String key)
	{
		return ResponseBodyFieldsIndex.get(key);
	}

	@NotNull
	private final FileVersion fileVersion;

	public ResponseBodyFixedWidthLineUser(@NotNull final FileVersion fileVersion)
	{
		this.fileVersion = fileVersion;
	}

	@NotNull
	@Override
	public ResponseBody use(final int zeroBasedLineIndex, @NotNull final Object... collectedFields) throws CouldNotConvertFieldsException
	{
		return constructResponseBody(collectedFields);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	private ResponseBody constructResponseBody(final Object... collectedFields) throws CouldNotConvertFieldsException
	{
		@NotNull final RecordType responseCode = AbstractFixedWidthLineUser.nonNullField(collectedFields, RecordType.class, RecordType);
		@NotNull final LocalPatientIdentifier localPatientIdentifier = AbstractFixedWidthLineUser.nonNullField(collectedFields, LocalPatientIdentifier.class, LocalPatientIdentifier);
		final int numberOfMultipleMatches = AbstractFixedWidthLineUser.nonNullField(collectedFields, Integer.class, NumberOfMultipleMatches);

		if (responseCode.doesNotHaveMatches)
		{
			return new UnknownResponseBody(responseCode, localPatientIdentifier, numberOfMultipleMatches);
		}

		//noinspection ConditionalExpression
		return new KnownResponseBody
		(
			responseCode,
			localPatientIdentifier,
			numberOfMultipleMatches,
			AbstractFixedWidthLineUser.nonNullField(collectedFields, NhsNumber.class, TraceResultNhsNumber),
			nullableField(collectedFields, DbsDate.class, ReturnedDateOfBirth),
			nullableField(collectedFields, FactOfDeath.class, ReturnedFactOfDeath),
			nullableField(collectedFields, DbsDate.class, ReturnedDateOfDeath),
			nullableField(collectedFields, NameFragment.class, ReturnedFamilyName),
			nullableField(collectedFields, NameFragment.class, ReturnedGivenName),
			nullableField(collectedFields, NameFragment.class, ReturnedAlternativeOrPreviousSurname),
			AbstractFixedWidthLineUser.nonNullField(collectedFields, List.class, ReturnedOtherGivenNames),
			AbstractFixedWidthLineUser.nonNullField(collectedFields, Gender.class, ReturnedGender),
			fileVersion == NoPracticeOrPatientAddressReturned ? UnknownPracticeAndAddressDataInstance : constructPracticeAndAddressData(collectedFields)
		);
	}

	private static PracticeAndAddressData constructPracticeAndAddressData(final Object... collectedFields) throws CouldNotConvertFieldsException
	{
		return new KnownPracticeAndAddressData
		(
			nullableField(collectedFields, AddressLine.class, ReturnedAddressLine1),
			nullableField(collectedFields, AddressLine.class, ReturnedAddressLine2),
			nullableField(collectedFields, AddressLine.class, ReturnedAddressLine3),
			nullableField(collectedFields, AddressLine.class, ReturnedAddressLine4),
			nullableField(collectedFields, AddressLine.class, ReturnedAddressLine5),
			nullableField(collectedFields, PostCode.class, ReturnedPostCode),
			nullableField(collectedFields, SpineDirectoryServiceGpPracticeCode.class, ReturnedRegisteredGpPracticeCode)
		);
	}
}
