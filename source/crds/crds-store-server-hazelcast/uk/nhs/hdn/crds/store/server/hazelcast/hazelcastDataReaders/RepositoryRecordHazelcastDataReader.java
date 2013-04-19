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

package uk.nhs.hdn.crds.store.server.hazelcast.hazelcastDataReaders;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashSet;
import uk.nhs.hdn.common.hazelcast.hazelcastDataReaders.AbstractHazelcastDataReader;
import uk.nhs.hdn.common.hazelcast.hazelcastDataReaders.HazelcastDataReader;
import uk.nhs.hdn.crds.store.domain.RepositoryEvent;
import uk.nhs.hdn.crds.store.domain.RepositoryRecord;

import java.io.DataInput;
import java.io.IOException;

import static uk.nhs.hdn.crds.store.server.hazelcast.hazelcastDataReaders.RepositoryEventHazelcastDataReader.RepositoryEventHazelcastDataReaderInstance;
import static uk.nhs.hdn.crds.store.server.hazelcast.hazelcastDataReaders.identifierDataReaders.RepositoryIdentifierHazelcastDataReader.RepositoryIdentifierDataReaderInstance;

public final class RepositoryRecordHazelcastDataReader extends AbstractHazelcastDataReader<RepositoryRecord>
{
	@NotNull public static final HazelcastDataReader<RepositoryRecord> RepositoryRecordHazelcastDataReaderInstance = new RepositoryRecordHazelcastDataReader();

	private RepositoryRecordHazelcastDataReader()
	{
	}

	@NotNull
	@Override
	public RepositoryRecord readData(@NotNull final DataInput in) throws IOException
	{
		return new RepositoryRecord(RepositoryIdentifierDataReaderInstance.readData(in), HazelcastAwareLinkedHashSet.<RepositoryEvent>readData(in, RepositoryEventHazelcastDataReaderInstance));
	}

}
