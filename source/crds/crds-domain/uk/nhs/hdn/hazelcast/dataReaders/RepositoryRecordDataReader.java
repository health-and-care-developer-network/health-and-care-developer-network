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

package uk.nhs.hdn.hazelcast.dataReaders;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.domain.RepositoryEvent;
import uk.nhs.hdn.crds.domain.RepositoryRecord;
import uk.nhs.hdn.common.hazelcast.HazelcastAwareLinkedHashSet;
import uk.nhs.hdn.common.hazelcast.DataReader;

import java.io.DataInput;
import java.io.IOException;

import static uk.nhs.hdn.hazelcast.dataReaders.RepositoryEventDataReader.RepositoryEventDataReaderInstance;
import static uk.nhs.hdn.hazelcast.dataReaders.identifierDataReaders.RepositoryIdentifierDataReader.RepositoryIdentifierDataReaderInstance;

public final class RepositoryRecordDataReader implements DataReader<RepositoryRecord>
{
	@NotNull public static final DataReader<RepositoryRecord> RepositoryRecordDataReaderInstance = new RepositoryRecordDataReader();

	private RepositoryRecordDataReader()
	{
	}

	@NotNull
	@Override
	public RepositoryRecord readData(@NotNull final DataInput in) throws IOException
	{
		return new RepositoryRecord(RepositoryIdentifierDataReaderInstance.readData(in), HazelcastAwareLinkedHashSet.<RepositoryEvent>readData(in, RepositoryEventDataReaderInstance));
	}

}
