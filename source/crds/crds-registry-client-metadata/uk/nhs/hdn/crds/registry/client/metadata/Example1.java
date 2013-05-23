package uk.nhs.hdn.crds.registry.client.metadata;

import uk.nhs.hdn.common.http.client.ApiMethod;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.crds.registry.client.ConcreteCrdsRestApi;
import uk.nhs.hdn.crds.registry.client.CrdsRestApi;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.metadata.ProviderMetadataRecord;

import java.util.LinkedHashSet;
import java.util.UUID;

public final class Example1
{
	public void example() throws UnacceptableResponseException, CouldNotConnectHttpException, CorruptResponseException
	{
		final CrdsRestApi crdsRestApi = ConcreteCrdsRestApi.DefaulConcreteCrdsRestApi;

		final ProviderIdentifier providerIdentifier = new ProviderIdentifier(UUID.fromString("599dbd25-3c3e-4b7a-868b-37b653f394dd"));
		final ApiMethod<ProviderMetadataRecord> providerMetadataRecordApiMethod = crdsRestApi.providerMetadataRecord(providerIdentifier);
		final ProviderMetadataRecord providerMetadataRecord = providerMetadataRecordApiMethod.execute();

		final ProviderIdentifier providerIdentifierIdenticalToThatQueriedFor = providerMetadataRecord.identifier;
		final long lastModified = providerMetadataRecord.lastModified;
		final String organisationalDataServiceCode = providerMetadataRecord.organisationalDataServiceCode;
		final LinkedHashSet<RepositoryIdentifier> repositoryIdentifiers = providerMetadataRecord.repositoryIdentifiers;
	}
}
