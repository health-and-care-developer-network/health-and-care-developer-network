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
import uk.nhs.hdn.ckan.domain.uniqueNames.GroupName;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.ckan.Api.DataGovUk;
import static uk.nhs.hdn.ckan.domain.Group.tsvSerialiserForGroups;
import static uk.nhs.hdn.ckan.domain.Licence.tsvSerialiserForLicences;
import static uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName.tsvSerialiserForDatasetNames;
import static uk.nhs.hdn.ckan.domain.uniqueNames.GroupName.tsvSerialiserForGroupNames;
import static uk.nhs.hdn.ckan.domain.uniqueNames.TagName.tsvSerialiserForTags;
import static uk.nhs.hdn.ckan.domain.uuids.DatasetId.tsvSerialiserForDatasetIds;
import static uk.nhs.hdn.ckan.domain.uuids.GroupId.tsvSerialiserForGroupIds;

public final class ConsoleEntryPoint
{
	private ConsoleEntryPoint()
	{
	}

	public static void main(@NotNull final String... args) throws UnacceptableResponseException, CouldNotConnectHttpException, CorruptResponseException
	{
		final GroupName[] groupNames = DataGovUk.allGroupNames().get();
		for (GroupName groupName : groupNames)
		{
			System.out.println("groupName = " + groupName);
			tsvSerialiserForGroups(true).printValuesOnStandardOut(DataGovUk.group(groupName).get());
		}

		if (false)
		{
			tsvSerialiserForLicences().printValuesOnStandardOut(DataGovUk.allLicences().get());
			tsvSerialiserForDatasetNames().printValuesOnStandardOut(DataGovUk.allDatasetNames().get());
			tsvSerialiserForDatasetIds().printValuesOnStandardOut(DataGovUk.allDatasetIds().get());
			tsvSerialiserForGroupNames().printValuesOnStandardOut(DataGovUk.allGroupNames().get());
			tsvSerialiserForGroupIds().printValuesOnStandardOut(DataGovUk.allGroupIds().get());
			tsvSerialiserForTags(true).printValuesOnStandardOut(DataGovUk.allTags().get());
		}
	}
}
