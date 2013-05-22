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

package uk.nhs.hdn.crds.registry.server;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.server.eventObservers.EventObserver;
import uk.nhs.hdn.crds.registry.patientRecordStore.PatientRecordStore;
import uk.nhs.hdn.crds.registry.server.standalone.patientRecordStore.NonBlockingStandalonePatientRecordStore;
import uk.nhs.hdn.crds.registry.server.standalone.patientRecordStore.StrippedLockingStandalonePatientRecordStore;
import uk.nhs.hdn.number.NhsNumber;

public enum PatientRecordStoreKind
{
	Hazelcast
	{
		@NotNull
		@Override
		public PatientRecordStore create(@NotNull final HazelcastConfiguration hazelcastConfiguration, @NotNull final EventObserver<NhsNumber> eventObserver)
		{
			return hazelcastConfiguration.rootMap(eventObserver);
		}
	},
	NonBlockingStandalone
	{
		@NotNull
		@Override
		public PatientRecordStore create(@NotNull final HazelcastConfiguration hazelcastConfiguration, @NotNull final EventObserver<NhsNumber> eventObserver)
		{
			return new NonBlockingStandalonePatientRecordStore( eventObserver);
		}
	},
	StrippedStandalone
	{
		@NotNull
		@Override
		public PatientRecordStore create(@NotNull final HazelcastConfiguration hazelcastConfiguration, @NotNull final EventObserver<NhsNumber> eventObserver)
		{
			return new StrippedLockingStandalonePatientRecordStore( eventObserver);
		}
	},
	;

	@NotNull
	public abstract PatientRecordStore create(@NotNull final HazelcastConfiguration hazelcastConfiguration, @NotNull final EventObserver<NhsNumber> eventObserver);
}
