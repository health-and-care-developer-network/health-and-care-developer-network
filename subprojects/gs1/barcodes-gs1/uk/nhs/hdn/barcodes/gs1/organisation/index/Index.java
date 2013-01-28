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

package uk.nhs.hdn.barcodes.gs1.organisation.index;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.barcodes.gs1.Gs1CompanyPrefixAndItem;
import uk.nhs.hdn.barcodes.gs1.organisation.Tuple;
import uk.nhs.hdn.barcodes.gs1.gs1Prefixes.Gs1Prefix;

public final class Index implements Gs1CompanyPrefixIndex
{
	private final SubIndex[] subIndices;

	public Index(@NotNull final Tuple... tuples)
	{
		subIndices = new SubIndex[1000];
		for(int index = 0; index < 1000; index++)
		{
			subIndices[index] = new SubIndex(index);
		}
		for (final Tuple tuple : tuples)
		{
			subIndex(tuple.gs1CompanyPrefix().gs1Prefix()).register(tuple);
		}
	}

	@Override
	@Nullable
	public Tuple find(@NotNull final Gs1CompanyPrefixAndItem gs1CompanyPrefixAndItem)
	{
		return subIndex(gs1CompanyPrefixAndItem.gs1Prefix()).find(gs1CompanyPrefixAndItem);
	}

	private SubIndex subIndex(final Gs1Prefix gs1Prefix)
	{
		return subIndices[gs1Prefix.to0To999()];
	}
}
