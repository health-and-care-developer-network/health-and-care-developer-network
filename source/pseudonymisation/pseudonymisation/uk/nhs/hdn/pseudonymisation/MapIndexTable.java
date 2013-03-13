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

package uk.nhs.hdn.pseudonymisation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.naming.Normalisable;
import uk.nhs.hdn.pseudonymisation.pseudonymisers.Pseudonymiser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class MapIndexTable<N extends Normalisable> implements IndexTable<N>
{
	private final int rowsEstimate;
	private final int rowSizeEstimate;
	private final Map<N, IndexTableRow<N>> table;
	private final Map<Pseudonymiser<N>, Set<PsuedonymisedValue>> previouslyPseudonymisedValues;

	public MapIndexTable(final int rowsEstimate, final int rowSizeEstimate)
	{
		this.rowsEstimate = rowsEstimate;
		this.rowSizeEstimate = rowSizeEstimate;
		table = new HashMap<>(rowsEstimate);
		previouslyPseudonymisedValues = new HashMap<>(rowSizeEstimate);
	}

	@SuppressWarnings("SimplifiableIfStatement")
	@Override
	public boolean has(@Nullable final N valueToPsuedonymise, @NotNull final Pseudonymiser<N> pseudonymiser)
	{
		@Nullable final IndexTableRow<N> indexTableRow = table.get(valueToPsuedonymise);
		if (indexTableRow == null)
		{
			return false;
		}
		return indexTableRow.has(pseudonymiser);
	}

	@Override
	public void add(@Nullable final N valueToPsuedonymise, @NotNull final Pseudonymiser<N> pseudonymiser, @NotNull final PsuedonymisedValue psuedonymisedValue) throws DuplicatePsuedonymisedValueException
	{
		getOrCreateIndexTableRow(valueToPsuedonymise).add(pseudonymiser, psuedonymisedValue);
	}

	@NotNull
	private IndexTableRow<N> getOrCreateIndexTableRow(@Nullable final N valueToPsuedonymise)
	{
		@Nullable final IndexTableRow<N> indexTableRow = table.get(valueToPsuedonymise);
		if (indexTableRow == null)
		{
			final IndexTableRow<N> value = new IndexTableRow<>(rowsEstimate, rowSizeEstimate, previouslyPseudonymisedValues);
			table.put(valueToPsuedonymise, value);
			return value;
		}
		return indexTableRow;
	}

	@Override
	public <X extends Exception> void iterate(@NotNull final PsuedonymisedValuesUser<N, X> psuedonymisedValuesUser) throws X
	{
		for (final Map.Entry<N, IndexTableRow<N>> entry : table.entrySet())
		{
			final N valueToPsuedonymise = entry.getKey();
			final IndexTableRow<N> pseudonymisedValues = entry.getValue();

			if (psuedonymisedValuesUser.use(valueToPsuedonymise, pseudonymisedValues))
			{
				return;
			}
		}
	}
}
