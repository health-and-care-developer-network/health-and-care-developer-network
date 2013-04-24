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

package uk.nhs.hdn.crds.registry.server.hazelcast.hazelcastDataReaders;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.hazelcast.hazelcastDataReaders.AbstractHazelcastDataReader;
import uk.nhs.hdn.common.hazelcast.hazelcastDataReaders.HazelcastDataReader;
import uk.nhs.hdn.crds.registry.domain.RepositoryEvent;

import java.io.DataInput;
import java.io.IOException;

import static uk.nhs.hdn.crds.registry.domain.RepositoryEventKind.repositoryEventKind;
import static uk.nhs.hdn.crds.registry.server.hazelcast.hazelcastDataReaders.identifierDataReaders.RepositoryEventIdentifierHazelcastDataReader.RepositoryEventIdentifierDataReaderInstance;

public final class RepositoryEventHazelcastDataReader extends AbstractHazelcastDataReader<RepositoryEvent>
{
	@NotNull public static final HazelcastDataReader<RepositoryEvent> RepositoryEventHazelcastDataReaderInstance = new RepositoryEventHazelcastDataReader();

	private RepositoryEventHazelcastDataReader()
	{
	}

	@NotNull
	@Override
	public RepositoryEvent readData(@NotNull final DataInput in) throws IOException
	{
		return new RepositoryEvent(RepositoryEventIdentifierDataReaderInstance.readData(in), in.readLong(), repositoryEventKind((int) in.readByte()));
	}
}
