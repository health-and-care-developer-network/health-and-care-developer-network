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

package uk.nhs.hdn.hazelcast.dataReaders.identifierDataReaders;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.crds.domain.AbstractIdentifier;
import uk.nhs.hdn.common.hazelcast.DataReader;

import java.io.DataInput;
import java.io.IOException;
import java.util.UUID;

import static uk.nhs.hdn.common.hazelcast.UuidDataReader.UuidDataReaderInstance;

public abstract class AbstractIdentifierDataReader<T extends AbstractIdentifier> implements DataReader<T>
{
	@NotNull
	@Override
	public final T readData(@NotNull final DataInput in) throws IOException
	{
		return newIdentifier(UuidDataReaderInstance.readData(in));
	}

	@NotNull
	protected abstract T newIdentifier(@NotNull final UUID uuid);
}
