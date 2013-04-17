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
import uk.nhs.hdn.common.digits.Digit;
import uk.nhs.hdn.common.digits.Digits;

import java.io.DataInput;
import java.io.IOException;

import static uk.nhs.hdn.common.digits.Digit.digit;

public final class DigitsHazelcastDataReader extends AbstractHazelcastDataReader<Digits>
{
	private final int numberOfDigits;

	public DigitsHazelcastDataReader(final int numberOfDigits)
	{
		this.numberOfDigits = numberOfDigits;
	}

	@NotNull
	@Override
	public Digits readData(@NotNull final DataInput in) throws IOException
	{
		final Digit[] digits = new Digit[10];
		for (int index = 0; index < numberOfDigits; index++)
		{
			digits[index] = digit((int) in.readByte());
		}
		return new Digits(false, digits);
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

		final DigitsHazelcastDataReader that = (DigitsHazelcastDataReader) obj;

		if (numberOfDigits != that.numberOfDigits)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return numberOfDigits;
	}
}
