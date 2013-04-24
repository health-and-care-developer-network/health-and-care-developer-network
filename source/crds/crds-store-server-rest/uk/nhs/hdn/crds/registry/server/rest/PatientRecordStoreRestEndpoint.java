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

package uk.nhs.hdn.crds.registry.server.rest;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.AbstractRegisterableMethodRoutingRestEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.GetMethodEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.HeadMethodEndpoint;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.MethodEndpoint;
import uk.nhs.hdn.crds.registry.patientRecordStore.PatientRecordStore;
import uk.nhs.hdn.crds.registry.server.eventObservers.ConcurrentAggregatedEventObserver;
import uk.nhs.hdn.number.NhsNumber;

import java.util.Map;

public final class PatientRecordStoreRestEndpoint extends AbstractRegisterableMethodRoutingRestEndpoint<PatientRecordStoreResourceStateSnapshot>
{
	@NotNull private final PatientRecordStoreResourceStateSnapshot patientRecordStoreResourceStateSnapshot;

	public PatientRecordStoreRestEndpoint(final int cacheMaximumNumberOfEntries, @NotNull final PatientRecordStore patientRecordStore, @NotNull final ConcurrentAggregatedEventObserver<NhsNumber> concurrentAggregatedRepositoryEventObserver)
	{
		super("/crds/registry/patient-record-registry/", NoAuthentication);
		// patientRecordStore, concurrentAggregatedRepositoryEventObserver are linked
		patientRecordStoreResourceStateSnapshot = new PatientRecordStoreResourceStateSnapshot(cacheMaximumNumberOfEntries, patientRecordStore, concurrentAggregatedRepositoryEventObserver);
	}

	@Override
	protected void registerMethodEndpointsExcludingOptions(@NotNull final Map<String, MethodEndpoint<PatientRecordStoreResourceStateSnapshot>> methodEndpointsRegister)
	{
		HeadMethodEndpoint.<PatientRecordStoreResourceStateSnapshot>headMethodEndpoint().register(methodEndpointsRegister);
		GetMethodEndpoint.<PatientRecordStoreResourceStateSnapshot>getMethodEndpoint().register(methodEndpointsRegister);
	}

	@NotNull
	@Override
	protected PatientRecordStoreResourceStateSnapshot snapshotOfStateThatIsInvariantForRequest()
	{
		return patientRecordStoreResourceStateSnapshot;
	}

}
