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

package uk.nhs.hdn.common.serialisers.separatedValues;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class FixedArraySeparatedValuesLine extends AbstractSeparatedValuesLine
{
	@ExcludeFromToString
	private final int length;
	@NotNull
	private final String[] fields;

	public FixedArraySeparatedValuesLine(final int length)
	{
		this.length = length;
		fields = new String[length];
	}

	@Override
	protected boolean isFieldAlreadyRecorded(final int index)
	{
		if (index >= length)
		{
			throw new IllegalArgumentException(format(ENGLISH, "index %1$s is out of range (index < %2$s)", index, length));
		}
		return fields[index] != null;
	}

	@Override
	protected void store(final int index, @Nullable final String rawValue)
	{
		fields[index] = rawValue;
	}

	@Override
	@Nullable
	protected String load(final int index)
	{
		return fields[index];
	}

	@Override
	protected int size()
	{
		return length;
	}
}
