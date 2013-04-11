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

package uk.nhs.hdn.crds.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.number.NhsNumber;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static uk.nhs.hdn.crds.domain.PatientRecord.initialPatientRecord;

public final class RootMap
{
	public static final float OptimumHashLoadFactor = 0.7f;

	@NotNull private static final ConcurrentMap<NhsNumberHazelcastSerialisationHolder, PatientRecordHazelcastSerialisationHolder> Root = new ConcurrentHashMap<>(100, OptimumHashLoadFactor);
	@NotNull private final ConcurrentMap<NhsNumberHazelcastSerialisationHolder, PatientRecordHazelcastSerialisationHolder> root;

	public RootMap()
	{
		this(Root);
	}

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public RootMap(@NotNull final ConcurrentMap<NhsNumberHazelcastSerialisationHolder, PatientRecordHazelcastSerialisationHolder> root)
	{
		this.root = root;
	}

	public void addEvent(@NotNull final NhsNumber patientIdentifier, @NotNull final ProviderIdentifier providerIdentifier, @NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final RepositoryEvent repositoryEvent)
	{
		final NhsNumberHazelcastSerialisationHolder nhsNumberHazelcastSerialisationHolder = new NhsNumberHazelcastSerialisationHolder(patientIdentifier);
		@Nullable PatientRecordHazelcastSerialisationHolder oldPatientRecord = (PatientRecordHazelcastSerialisationHolder) root.get(nhsNumberHazelcastSerialisationHolder);
		do
		{
			if (oldPatientRecord == null)
			{
				// No entry, try to add one

				oldPatientRecord = root.putIfAbsent(nhsNumberHazelcastSerialisationHolder, new PatientRecordHazelcastSerialisationHolder(initialPatientRecord(patientIdentifier, providerIdentifier, repositoryIdentifier, repositoryEvent)));

				if (wasNewPatientSuccessfullyAdded(oldPatientRecord))
				{
					return;
				}
			}
			else
			{
				// Existing entry. Try to atomically replace it

				final PatientRecordHazelcastSerialisationHolder newPatientRecord = oldPatientRecord.addRepositoryEvent(providerIdentifier, repositoryIdentifier, repositoryEvent);
				if (root.replace(nhsNumberHazelcastSerialisationHolder, oldPatientRecord, newPatientRecord))
				{
					return;
				}
				oldPatientRecord = root.get(nhsNumberHazelcastSerialisationHolder);
			}
		}
		while (true);
	}

	private static boolean wasNewPatientSuccessfullyAdded(@Nullable final PatientRecordHazelcastSerialisationHolder oldPatientRecord)
	{
		return oldPatientRecord == null;
	}
}
