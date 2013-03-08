package uk.nhs.hdn.ckan.api;

import uk.nhs.hdn.ckan.api.search.SearchCriteria;
import uk.nhs.hdn.ckan.api.search.searchDelegates.SearchDelegate;
import uk.nhs.hdn.ckan.domain.Dataset;
import uk.nhs.hdn.ckan.domain.ids.DatasetId;
import uk.nhs.hdn.ckan.domain.ids.ResourceId;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.ckan.api.ConcreteCkanApi.DataGovUk;
import static uk.nhs.hdn.ckan.api.search.StringSearchCriterion.*;

public class Example3
{
	public void example() throws UnacceptableResponseException, CouldNotConnectHttpException, CorruptResponseException
	{
		// Note that this is highly unlikely to match anything!
		final SearchCriteria<Dataset> datasetSearchCriteria = datasetAnySearchCriterion("health").and(datasetAuthorSearchCriterion("Joe Bloggs")).and(datasetGroupsSearchCriterion("PCTs"));
		final SearchDelegate<DatasetName,Dataset> datasetNameDatasetSearchDelegate = DataGovUk.datasetNameSearchDelegate();
		final DatasetName[] datasetNames = datasetNameDatasetSearchDelegate.allResults(datasetSearchCriteria);

		final DatasetId[] datasetIds = DataGovUk.datasetIdSearchDelegate().allResults(datasetSearchCriteria);

		final ResourceId[] someExcelResources = DataGovUk.resourceSearchDelegate().allResults(resourceFormatSearchCriterion("XLS"));
	}
}
