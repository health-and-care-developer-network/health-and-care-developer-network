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

package uk.nhs.hdn.dts.client.rats;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.commandLine.ShouldHaveExitedException;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotUploadException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteDataException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.MapSerialisable;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.dts.domain.DtsName;
import uk.nhs.hdn.dts.domain.identifiers.LocalIdentifier;
import uk.nhs.hdn.dts.rats.response.Response;

import static java.lang.System.out;
import static uk.nhs.hdn.common.CharsetHelper.Utf8;
import static uk.nhs.hdn.dts.client.rats.DtsRatsClient.*;
import static uk.nhs.hdn.dts.domain.DtsName.UnknownDtsName;
import static uk.nhs.hdn.dts.domain.identifiers.LocalIdentifier.UnknownLocalIdentifier;

public final class HdnDtsRatsConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String FromDtsNameOption = "from-dts-name";
	private static final String LocalIdentifierOption = "local-identifier";
	private static final String DomainNameOption = "domain-name";
	private static final String PortOption = "port";
	private static final String UseHttpsOption = "use-https";
	private static final String PathOption = "path";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(HdnDtsRatsConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(FromDtsNameOption).withRequiredArg().ofType(DtsName.class).defaultsTo(UnknownDtsName).describedAs("DTS name");
		options.accepts(LocalIdentifierOption).withRequiredArg().ofType(LocalIdentifier.class).defaultsTo(UnknownLocalIdentifier).describedAs("local identifier");

		options.accepts(DomainNameOption).withRequiredArg().ofType(String.class).defaultsTo(DefaultDomainName).describedAs("domain name to connect to");
		options.accepts(PortOption).withRequiredArg().ofType(Integer.class).defaultsTo((int) DefaultPort).describedAs("port to connect to HTTP(S) on");
		options.accepts(UseHttpsOption);
		options.accepts(PathOption).withRequiredArg().ofType(String.class).defaultsTo(DefaultPath).describedAs("path to RATS service on hostname");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException, CouldNotUploadException
	{
		@NotNull final DtsName fromDtsName = defaulted(optionSet, FromDtsNameOption);
		@NotNull final LocalIdentifier localIdentifier = defaulted(optionSet, LocalIdentifierOption);

		if (fromDtsName.isUnknown())
		{
			exitWithErrorAndHelp(FromDtsNameOption, "unknown", "must be supplied");
			throw new ShouldHaveExitedException();
		}

		if (localIdentifier.isUnknown())
		{
			exitWithErrorAndHelp(LocalIdentifierOption, "unknown", "must be supplied");
			throw new ShouldHaveExitedException();
		}

		@NotNull final String domainName = defaulted(optionSet, DomainNameOption);
		final char httpPort = portNumber(optionSet, PortOption);
		final boolean useHttps = optionSet.has(UseHttpsOption);
		@NotNull final String path = defaulted(optionSet, PathOption);

		demonstrate(fromDtsName, localIdentifier, domainName, httpPort, useHttps, path);
	}

	private static void demonstrate(final DtsName fromDtsName, final LocalIdentifier localIdentifier, final String domainName, final char httpPort, final boolean useHttps, final String path) throws CouldNotConnectHttpException, UnacceptableResponseException, CorruptResponseException, CouldNotUploadException
	{
		final DtsRatsClient dtsRatsClient = new DtsRatsClient(useHttps, domainName, httpPort, path);
		final Response[] responses = dtsRatsClient.request(fromDtsName, localIdentifier);
		outputResponses(responses);
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static void outputResponses(final Response... responses)
	{
		final SeparatedValueSerialiser separatedValueSerialiser = Response.tsvSerialiserForResponse();
		try
		{
			separatedValueSerialiser.start(out, Utf8);
			final MapSerialisable[] responses1 = responses;
			separatedValueSerialiser.writeValue(responses1);
			separatedValueSerialiser.finish();
		}
		catch (CouldNotWriteDataException | CouldNotWriteValueException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
