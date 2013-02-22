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
import uk.nhs.hdn.ckan.domain.ids.DatasetId;
import uk.nhs.hdn.ckan.domain.ids.RevisionId;
import uk.nhs.hdn.ckan.domain.uniqueNames.GroupName;
import uk.nhs.hdn.ckan.domain.uniqueNames.TagName;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static java.lang.System.exit;
import static uk.nhs.hdn.ckan.api.Api.DataGovUk;
import static uk.nhs.hdn.ckan.domain.Group.tsvSerialiserForGroups;
import static uk.nhs.hdn.ckan.domain.Licence.tsvSerialiserForLicences;
import static uk.nhs.hdn.ckan.domain.Revision.tsvSerialiserForRevisions;
import static uk.nhs.hdn.ckan.domain.TagCount.tsvSerialiserForTagCounts;
import static uk.nhs.hdn.ckan.domain.dates.MicrosecondTimestamp.microsecondTimestamp;
import static uk.nhs.hdn.ckan.domain.ids.DatasetId.tsvSerialiserForDatasetIds;
import static uk.nhs.hdn.ckan.domain.ids.GroupId.tsvSerialiserForGroupIds;
import static uk.nhs.hdn.ckan.domain.ids.RevisionId.tsvSerialiserForRevisionIds;
import static uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName.tsvSerialiserForDatasetNames;
import static uk.nhs.hdn.ckan.domain.uniqueNames.GroupName.tsvSerialiserForGroupNames;
import static uk.nhs.hdn.ckan.domain.uniqueNames.TagName.tsvSerialiserForTags;

public final class ConsoleEntryPoint
{
	private ConsoleEntryPoint()
	{
	}

	public static void main(@NotNull final String... args) throws UnacceptableResponseException, CouldNotConnectHttpException, CorruptResponseException
	{
		tsvSerialiserForRevisionIds().printValuesOnStandardOut(DataGovUk.revisions(microsecondTimestamp("2012-12-05T19:42:45.854533")).get());
		tsvSerialiserForRevisionIds().printValuesOnStandardOut(DataGovUk.revisions(RevisionId.valueOf("a47db05c-0095-454b-a8f1-25a15404941c")).get());
		tsvSerialiserForTagCounts().printValuesOnStandardOut(DataGovUk.tagCounts().get());

		final DatasetId[] datasetIds = DataGovUk.allDatasetIds().get();
		for (int index = 530; index != -1; index--)
		{
			final DatasetId datasetId = datasetIds[index];
			System.out.println("dataset " + index + " " + datasetId);
			DataGovUk.dataset(datasetId).get();
		}
		exit(0);

		tsvSerialiserForDatasetIds().printValuesOnStandardOut(DataGovUk.datasetIdsWithTag(new TagName("25k-transparency-nhs")).get());

		tsvSerialiserForRevisions().printValuesOnStandardOut(DataGovUk.revision(RevisionId.valueOf("a1f4375a-4afe-4dad-88cd-03f2bd6adaaf")).get());

		final GroupName[] groupNames = DataGovUk.allGroupNames().get();
		for (GroupName groupName : groupNames)
		{
			System.out.println("groupName = " + groupName);
			tsvSerialiserForGroups().printValuesOnStandardOut(DataGovUk.group(groupName).get());
		}

		if (false)
		{
			tsvSerialiserForLicences().printValuesOnStandardOut(DataGovUk.allLicences().get());
			tsvSerialiserForDatasetNames().printValuesOnStandardOut(DataGovUk.allDatasetNames().get());
			tsvSerialiserForDatasetIds().printValuesOnStandardOut(DataGovUk.allDatasetIds().get());
			tsvSerialiserForGroupNames().printValuesOnStandardOut(DataGovUk.allGroupNames().get());
			tsvSerialiserForGroupIds().printValuesOnStandardOut(DataGovUk.allGroupIds().get());
			tsvSerialiserForTags().printValuesOnStandardOut(DataGovUk.allTags().get());
		}
	}
}
