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
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.hazelcast.HazelcastAwareLinkedHashMap;
import uk.nhs.hdn.common.hazelcast.DataWriter;

import java.io.DataOutput;
import java.io.IOException;

import static uk.nhs.hdn.crds.domain.RepositoryRecord.initialRepositoryRecords;
import static uk.nhs.hdn.crds.domain.RepositoryRecord.repositoryRecord;

public final class ProviderRecord extends AbstractToString implements DataWriter
{
	@NotNull
	public static HazelcastAwareLinkedHashMap<ProviderIdentifier, ProviderRecord> initialProviderRecords(@NotNull final ProviderIdentifier providerIdentifier, @NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final RepositoryEvent repositoryEvent)
	{
		return new HazelcastAwareLinkedHashMap<>(providerIdentifier, providerRecord(providerIdentifier, repositoryIdentifier, repositoryEvent));
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static ProviderRecord providerRecord(@NotNull final ProviderIdentifier providerIdentifier, @NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final RepositoryEvent repositoryEvent)
	{
		return new ProviderRecord(providerIdentifier, initialRepositoryRecords(repositoryIdentifier, repositoryEvent));
	}

	@NotNull private final ProviderIdentifier providerIdentifier;
	@NotNull private final HazelcastAwareLinkedHashMap<RepositoryIdentifier, RepositoryRecord> knownRepositories;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public ProviderRecord(@NotNull final ProviderIdentifier providerIdentifier, @NotNull final HazelcastAwareLinkedHashMap<RepositoryIdentifier, RepositoryRecord> knownRepositories)
	{
		this.providerIdentifier = providerIdentifier;
		this.knownRepositories = knownRepositories;
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		providerIdentifier.writeData(out);
		knownRepositories.writeData(out);
	}

	@SuppressWarnings("FeatureEnvy")
	@NotNull
	public ProviderRecord addRepositoryEvent(@NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final RepositoryEvent repositoryEvent)
	{
		@Nullable final RepositoryRecord existingRepositoryRecord = knownRepositories.get(repositoryIdentifier);
		final RepositoryRecord repositoryRecord;
		if (existingRepositoryRecord == null)
		{
			repositoryRecord = repositoryRecord(repositoryIdentifier, repositoryEvent);
		}
		else
		{
			repositoryRecord = existingRepositoryRecord.addRepositoryEvent(repositoryEvent);
		}
		final HazelcastAwareLinkedHashMap<RepositoryIdentifier, RepositoryRecord> replacementKnownRepositories = new HazelcastAwareLinkedHashMap<>(knownRepositories, repositoryIdentifier, repositoryRecord);
		return new ProviderRecord(providerIdentifier, replacementKnownRepositories);
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final ProviderRecord that = (ProviderRecord) obj;

		if (!knownRepositories.equals(that.knownRepositories))
		{
			return false;
		}
		if (!providerIdentifier.equals(that.providerIdentifier))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = providerIdentifier.hashCode();
		result = 31 * result + knownRepositories.hashCode();
		return result;
	}
}
