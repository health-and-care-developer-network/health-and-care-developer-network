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

import java.util.*;

public final class IndexTableRow<N extends Normalisable> implements PsuedonymisedValues<N>
{
	private final int rowsEstimate;
	@NotNull private final Map<Pseudonymiser<N>, Set<PsuedonymisedValue>> previouslyPsuedonymisedValues;
	@NotNull private final HashMap<Pseudonymiser<N>, PsuedonymisedValue> row;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public IndexTableRow(final int rowsEstimate, final int rowSizeEstimate, @NotNull final Map<Pseudonymiser<N>, Set<PsuedonymisedValue>> previouslyPsuedonymisedValues)
	{
		this.rowsEstimate = rowsEstimate;
		this.previouslyPsuedonymisedValues = previouslyPsuedonymisedValues;
		row = new HashMap<>(rowSizeEstimate);
	}

	public boolean has(@NotNull final Pseudonymiser<N> pseudonymiser)
	{
		return row.containsKey(pseudonymiser);
	}

	public void add(@NotNull final Pseudonymiser<N> pseudonymiser, @NotNull final PsuedonymisedValue psuedonymisedValue) throws DuplicatePsuedonymisedValueException
	{
		if (row.put(pseudonymiser, psuedonymisedValue) != null)
		{
			throw new IllegalArgumentException("Already add this value for this pseudonymiser");
		}

		if (previousValues(pseudonymiser).add(psuedonymisedValue))
		{
			return;
		}
		throw new DuplicatePsuedonymisedValueException();
	}

	@Nullable
	public PsuedonymisedValue get(@NotNull final Pseudonymiser<N> pseudonymiser)
	{
		return row.get(pseudonymiser);
	}

	@Override
	public void iterate(@NotNull final PsuedonymisedValueUser<N> psuedonymisedValueUser)
	{
		for (final Map.Entry<Pseudonymiser<N>, PsuedonymisedValue> entry : row.entrySet())
		{
			final Pseudonymiser<N> key = entry.getKey();
			final PsuedonymisedValue psuedonymisedValue = entry.getValue();
			psuedonymisedValueUser.use(key, psuedonymisedValue);
		}
	}

	@NotNull
	private Collection<PsuedonymisedValue> previousValues(@NotNull final Pseudonymiser<N> pseudonymiser)
	{
		@Nullable final Set<PsuedonymisedValue> psuedonymisedValues = previouslyPsuedonymisedValues.get(pseudonymiser);
		if (psuedonymisedValues != null)
		{
			return psuedonymisedValues;
		}
		final Set<PsuedonymisedValue> newPsuedonymisedValues = new HashSet<>(rowsEstimate);
		previouslyPsuedonymisedValues.put(pseudonymiser, newPsuedonymisedValues);
		return newPsuedonymisedValues;
	}
}
