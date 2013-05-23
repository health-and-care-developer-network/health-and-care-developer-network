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

package uk.nhs.hdn.crds.registry.client.metadata;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.commandLine.ShouldHaveExitedException;
import uk.nhs.hdn.common.http.client.ApiMethod;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.http.client.json.JsonGenericGetApi;
import uk.nhs.hdn.common.serialisers.Serialisable;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.crds.registry.client.ConcreteCrdsRestApi;
import uk.nhs.hdn.crds.registry.client.CrdsRestApi;
import uk.nhs.hdn.crds.registry.domain.identifiers.Identifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;
import uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor;

import java.util.UUID;

import static java.lang.Boolean.TRUE;
import static uk.nhs.hdn.crds.registry.domain.metadata.IdentifierConstructor.*;

public final class MetadataRecordConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String DomainNameOption = "domain-name";
	private static final String UseHttpsOption = "use-https";
	private static final String PortOption = "port";

	private static final char DefaultPort = (char) 80;
	public static final char HttpsPortDefault = (char) 443;

	@NotNull private static final IdentifierConstructor[] HaveMetadataIdentifierConstructors =
	{
			provider,
			repository,
			stuff
	};

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(MetadataRecordConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		oneOfEnumAsOptionWithRequiredArgument(options, HaveMetadataIdentifierConstructors, String.class);

		options.accepts(UseHttpsOption, "Use HTTPS").withOptionalArg().ofType(Boolean.class).defaultsTo(TRUE);
		options.accepts(DomainNameOption, "FQDN of server").withRequiredArg().ofType(String.class).defaultsTo(ConcreteCrdsRestApi.DefaultDomainName).describedAs("domain name to connect to");
		options.accepts(PortOption, "Port to connect on").withRequiredArg().ofType(Integer.class).defaultsTo((int) DefaultPort).describedAs("port to connect to HTTP(S) on");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		@NotNull final IdentifierConstructor identifierConstructor = enumOptionChosen(optionSet, HaveMetadataIdentifierConstructors);
		@NotNull final String key = required(optionSet, convertUnderscoresInEnumValueAsTheyAreNotValidForLongOptions(true, identifierConstructor));
		final UUID uuid;
		try
		{
			uuid = UUID.fromString(key);
		}
		catch (RuntimeException ignored)
		{
			exitWithErrorAndHelp(identifierConstructor.name(), key, "be a valid UUID");
			throw new ShouldHaveExitedException();
		}
		final Identifier identifier = identifierConstructor.construct(uuid);

		@NotNull final String domainName = defaulted(optionSet, DomainNameOption);

		final boolean useHttps = optionSet.has(UseHttpsOption) && (boolean) defaulted(optionSet, UseHttpsOption);

		final char httpPort;
		if (optionSet.hasArgument(PortOption))
		{
			httpPort = portNumber(optionSet, PortOption);
		}
		else
		{
			if (useHttps)
			{
				httpPort = HttpsPortDefault;
			}
			else
			{
				httpPort = portNumber(optionSet, PortOption);
			}
		}

		execute(identifierConstructor, identifier, domainName, useHttps, httpPort);
	}

	@SuppressWarnings({"UseOfSystemOutOrSystemErr", "FeatureEnvy", "OverlyCoupledMethod"})
	private static void execute(final IdentifierConstructor identifierConstructor, final Identifier identifier, final String domainName, final boolean useHttps, final char httpPort) throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException
	{
		final CrdsRestApi crdsRestApi = new ConcreteCrdsRestApi(new JsonGenericGetApi(useHttps, domainName, httpPort, ""));
		final ApiMethod<?> apiMethod;
		final SeparatedValueSerialiser tsvSerialiser = identifierConstructor.tsvSerialiser();
		switch(identifierConstructor)
		{
			case provider:
				apiMethod = crdsRestApi.providerMetadataRecord((ProviderIdentifier) identifier);
				break;

			case repository:
				apiMethod = crdsRestApi.repositoryMetadataRecord((RepositoryIdentifier) identifier);
				break;

			case stuff:
				apiMethod = crdsRestApi.stuffMetadataRecord((StuffIdentifier) identifier);
				break;

			case stuff_event:
				throw new IllegalStateException("Should not be possible");

			default:
				throw new IllegalStateException("Illogical");
		}

		tsvSerialiser.printValuesOnStandardOut((Serialisable) apiMethod.execute());
	}
}
