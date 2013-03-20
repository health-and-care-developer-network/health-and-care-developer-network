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

package uk.nhs.hdn.dbs.response.client;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.parsers.CouldNotParseException;
import uk.nhs.hdn.dbs.response.Response;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.System.in;
import static uk.nhs.hdn.dbs.response.AbstractResponseBody.tsvSerialiserForResponseBody;
import static uk.nhs.hdn.dbs.response.ResponseHeader.tsvSerialiserForResponseHeader;
import static uk.nhs.hdn.dbs.response.parsing.parsers.ResponseParser.parseResponse;

public final class HdnDbsResponseConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String ResponseOption = "response";
	private static final String IncludeHeaderOption = "include-header";

	@NonNls @NotNull private static final String ResponseDefault = "-";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(HdnDbsResponseConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(ResponseOption, "Path to response file").withRequiredArg().ofType(String.class).defaultsTo(ResponseDefault).describedAs("Path to response file, or - to use standard in");
		options.accepts(IncludeHeaderOption, "Include response header").requiredIf(ResponseOption);
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws IOException, CouldNotParseException
	{
		final String path = defaulted(optionSet, ResponseOption);
		final boolean includeHeader = optionSet.has(IncludeHeaderOption);

		final InputStream inputStream;
		if (path.equals(ResponseDefault))
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

		final Response response = parseResponse(inputStream);

		if (includeHeader)
		{
			tsvSerialiserForResponseHeader(true).printValuesOnStandardOut(response.responseHeader);
		}
		tsvSerialiserForResponseBody(true).printValuesOnStandardOut(response.responseBodies);
	}
}
