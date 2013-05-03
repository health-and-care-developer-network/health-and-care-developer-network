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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.naming.Normalisable;
import uk.nhs.hdn.common.parsers.CouldNotParseException;
import uk.nhs.hdn.common.parsers.separatedValueParsers.TabSeparatedValueParser;
import uk.nhs.hdn.common.serialisers.CouldNotWriteDataException;
import uk.nhs.hdn.pseudonymisation.IndexTable;
import uk.nhs.hdn.pseudonymisation.MapIndexTable;
import uk.nhs.hdn.pseudonymisation.pseudonymisers.Pseudonymiser;

import java.io.*;
import java.util.List;

import static java.lang.System.*;
import static uk.nhs.hdn.common.CharsetHelper.Utf8;
import static uk.nhs.hdn.common.serialisers.separatedValues.fieldEscapers.SanitisingTabSeparatedFieldEscaper.SanitisingTabSeparatedFieldEscaperInstance;
import static uk.nhs.hdn.pseudonymisation.client.DataKind.nhs_number;
import static uk.nhs.hdn.pseudonymisation.client.PseudonymisationAction.values;

public final class HdnPseudonymisationClientConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String FileOption = "file";
	private static final String DataKindOption = "data-kind";
	private static final String PseudonymsiersOption = "pseudonymsiers";

	@NonNls @NotNull private static final String FileDefault = "-";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(HdnPseudonymisationClientConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(DataKindOption, enumDescription(DataKind.values(), false, false)).withRequiredArg().ofType(DataKind.class).defaultsTo(nhs_number).describedAs("Post Code or NHS Number data is being pseudeonymsied");
		options.accepts(PseudonymsiersOption, enumDescription(values(), false, false)).withRequiredArg().ofType(PseudonymisationAction.class).defaultsTo(values()).describedAs("one or more pseudonymisation operations");
		options.accepts(FileOption, "Path to file of data-kind").withRequiredArg().ofType(String.class).defaultsTo(FileDefault).describedAs("Path to data-kind file (one per line, LF separated), or - to use standard in");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws IOException, CouldNotParseException, CouldNotWriteDataException
	{
		final DataKind dataKind = defaulted(optionSet, DataKindOption);
		final List<PseudonymisationAction> pseudonymisationActions = atLeastOneOfDefaulted(optionSet, PseudonymsiersOption);

		final String path = defaulted(optionSet, FileOption);

		final InputStream inputStream;
		if (path.equals(FileDefault))
		{
			if (in.available() == 0)
			{
				throw new IllegalStateException("It appears that stdin is not provided");
			}
			inputStream = in;
		}
		else
		{
			// parseResponse closes
			//noinspection IOResourceOpenedButNotSafelyClosed
			inputStream = new FileInputStream(path);
		}

		final int size = pseudonymisationActions.size();
		@SuppressWarnings("unchecked") final Pseudonymiser<Normalisable>[] pseudonymisers = new Pseudonymiser[size];
		for(int index = 0; index < size; index++)
		{
			final PseudonymisationAction pseudonymisationAction = pseudonymisationActions.get(index);
			pseudonymisers[index] = pseudonymisationAction.pseudonymiser();
		}

		final IndexTable<Normalisable> indexTable = new MapIndexTable<>(1000, size);

		@SuppressWarnings("unchecked") final TabSeparatedValueParser<PseudonymisingSingleLineSeparatedValueParseEventHandler.PseudonymisingSingleLineSeparatedValueParseEventHandlerState> tabSeparatedValueParser = new TabSeparatedValueParser<>(new PseudonymisingSingleLineSeparatedValueParseEventHandler<>(dataKind, indexTable, pseudonymisers));
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Utf8));
		try
		{
			tabSeparatedValueParser.parse(bufferedReader, currentTimeMillis());
		}
		finally
		{
			try
			{
				inputStream.close();
			}
			catch (IOException ignored)
			{
			}
		}

		//noinspection UseOfSystemOutOrSystemErr
		new WriteOutSeparatedValues(SanitisingTabSeparatedFieldEscaperInstance, out, dataKind).writeOut(indexTable, pseudonymisers);
	}

}
