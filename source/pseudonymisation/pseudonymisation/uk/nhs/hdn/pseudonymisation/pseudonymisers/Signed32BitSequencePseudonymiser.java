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
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;
import uk.nhs.hdn.pseudonymisation.DuplicatePsuedonymisedValueException;
import uk.nhs.hdn.pseudonymisation.IndexTable;
import uk.nhs.hdn.pseudonymisation.PsuedonymisedValue;

import static uk.nhs.hdn.common.IntegerHelper.signedIntegerToByteArray;

public final class Signed32BitSequencePseudonymiser<N extends Normalisable> extends AbstractPseudonymiser<N>
{
	public static final int NullAssignableValue = -1;
	public static final int DefaultInitialValue = 0;

	private final int initialValue;

	@ExcludeFromToString private int nextSequenceValue;

	public Signed32BitSequencePseudonymiser()
	{
		this(DefaultInitialValue);
	}

	public Signed32BitSequencePseudonymiser(final int initialValue)
	{
		super(4, false);
		if (initialValue == NullAssignableValue)
		{
			throw new IllegalArgumentException("Initial int value can not be that reserved for null");
		}
		this.initialValue = initialValue;
		nextSequenceValue = initialValue;
	}

	@Override
	public void pseudonymise(@Nullable final N valueToPsuedonymise, @NotNull final IndexTable<N> indexTable)
	{
		if (indexTable.has(valueToPsuedonymise, this))
		{
			return;
		}
		final int sequenceValue;
		//noinspection VariableNotUsedInsideIf
		if (valueToPsuedonymise == null)
		{
			sequenceValue = NullAssignableValue;
		}
		else
		{
			if (nextSequenceValue == NullAssignableValue)
			{
				throw new IllegalStateException("Exhausted int sequence");
			}
			sequenceValue = nextSequenceValue;
			nextSequenceValue++;
		}

		final byte[] sequenceValueAsBytes = signedIntegerToByteArray(sequenceValue);
		final PsuedonymisedValue psuedonymisedValue = new PsuedonymisedValue(sequenceValueAsBytes);
		try
		{
			indexTable.add(valueToPsuedonymise, this, psuedonymisedValue);
		}
		catch (DuplicatePsuedonymisedValueException e)
		{
			throw new IllegalStateException("Exhausted int sequence only possible if adding to existing seqeunce", e);
		}
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
		if (!super.equals(obj))
		{
			return false;
		}

		final Signed32BitSequencePseudonymiser<?> that = (Signed32BitSequencePseudonymiser<?>) obj;

		if (initialValue != that.initialValue)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + initialValue;
		return result;
	}
}
