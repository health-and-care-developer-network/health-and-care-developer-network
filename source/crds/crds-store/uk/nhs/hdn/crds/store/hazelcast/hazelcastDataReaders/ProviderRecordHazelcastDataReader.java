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

package uk.nhs.hdn.crds.store.hazelcast.hazelcastDataReaders;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.store.domain.ProviderRecord;
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashMap;
import uk.nhs.hdn.common.hazelcast.hazelcastDataReaders.HazelcastDataReader;

import java.io.DataInput;
import java.io.IOException;

import static uk.nhs.hdn.crds.store.hazelcast.hazelcastDataReaders.identifierDataReaders.ProviderIdentifierHazelcastDataReader.ProviderIdentifierDataReaderInstance;
import static uk.nhs.hdn.crds.store.hazelcast.hazelcastDataReaders.identifierDataReaders.RepositoryIdentifierHazelcastDataReader.RepositoryIdentifierDataReaderInstance;

public final class ProviderRecordHazelcastDataReader implements HazelcastDataReader<ProviderRecord>
{
	@NotNull public static final HazelcastDataReader<ProviderRecord> ProviderRecordHazelcastDataReaderInstance = new ProviderRecordHazelcastDataReader();

	private ProviderRecordHazelcastDataReader()
	{
	}

	@NotNull
	@Override
	public ProviderRecord readData(@NotNull final DataInput in) throws IOException
	{
		return new ProviderRecord(ProviderIdentifierDataReaderInstance.readData(in), HazelcastAwareLinkedHashMap.readData(in, RepositoryIdentifierDataReaderInstance, RepositoryRecordHazelcastDataReader.RepositoryRecordHazelcastDataReaderInstance));
	}

}
