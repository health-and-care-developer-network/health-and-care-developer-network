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

import java.util.ArrayList;
import java.util.List;

public final class VariableListSeparatedValuesLine extends AbstractSeparatedValuesLine
{
	@NotNull
	private final List<String> fields;

	public VariableListSeparatedValuesLine()
	{
		fields = new ArrayList<>(10);
	}

	@SuppressWarnings("SimplifiableIfStatement")
	@Override
	protected boolean isFieldAlreadyRecorded(final int index)
	{
		if (index >= size())
		{
			return false;
		}
		return fields.get(index) != null;
	}

	@Override
	protected void store(final int index, @Nullable final String rawValue)
	{
		final int size = size();
		if (index < size)
		{
			fields.add(index, rawValue);
			return;
		}
		if (index == size)
		{
			fields.add(rawValue);
			return;
		}
		final int emptyElementsToAdd = index - size;
		for(int count = 0; count < emptyElementsToAdd; count++)
		{
			fields.add(null);
		}
		fields.add(rawValue);
	}

	@Override
	@Nullable
	protected String load(final int index)
	{
		return fields.get(index);
	}

	@Override
	protected int size()
	{
		return fields.size();
	}
}
