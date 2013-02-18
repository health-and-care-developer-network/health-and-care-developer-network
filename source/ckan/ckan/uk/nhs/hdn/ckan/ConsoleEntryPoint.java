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

package uk.nhs.hdn.ckan;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.ckan.Api.DataGovUk;
import static uk.nhs.hdn.ckan.domain.DatasetId.tsvSerialiserForDatasetIds;
import static uk.nhs.hdn.ckan.domain.GroupId.tsvSerialiserForGroupIds;
import static uk.nhs.hdn.ckan.domain.Licence.tsvSerialiserForLicences;
import static uk.nhs.hdn.ckan.domain.strings.DatasetNameString.tsvSerialiserForDatasetNames;
import static uk.nhs.hdn.ckan.domain.strings.GroupNameString.tsvSerialiserForGroupNames;
import static uk.nhs.hdn.ckan.domain.strings.TagString.tsvSerialiserForTags;

public final class ConsoleEntryPoint
{
	private ConsoleEntryPoint()
	{
	}

	public static void main(@NotNull final String... args) throws UnacceptableResponseException, CouldNotConnectHttpException, CorruptResponseException
	{
		tsvSerialiserForLicences().printValuesOnStandardOut(DataGovUk.allLicences().get());
		tsvSerialiserForDatasetNames().printValuesOnStandardOut(DataGovUk.allDatasetNames().get());
		tsvSerialiserForDatasetIds().printValuesOnStandardOut(DataGovUk.allDatasetIds().get());
		tsvSerialiserForGroupNames().printValuesOnStandardOut(DataGovUk.allGroupNames().get());
		tsvSerialiserForGroupIds().printValuesOnStandardOut(DataGovUk.allGroupIds().get());
		tsvSerialiserForTags(true).printValuesOnStandardOut(DataGovUk.allTags().get());
	}
}
