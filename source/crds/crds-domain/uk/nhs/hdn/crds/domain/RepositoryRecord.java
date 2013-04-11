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
import uk.nhs.hdn.crds.domain.hazelcast.ConvenientLinkedHashMap;
import uk.nhs.hdn.crds.domain.hazelcast.ConvenientLinkedHashSet;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.crds.domain.hazelcast.DataWriter;

import java.io.DataOutput;
import java.io.IOException;

import static uk.nhs.hdn.crds.domain.RepositoryEvent.initialRepositoryEvents;

public final class RepositoryRecord extends AbstractToString implements DataWriter
{
	@NotNull
	public static ConvenientLinkedHashMap<RepositoryIdentifier, RepositoryRecord> initialRepositoryRecords(@NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final RepositoryEvent repositoryEvent)
	{
		return new ConvenientLinkedHashMap<>(repositoryIdentifier, repositoryRecord(repositoryIdentifier, repositoryEvent));
	}

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static RepositoryRecord repositoryRecord(@NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final RepositoryEvent repositoryEvent)
	{
		return new RepositoryRecord(repositoryIdentifier, initialRepositoryEvents(repositoryEvent));
	}

	@NotNull private final RepositoryIdentifier repositoryIdentifier;
	@NotNull private final ConvenientLinkedHashSet<RepositoryEvent> repositoryEvents;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public RepositoryRecord(@NotNull final RepositoryIdentifier repositoryIdentifier, @NotNull final ConvenientLinkedHashSet<RepositoryEvent> repositoryEvents)
	{
		this.repositoryIdentifier = repositoryIdentifier;
		this.repositoryEvents = repositoryEvents;
	}

	@Override
	public void writeData(@NotNull final DataOutput out) throws IOException
	{
		repositoryIdentifier.writeData(out);
		repositoryEvents.writeData(out);
	}

	@NotNull
	public RepositoryRecord addRepositoryEvent(@NotNull final RepositoryEvent repositoryEvent)
	{
		return new RepositoryRecord(repositoryIdentifier, new ConvenientLinkedHashSet<>(repositoryEvents, repositoryEvent));
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

		final RepositoryRecord that = (RepositoryRecord) obj;

		if (!repositoryEvents.equals(that.repositoryEvents))
		{
			return false;
		}
		if (!repositoryIdentifier.equals(that.repositoryIdentifier))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = repositoryIdentifier.hashCode();
		result = 31 * result + repositoryEvents.hashCode();
		return result;
	}
}
