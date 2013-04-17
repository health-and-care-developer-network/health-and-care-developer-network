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

package uk.nhs.hdn.common.hazelcast.hazelcastDataReaders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.number.NhsNumber;

import java.io.DataInput;
import java.io.IOException;

public final class NhsNumberHazelcastDataReader extends AbstractHazelcastDataReader<NhsNumber>
{
	@NotNull public static final HazelcastDataReader<NhsNumber> NhsNumberHazelcastDataReaderInstance = new NhsNumberHazelcastDataReader();

	@NotNull private final DigitsHazelcastDataReader digitsHazelcastDataReader;

	private NhsNumberHazelcastDataReader()
	{
		digitsHazelcastDataReader = new DigitsHazelcastDataReader(10);
	}

	@NotNull
	@Override
	public NhsNumber readData(@NotNull final DataInput in) throws IOException
	{
		return new NhsNumber(digitsHazelcastDataReader.readData(in));
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

		final NhsNumberHazelcastDataReader that = (NhsNumberHazelcastDataReader) obj;

		if (!digitsHazelcastDataReader.equals(that.digitsHazelcastDataReader))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return digitsHazelcastDataReader.hashCode();
	}
}
