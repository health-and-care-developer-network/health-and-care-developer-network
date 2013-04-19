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

package uk.nhs.hdn.crds.store.rest.metadata;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.AbstractRegisterableMethodRoutingRestEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.GetMethodEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.HeadMethodEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.MethodEndpoint;
import uk.nhs.hdn.crds.store.domain.identifiers.Identifier;
import uk.nhs.hdn.crds.store.domain.metadata.AbstractMetadataRecord;
import uk.nhs.hdn.crds.store.domain.metadata.IdentifierConstructor;
import uk.nhs.hdn.crds.store.eventObservers.ConcurrentAggregatedEventObserver;
import uk.nhs.hdn.crds.store.recordStore.RecordStore;

import java.util.Map;

import static java.util.Locale.UK;

public final class MetadataRecordRestEndpoint extends AbstractRegisterableMethodRoutingRestEndpoint<MetadataRecordResourceStateSnapshot>
{
	@NotNull private final MetadataRecordResourceStateSnapshot metadataRecordResourceStateSnapshot;

	public MetadataRecordRestEndpoint(@NotNull final IdentifierConstructor identifierConstructor, final int cacheMaximumNumberOfEntries, @NotNull final RecordStore<Identifier, AbstractMetadataRecord<?>> metadataRecordStore, @NotNull final ConcurrentAggregatedEventObserver<Identifier> concurrentAggregatedRepositoryEventObserver)
	{
		super("/crds/metadata/" + identifierConstructor.name().toLowerCase(UK), NoAuthentication);

		// patientRecordStore, concurrentAggregatedRepositoryEventObserver are linked
		metadataRecordResourceStateSnapshot = new MetadataRecordResourceStateSnapshot(identifierConstructor, cacheMaximumNumberOfEntries, metadataRecordStore, concurrentAggregatedRepositoryEventObserver);
	}

	@Override
	protected void registerMethodEndpointsExcludingOptions(@NotNull final Map<String, MethodEndpoint<MetadataRecordResourceStateSnapshot>> methodEndpointsRegister)
	{
		HeadMethodEndpoint.<MetadataRecordResourceStateSnapshot>headMethodEndpoint().register(methodEndpointsRegister);
		GetMethodEndpoint.<MetadataRecordResourceStateSnapshot>getMethodEndpoint().register(methodEndpointsRegister);
	}

	@NotNull
	@Override
	protected MetadataRecordResourceStateSnapshot snapshotOfStateThatIsInvariantForRequest()
	{
		return metadataRecordResourceStateSnapshot;
	}
}
