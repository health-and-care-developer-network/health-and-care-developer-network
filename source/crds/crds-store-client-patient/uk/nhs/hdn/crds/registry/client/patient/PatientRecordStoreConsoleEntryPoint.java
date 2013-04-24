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

package uk.nhs.hdn.crds.registry.client.patient;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.http.client.ApiMethod;
import uk.nhs.hdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hdn.common.http.client.json.JsonGenericGetApi;
import uk.nhs.hdn.crds.registry.client.ConcreteCrdsRestApi;
import uk.nhs.hdn.crds.registry.client.CrdsRestApi;
import uk.nhs.hdn.crds.registry.domain.SimplePatientRecord;
import uk.nhs.hdn.number.NhsNumber;

import static java.lang.Boolean.TRUE;
import static java.lang.System.out;

public final class PatientRecordStoreConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String PatientOption = "patient";
	private static final String DomainNameOption = "domain-name";
	private static final String UseHttpsOption = "use-https";
	private static final String PortOption = "port";

	@NotNull @NonNls private static final String DefaultDomainName = "services.developer.nhs.uk";
	private static final char DefaultPort = (char) 80;
	public static final char HttpsPortDefault = (char) 443;

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		execute(PatientRecordStoreConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(PatientOption, "NHS Number of Patient").withRequiredArg().ofType(NhsNumber.class);
		options.accepts(UseHttpsOption, "Use HTTPS").withOptionalArg().ofType(Boolean.class).defaultsTo(TRUE);
		options.accepts(DomainNameOption, "FQDN of server").withRequiredArg().ofType(String.class).defaultsTo(DefaultDomainName).describedAs("domain name to connect to");
		options.accepts(PortOption, "Port to connect on").withRequiredArg().ofType(Integer.class).defaultsTo((int) DefaultPort).describedAs("port to connect to HTTP(S) on");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws CouldNotConnectHttpException, CorruptResponseException, UnacceptableResponseException
	{
		final NhsNumber patient = required(optionSet, PatientOption);

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

		execute(patient, domainName, useHttps, httpPort);
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	private static void execute(final NhsNumber patient, final String domainName, final boolean useHttps, final char httpPort) throws UnacceptableResponseException, CorruptResponseException, CouldNotConnectHttpException
	{
		final CrdsRestApi crdsRestApi = new ConcreteCrdsRestApi(new JsonGenericGetApi(useHttps, domainName, httpPort, ""));
		final ApiMethod<SimplePatientRecord> simplePatientRecordApiMethod = crdsRestApi.simplePatientRecord(patient);
		final SimplePatientRecord execute = simplePatientRecordApiMethod.execute();
		out.println(execute.toString());
	}
}
