package uk.nhs.hdn.ckan.api;

import uk.nhs.hdn.ckan.domain.ids.DatasetId;
import uk.nhs.hdn.ckan.domain.uniqueNames.DatasetName;
import uk.nhs.hdn.common.http.client.ApiMethod;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

import static uk.nhs.hdn.ckan.api.ConcreteCkanApi.DataGovUk;

public class Example1
{
	public void example() throws UnacceptableResponseException, CouldNotConnectHttpException, CorruptResponseException
	{
		final ApiMethod<DatasetName[]> getAllDatasetNames = DataGovUk.allDatasetNames();
		final DatasetName[] allDatasetNames = getAllDatasetNames.execute();

		final DatasetId[] allDatasetIds = DataGovUk.allDatasetIds().execute();
	}
}
