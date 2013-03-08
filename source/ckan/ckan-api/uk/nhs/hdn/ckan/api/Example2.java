package uk.nhs.hdn.ckan.api;

import uk.nhs.hdn.ckan.domain.*;
import uk.nhs.hdn.ckan.domain.dates.MicrosecondTimestamp;
import uk.nhs.hdn.ckan.domain.ids.DatasetId;
import uk.nhs.hdn.ckan.domain.ids.GroupId;
import uk.nhs.hdn.ckan.domain.ids.RevisionId;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetKey;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName;
import uk.nhs.hdn.ckan.domain.uniqueNames.GroupName;
import uk.nhs.hdn.ckan.domain.uniqueNames.TagName;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.ckan.api.ConcreteCkanApi.DataGovUk;
import static uk.nhs.hdn.ckan.api.RelationshipType.linked_from;
import static uk.nhs.hdn.ckan.domain.dates.MicrosecondTimestamp.microsecondTimestamp;

public class Example2
{
	public void example() throws UnacceptableResponseException, CouldNotConnectHttpException, CorruptResponseException
	{
		final GroupName[] allGroupNames = DataGovUk.allGroupNames().execute();
		final GroupId[] allGroupIds = DataGovUk.allGroupIds().execute();
		final TagName[] allTags = DataGovUk.allTags().execute();
		final Licence[] allLicences = DataGovUk.allLicences().execute();
		final TagCount[] allTagCounts = DataGovUk.tagCounts().execute();

		final DatasetKey datasetName = new DatasetName("focus_on_health");
		final Dataset dataset = DataGovUk.dataset(datasetName).execute();

		final DatasetKey datasetId = DatasetId.valueOf("1b45312a-a784-473e-b2f7-def3eadddf96");
		final Dataset anotherDataset = DataGovUk.dataset(datasetName).execute();

		final GroupName aGroupName = allGroupNames[0];
		final TagName aTagName = allTags[0];
		final RevisionId aRevisionId = RevisionId.valueOf("ff24e891-407f-4152-972c-b8052f1d5737");
		final RevisionId sinceRevisionId = aRevisionId;
		final MicrosecondTimestamp sinceTimestamp = microsecondTimestamp("2013-01-28T20:06:30.061645");

		final Group group = DataGovUk.group(aGroupName).execute();

		final DatasetName[] datasetNamesWithTag = DataGovUk.datasetNamesWithTag(aTagName).execute();

		final DatasetId[] datasetIdsWithTag = DataGovUk.datasetIdsWithTag(aTagName).execute();

		final Revision[] revisionsForDataset = DataGovUk.datasetRevisions(datasetName).execute();

		final Revision revision = DataGovUk.revision(aRevisionId).execute();

		final RevisionId[] revisionsSinceRevisioNId = DataGovUk.revisions(sinceRevisionId).execute();

		final RevisionId[] revisionsSinceTimestamp = DataGovUk.revisions(sinceTimestamp).execute();

		final DatasetName[] datasetNamesByLinkedFrom = DataGovUk.datasetRelationshipsByDatasetName((DatasetName) datasetName, linked_from).execute();

		final DatasetId[] datasetIdsLinkedFrom = DataGovUk.datasetRelationshipsByDatasetId(datasetId, linked_from).execute();
	}
}
