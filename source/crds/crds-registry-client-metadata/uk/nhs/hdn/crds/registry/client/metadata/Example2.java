package uk.nhs.hdn.crds.registry.client.metadata;

import uk.nhs.hdn.common.http.client.ApiMethod;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.crds.registry.client.ConcreteCrdsRestApi;
import uk.nhs.hdn.crds.registry.client.CrdsRestApi;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.metadata.RepositoryMetadataRecord;

import java.net.URI;
import java.util.UUID;

public final class Example2
{
	public void example() throws UnacceptableResponseException, CouldNotConnectHttpException, CorruptResponseException
	{
		final CrdsRestApi crdsRestApi = ConcreteCrdsRestApi.DefaulConcreteCrdsRestApi;

		final RepositoryIdentifier repositoryIdentifier = new RepositoryIdentifier(UUID.fromString("66dad8b0-72c7-4164-a8b2-27ae6b7467cf"));
		final ApiMethod<RepositoryMetadataRecord> repositoryMetadataRecordApiMethod = crdsRestApi.repositoryMetadataRecord(repositoryIdentifier);
		final RepositoryMetadataRecord repositoryMetadataRecord = repositoryMetadataRecordApiMethod.execute();

		final RepositoryIdentifier repositoryIdentifierIdenticalToThatQueriedFor = repositoryMetadataRecord.identifier;
		final long lastModified = repositoryMetadataRecord.lastModified;
		final String systemDescription = repositoryMetadataRecord.systemDescription;
		final URI systemLocation = repositoryMetadataRecord.systemLocation;
	}
}
