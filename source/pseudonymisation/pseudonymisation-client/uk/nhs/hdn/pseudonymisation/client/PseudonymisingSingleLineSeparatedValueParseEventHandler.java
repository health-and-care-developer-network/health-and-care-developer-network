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
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.naming.Normalisable;
import uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.CouldNotParseFieldException;
import uk.nhs.hdn.common.parsers.separatedValueParsers.separatedValuesParseEventHandlers.AbstractSingleLineSeparatedValueParseEventHandler;
import uk.nhs.hdn.pseudonymisation.IndexTable;
import uk.nhs.hdn.pseudonymisation.pseudonymisers.Pseudonymiser;

import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;

public final class PseudonymisingSingleLineSeparatedValueParseEventHandler<N extends Normalisable> extends AbstractSingleLineSeparatedValueParseEventHandler<PseudonymisingSingleLineSeparatedValueParseEventHandler.PseudonymisingSingleLineSeparatedValueParseEventHandlerState>
{
	@NotNull private final DataKind dataKind;
	@NotNull private final Pseudonymiser<N>[] pseudonymisers;
	@NotNull private final IndexTable<N> indexTable;

	@SuppressWarnings("unchecked")
	public PseudonymisingSingleLineSeparatedValueParseEventHandler(@NotNull final DataKind dataKind, @NotNull final IndexTable<N> indexTable, @NotNull final Pseudonymiser<N>... pseudonymisers)
	{
		this.dataKind = dataKind;
		this.indexTable = indexTable;

		this.pseudonymisers = copyOf(pseudonymisers);
	}

	@NotNull
	@SuppressWarnings("NoopMethodInAbstractClass")
	@Override
	public PseudonymisingSingleLineSeparatedValueParseEventHandlerState start(@MillisecondsSince1970 final long lastModified)
	{
		return new PseudonymisingSingleLineSeparatedValueParseEventHandlerState();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void field(@NotNull final PseudonymisingSingleLineSeparatedValueParseEventHandlerState state, @NotNull final String value) throws CouldNotParseFieldException
	{
		if (state.isExpectingEndOfLine())
		{
			throw new CouldNotParseFieldException(1, value, "too many fields");
		}
		state.setExpectingEndOfLine(true);

		@Nullable final N valueToPseudonymise;
		try
		{
			valueToPseudonymise = (N) dataKind.parse(value);
		}
		catch (RuntimeException e)
		{
			throw new CouldNotParseFieldException(0, value, e);
		}

		for (final Pseudonymiser<N> pseudonymiser : pseudonymisers)
		{
			pseudonymiser.pseudonymise(valueToPseudonymise, indexTable);
		}
	}

	@Override
	public void endOfLine(@NotNull final PseudonymisingSingleLineSeparatedValueParseEventHandlerState state)
	{
		state.setExpectingEndOfLine(false);
	}

	public static final class PseudonymisingSingleLineSeparatedValueParseEventHandlerState
	{
		private boolean expectingEndOfLine = false;

		public boolean isExpectingEndOfLine()
		{
			return expectingEndOfLine;
		}

		public void setExpectingEndOfLine(final boolean expectingEndOfLine)
		{
			this.expectingEndOfLine = expectingEndOfLine;
		}
	}
}
