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
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashSet;
import uk.nhs.hdn.common.hazelcast.hazelcastDataReaders.AbstractHazelcastDataReader;
import uk.nhs.hdn.common.hazelcast.hazelcastDataReaders.HazelcastDataReader;
import uk.nhs.hdn.crds.registry.domain.StuffEvent;
import uk.nhs.hdn.crds.registry.domain.StuffRecord;

import java.io.DataInput;
import java.io.IOException;

import static uk.nhs.hdn.crds.registry.server.hazelcast.hazelcastDataReaders.StuffEventHazelcastDataReader.StuffEventHazelcastDataReaderInstance;
import static uk.nhs.hdn.crds.registry.server.hazelcast.hazelcastDataReaders.identifierDataReaders.StuffIdentifierHazelcastDataReader.StuffIdentifierDataReaderInstance;

public final class StuffRecordHazelcastDataReader extends AbstractHazelcastDataReader<StuffRecord>
{
	@NotNull public static final HazelcastDataReader<StuffRecord> StuffRecordHazelcastDataReaderInstance = new StuffRecordHazelcastDataReader();

	private StuffRecordHazelcastDataReader()
	{
	}

	@NotNull
	@Override
	public StuffRecord readData(@NotNull final DataInput in) throws IOException
	{
		return new StuffRecord(StuffIdentifierDataReaderInstance.readData(in), HazelcastAwareLinkedHashSet.<StuffEvent>readData(in, StuffEventHazelcastDataReaderInstance));
	}

}
