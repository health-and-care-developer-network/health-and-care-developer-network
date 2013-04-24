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

package uk.nhs.hdn.crds.registry.patientRecordStore;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.registry.domain.SimplePatientRecord;
import uk.nhs.hdn.crds.registry.domain.StuffEvent;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;
import uk.nhs.hdn.crds.registry.recordStore.RecordStore;
import uk.nhs.hdn.number.NhsNumber;

public interface PatientRecordStore extends RecordStore<NhsNumber, SimplePatientRecord>
{
	void addEvent(@NotNull NhsNumber patientIdentifier, @NotNull ProviderIdentifier providerIdentifier, @NotNull RepositoryIdentifier repositoryIdentifier, @NotNull StuffIdentifier stuffIdentifier, @NotNull StuffEvent stuffEvent);
}
