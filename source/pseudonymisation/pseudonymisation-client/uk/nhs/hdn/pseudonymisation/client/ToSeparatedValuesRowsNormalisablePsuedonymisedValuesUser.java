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

package uk.nhs.hdn.pseudonymisation.client;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.naming.Normalisable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.CouldNotEncodeDataException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteDataException;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValuesLine;
import uk.nhs.hdn.common.serialisers.separatedValues.VariableListSeparatedValuesLine;
import uk.nhs.hdn.common.serialisers.separatedValues.fieldEscapers.FieldEscaper;
import uk.nhs.hdn.pseudonymisation.PsuedonymisedValue;
import uk.nhs.hdn.pseudonymisation.PsuedonymisedValues;
import uk.nhs.hdn.pseudonymisation.PsuedonymisedValuesUser;
import uk.nhs.hdn.pseudonymisation.pseudonymisers.Pseudonymiser;

import java.io.Writer;

import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;

public final class ToSeparatedValuesRowsNormalisablePsuedonymisedValuesUser<N extends Normalisable> extends AbstractToString implements PsuedonymisedValuesUser<N, CouldNotWriteDataException>
{
	@NotNull private final Writer writer;
	@NotNull private final FieldEscaper fieldEscaper;
	@NotNull private final Pseudonymiser<N>[] psuedonymisersInOutputOrder;

	@SafeVarargs
	public ToSeparatedValuesRowsNormalisablePsuedonymisedValuesUser(@NotNull final Writer writer, @NotNull final FieldEscaper fieldEscaper, @NotNull final Pseudonymiser<N>... psuedonymisersInOutputOrder)
	{
		this.writer = writer;
		this.fieldEscaper = fieldEscaper;
		this.psuedonymisersInOutputOrder = copyOf(psuedonymisersInOutputOrder);
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public boolean use(@NotNull final N valueToPsuedonymise, @NotNull final PsuedonymisedValues<N> pseudonymisedValues) throws CouldNotWriteDataException
	{
		final SeparatedValuesLine separatedValuesLine = new VariableListSeparatedValuesLine();

		int index = 0;
		for (final Pseudonymiser<N> pseudonymiser : psuedonymisersInOutputOrder)
		{
			@SuppressWarnings("ConstantConditions") @NotNull final PsuedonymisedValue psuedonymisedValue = pseudonymisedValues.get(pseudonymiser);

			index = record(separatedValuesLine, index, psuedonymisedValue.valueAsBase16Hexadecimal());
			if (psuedonymisedValue.hasSalt())
			{
				index = record(separatedValuesLine, index, psuedonymisedValue.saltAsBase16Hexadecimal());
			}
		}

		try
		{
			separatedValuesLine.writeLine(writer, fieldEscaper);
		}
		catch (CouldNotEncodeDataException e)
		{
			throw new CouldNotWriteDataException(e);
		}

		return false;
	}

	private static int record(final SeparatedValuesLine separatedValuesLine, final int index, final char... hexadecimalCharacters)
	{
		separatedValuesLine.recordValue(index, toHexadecimal(hexadecimalCharacters));
		return index + 1;
	}

	@NonNls
	private static String toHexadecimal(@NotNull final char... hexadecimalCharacters)
	{
		return "0x" + new String(hexadecimalCharacters);
	}
}
