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

package uk.nhs.hdn.crds.store.server.standalone.patientRecordStore;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.store.domain.SimplePatientRecord;
import uk.nhs.hdn.crds.store.server.eventObservers.EventObserver;
import uk.nhs.hdn.number.NhsNumber;

import java.util.concurrent.ConcurrentHashMap;

public final class StrippedLockingStandalonePatientRecordStore extends AbstractStandalonePatientRecordStore
{
	private static final float OptimumLoadFactor = 0.7f;
	private static final int SixtyFourThreads = 64;

	public StrippedLockingStandalonePatientRecordStore(@NotNull final EventObserver<NhsNumber> eventObserver)
	{
		// Java's ConcurrentHashMap uses stripped locking
		super(new ConcurrentHashMap<NhsNumber, SimplePatientRecord>(100, OptimumLoadFactor, SixtyFourThreads), eventObserver);
	}
}
