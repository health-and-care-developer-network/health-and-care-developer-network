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

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.naming.Normalisable;
import uk.nhs.hdn.common.serialisers.CouldNotEncodeDataException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteDataException;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValuesLine;
import uk.nhs.hdn.common.serialisers.separatedValues.VariableListSeparatedValuesLine;
import uk.nhs.hdn.common.serialisers.separatedValues.fieldEscapers.FieldEscaper;
import uk.nhs.hdn.pseudonymisation.IndexTable;
import uk.nhs.hdn.pseudonymisation.pseudonymisers.Pseudonymiser;

import java.io.*;

public final class WriteOutSeparatedValues
{
	@NotNull private final FieldEscaper fieldEscaper;
	@NotNull private final DataKind dataKind;
	@NotNull private final OutputStream outputStream;

	public WriteOutSeparatedValues(@NotNull final FieldEscaper fieldEscaper, @NotNull final OutputStream outputStream, @NotNull final DataKind dataKind)
	{
		this.outputStream = outputStream;
		this.fieldEscaper = fieldEscaper;
		this.dataKind = dataKind;
	}

	@SuppressWarnings("FinalMethodInFinalClass") // SafeVargs
	@SafeVarargs
	public final <N extends Normalisable> void writeOut(final IndexTable<N> indexTable, @NotNull final Pseudonymiser<N>... pseudonymisers) throws CouldNotWriteDataException
	{
		@SuppressWarnings("UseOfSystemOutOrSystemErr") final Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
		try
		{
			writeHeaderRow(writer, pseudonymisers);
			writeDataRows(indexTable, writer, pseudonymisers);
		}
		finally
		{
			try
			{
				writer.close();
			}
			catch (IOException ignored)
			{
			}
		}
	}

	@SuppressWarnings({"FeatureEnvy", "FinalMethodInFinalClass"})
	@SafeVarargs
	private final <N extends Normalisable> void writeHeaderRow(final Writer writer, final Pseudonymiser<N>... pseudonymisers) throws CouldNotWriteDataException
	{
		final SeparatedValuesLine separatedValuesLine = new VariableListSeparatedValuesLine();
		separatedValuesLine.recordValue(0, dataKind.name());
		int index = 1;
		for (final Pseudonymiser<N> pseudonymiser : pseudonymisers)
		{
			final String fieldName = pseudonymiser.toString();
			separatedValuesLine.recordValue(index, fieldName);
			index++;
			if (pseudonymiser.hasSalt())
			{
				separatedValuesLine.recordValue(index, fieldName + " Salt");
				index++;
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
	}

	@SuppressWarnings("FinalMethodInFinalClass")
	@SafeVarargs
	private final <N extends Normalisable> void writeDataRows(final IndexTable<N> indexTable, final Writer writer, final Pseudonymiser<N>... pseudonymisers) throws CouldNotWriteDataException
	{
		indexTable.iterate(new ToSeparatedValuesRowsNormalisablePsuedonymisedValuesUser<>(writer, fieldEscaper, pseudonymisers));
	}
}
