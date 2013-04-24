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
import uk.nhs.hdn.common.hazelcast.collections.HazelcastAwareLinkedHashMap;
import uk.nhs.hdn.common.hazelcast.hazelcastDataReaders.HazelcastDataReader;
import uk.nhs.hdn.crds.registry.domain.SimplePatientRecord;
import uk.nhs.hdn.number.NhsNumber;

import java.io.DataInput;
import java.io.IOException;

import static uk.nhs.hdn.common.hazelcast.hazelcastDataReaders.NhsNumberHazelcastDataReader.NhsNumberHazelcastDataReaderInstance;
import static uk.nhs.hdn.crds.registry.server.hazelcast.hazelcastDataReaders.ProviderRecordHazelcastDataReader.ProviderRecordHazelcastDataReaderInstance;
import static uk.nhs.hdn.crds.registry.server.hazelcast.hazelcastDataReaders.identifierDataReaders.ProviderIdentifierHazelcastDataReader.ProviderIdentifierDataReaderInstance;

public final class SimplePatientRecordHazelcastDataReader implements HazelcastDataReader<SimplePatientRecord>
{
	@NotNull public static final HazelcastDataReader<SimplePatientRecord> SimplePatientRecordHazelcastDataReaderInstance = new SimplePatientRecordHazelcastDataReader();

	private SimplePatientRecordHazelcastDataReader()
	{
	}

	@NotNull
	@Override
	public SimplePatientRecord readData(@NotNull final DataInput in) throws IOException
	{
		final NhsNumber nhsNumber = NhsNumberHazelcastDataReaderInstance.readData(in);

		return new SimplePatientRecord(nhsNumber, in.readLong(), HazelcastAwareLinkedHashMap.readData(in, ProviderIdentifierDataReaderInstance, ProviderRecordHazelcastDataReaderInstance));

	}
}
