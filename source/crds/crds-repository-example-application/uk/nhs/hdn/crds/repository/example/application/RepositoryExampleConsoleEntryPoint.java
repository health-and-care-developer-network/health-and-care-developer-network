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

package uk.nhs.hdn.crds.repository.example.application;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.sql.postgresql.PostgresqlConnectionProvider;
import uk.nhs.hdn.crds.repository.senders.hazelcast.ClientHazelcastConfiguration;
import uk.nhs.hdn.crds.repository.senders.hazelcast.HazelcastRepositoryExampleApplication;

import java.io.IOException;
import java.sql.SQLException;

import static uk.nhs.hdn.crds.registry.server.HazelcastConfiguration.DefaultHazelcastPortNotFirewalled;
import static uk.nhs.hdn.crds.repository.senders.hazelcast.ClientHazelcastConfiguration.DefaultHostname;

public final class RepositoryExampleConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String HostnameOption = "hostname";
	private static final String PortOption = "port";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		AbstractConsoleEntryPoint.execute(RepositoryExampleConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(HostnameOption, "server running Hazelcast").withRequiredArg().ofType(String.class).defaultsTo(DefaultHostname).describedAs("public services server");
		options.accepts(PortOption, "port of server running Hazelcast").withRequiredArg().ofType(Integer.class).defaultsTo(DefaultHazelcastPortNotFirewalled).describedAs("usual port of Hazelcast, modified for access via Azure");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws SQLException, IOException
	{
		final String hostname = defaulted(optionSet, HostnameOption);
		final char portNumber = portNumber(optionSet, PortOption);

		final ClientHazelcastConfiguration hazelcastConfiguration = new ClientHazelcastConfiguration(hostname, (int) portNumber);
		final PostgresqlConnectionProvider postgresqlConnectionProvider = new PostgresqlConnectionProvider("localhost", "postgres", "repository-example", "postgres", "postgres");
		new HazelcastRepositoryExampleApplication(hazelcastConfiguration).run(postgresqlConnectionProvider);
	}
}
