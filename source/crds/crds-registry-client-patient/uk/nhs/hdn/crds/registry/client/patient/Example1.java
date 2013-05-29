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

package uk.nhs.hdn.crds.registry.client.patient;

import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashMap;
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashSet;
import uk.nhs.hdn.common.http.client.ApiMethod;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.crds.registry.client.ConcreteCrdsRestApi;
import uk.nhs.hdn.crds.registry.client.CrdsRestApi;
import uk.nhs.hdn.crds.registry.domain.*;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffEventIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;
import uk.nhs.hdn.number.NhsNumber;

public class Example1
{
	public void example() throws UnacceptableResponseException, CouldNotConnectHttpException, CorruptResponseException
	{
		final CrdsRestApi crdsRestApi = ConcreteCrdsRestApi.DefaulConcreteCrdsRestApi;

		final NhsNumber patientNhsNumber = NhsNumber.valueOf("123456881");
		final ApiMethod<SimplePatientRecord> simplePatientRecordApiMethod = crdsRestApi.simplePatientRecord(patientNhsNumber);
		final SimplePatientRecord simplePatientRecord = simplePatientRecordApiMethod.execute();

		final NhsNumber patientIdentifier = simplePatientRecord.patientIdentifier;
		@MillisecondsSince1970 final long lastModified = simplePatientRecord.lastModified;
		final HazelcastAwareLinkedHashMap<ProviderIdentifier,ProviderRecord> knownProviders = simplePatientRecord.knownProviders;
		for (final ProviderRecord providerRecord : knownProviders.values())
		{
			final ProviderIdentifier providerIdentifier = providerRecord.providerIdentifier;
			final HazelcastAwareLinkedHashMap<RepositoryIdentifier,RepositoryRecord> knownRepositories = providerRecord.knownRepositories;
			for (final RepositoryRecord repositoryRecord : knownRepositories.values())
			{
				final RepositoryIdentifier repositoryIdentifier = repositoryRecord.repositoryIdentifier;
				final HazelcastAwareLinkedHashMap<StuffIdentifier,StuffRecord> stuffRecords = repositoryRecord.stuffRecords;
				for (final StuffRecord stuffRecord : stuffRecords.values())
				{
					final StuffIdentifier stuffIdentifier = stuffRecord.stuffIdentifier;
					final HazelcastAwareLinkedHashSet<StuffEvent> stuffEvents = stuffRecord.stuffEvents;
					for (final StuffEvent stuffEvent : stuffEvents)
					{
						final StuffEventIdentifier stuffEventIdentifier = stuffEvent.stuffEventIdentifier;
						@MillisecondsSince1970 final long timestamp = stuffEvent.timestamp;
						final StuffEventKind stuffEventKind = stuffEvent.stuffEventKind;
					}
				}
			}
		}
	}
}
