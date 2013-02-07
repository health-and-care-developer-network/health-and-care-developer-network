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

package uk.nhs.hdn.dbs.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.dbs.*;
import uk.nhs.hdn.dbs.response.practiceAndAddressData.PracticeAndAddressData;
import uk.nhs.hdn.number.NhsNumber;

import java.util.List;

public final class KnownResponseBody implements ResponseBody
{
	public KnownResponseBody(@NotNull final RecordType responseCode, @NotNull final LocalPatientIdentifier localPatientIdentifier, final int numberOfMultipleMatches, @NotNull final NhsNumber traceResultNhsNumber, @Nullable final DbsDate returnedDateOfBirth, @Nullable final FactOfDeath returnedFactOfDeath, @Nullable final DbsDate returnedDateOfDeath, @Nullable final NameFragment returnedFamilyName, @Nullable final NameFragment returnedGivenName, @Nullable final NameFragment returnedAlternativeOrPreviousSurname, @NotNull final List<NameFragment> returnedOtherGivenNames, @NotNull final Gender returnedGender, @NotNull final PracticeAndAddressData practiceAndAddressData)
	{
	}
}
