package uk.nhs.hdn.crds.registry.client.metadata;

import uk.nhs.hdn.common.http.client.ApiMethod;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.crds.registry.client.ConcreteCrdsRestApi;
import uk.nhs.hdn.crds.registry.client.CrdsRestApi;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;
import uk.nhs.hdn.crds.registry.domain.metadata.StuffMetadataRecord;

import java.util.UUID;

public final class Example3
{
	public void example() throws UnacceptableResponseException, CouldNotConnectHttpException, CorruptResponseException
	{
		final CrdsRestApi crdsRestApi = ConcreteCrdsRestApi.DefaulConcreteCrdsRestApi;

		final StuffIdentifier stuffIdentifier = new StuffIdentifier(UUID.fromString("2dbf298f-eed9-474d-bf8b-d70f68b83417"));
		final ApiMethod<StuffMetadataRecord> stuffMetadataRecordApiMethod = crdsRestApi.stuffMetadataRecord(stuffIdentifier);
		final StuffMetadataRecord stuffMetadataRecord = stuffMetadataRecordApiMethod.execute();

		final StuffIdentifier stuffIdentifierIdenticalToThatQueriedFor = stuffMetadataRecord.identifier;
		final long lastModified = stuffMetadataRecord.lastModified;
		final String stuffDescription = stuffMetadataRecord.stuffDescription;
	}
}
