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

package uk.nhs.hdn.pseudonymisation.pseudonymisers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.naming.Normalisable;
import uk.nhs.hdn.pseudonymisation.DuplicatePsuedonymisedValueException;
import uk.nhs.hdn.pseudonymisation.IndexTable;
import uk.nhs.hdn.pseudonymisation.PsuedonymisedValue;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static uk.nhs.hdn.common.UUIDHelper.uuidToByteArray;

public final class UuidPseudonymiser<N extends Normalisable> extends AbstractPseudonymiser<N>
{
	@NotNull private final UUID uuidForNull;

	public UuidPseudonymiser()
	{
		super(16);
		uuidForNull = randomUUID();
	}

	@Override
	public void pseudonymise(@Nullable final N valueToPsuedonymise, @NotNull final IndexTable<N> indexTable)
	{
		if (indexTable.has(valueToPsuedonymise, this))
		{
			return;
		}

		do
		{
			@SuppressWarnings("VariableNotUsedInsideIf") final UUID sequenceValue = valueToPsuedonymise == null ? uuidForNull : randomUUID();

			final byte[] sequenceValueAsBytes = uuidToByteArray(sequenceValue);
			final PsuedonymisedValue psuedonymisedValue = new PsuedonymisedValue(sequenceValueAsBytes);
			try
			{
				indexTable.add(valueToPsuedonymise, this, psuedonymisedValue);
				return;
			}
			catch (DuplicatePsuedonymisedValueException e)
			{
				if (valueToPsuedonymise == null && sequenceValue.equals(uuidForNull))
				{
					throw new IllegalStateException("Duplicate UUID for null", e);
				}
			}
		}
		while (true);
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

		final UuidPseudonymiser<?> that = (UuidPseudonymiser<?>) obj;

		if (!uuidForNull.equals(that.uuidForNull))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return uuidForNull.hashCode();
	}
}
