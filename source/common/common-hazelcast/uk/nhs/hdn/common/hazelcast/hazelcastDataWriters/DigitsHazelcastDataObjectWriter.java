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

package uk.nhs.hdn.common.hazelcast.hazelcastDataWriters;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.digits.DigitList;

import java.io.DataOutput;
import java.io.IOException;

public final class DigitsHazelcastDataObjectWriter
{
	private DigitsHazelcastDataObjectWriter()
	{
	}

	public static void writeData(@NotNull final DataOutput out, @NotNull final DigitList digits) throws IOException
	{
		final int size = digits.size();
		for (int index = 0; index < size; index++)
		{
			out.writeByte(digits.digitAt(index).digit());
		}
	}
}
