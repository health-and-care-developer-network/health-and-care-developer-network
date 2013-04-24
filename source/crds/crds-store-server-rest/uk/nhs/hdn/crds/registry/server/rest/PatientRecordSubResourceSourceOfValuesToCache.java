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
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.caching.caching.SourceOfValuesToCache;
import uk.nhs.hdn.crds.registry.domain.SimplePatientRecord;
import uk.nhs.hdn.crds.registry.recordStore.RecordStore;
import uk.nhs.hdn.number.NhsNumber;

public final class PatientRecordSubResourceSourceOfValuesToCache implements SourceOfValuesToCache<NhsNumber, PatientRecordSubResource>
{
	private final RecordStore<NhsNumber, SimplePatientRecord> patientRecordStore;

	public PatientRecordSubResourceSourceOfValuesToCache(@NotNull final RecordStore<NhsNumber, SimplePatientRecord> patientRecordStore)
	{
		this.patientRecordStore = patientRecordStore;
	}

	@Nullable
	@Override
	public PatientRecordSubResource get(@NotNull final NhsNumber key)
	{
		@Nullable final SimplePatientRecord simplePatientRecord = patientRecordStore.get(key);
		if (simplePatientRecord == null)
		{
			return null;
		}
		return new PatientRecordSubResource(simplePatientRecord.lastModified(), 1000, "patientRecord", simplePatientRecord);
	}
}
