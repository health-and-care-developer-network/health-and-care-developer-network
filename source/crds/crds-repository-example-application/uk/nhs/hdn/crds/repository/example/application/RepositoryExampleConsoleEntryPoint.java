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
import uk.nhs.hdn.crds.registry.server.HazelcastConfiguration;
import uk.nhs.hdn.crds.repository.senders.hazelcast.HazelcastRepositoryExampleApplication;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static uk.nhs.hdn.crds.registry.domain.Configuration.*;
import static uk.nhs.hdn.crds.registry.server.HazelcastConfiguration.DefaultHazelcastPort;
import static uk.nhs.hdn.crds.registry.server.HazelcastConfiguration.DefaultHazelcastTcp;

public final class RepositoryExampleConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String InstanceIdOption = "instance-id";
	private static final String PgpassFileOption = "pgpass-file";
	private static final String PersistedMessagesPathOption = "persisted-messages-path";

	private static final int DefaultInstanceId = 0;
	private static final String DefaultPersistedMessagesPath = "/srv/hdn-crds-repository-example";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		AbstractConsoleEntryPoint.execute(RepositoryExampleConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		options.accepts(InstanceIdOption, "long lived instance identifier").withRequiredArg().ofType(Integer.class).defaultsTo(DefaultInstanceId).describedAs("Instance identifer. Must be unique but consistent across invocations");
		options.accepts(PgpassFileOption, "location of password file if not ~/.pgpass").withRequiredArg().ofType(File.class);
		options.accepts(PersistedMessagesPathOption, "writable location to store persisted but not sent messages").withRequiredArg().ofType(File.class).defaultsTo(new File(DefaultPersistedMessagesPath)).describedAs("Folder path containing persisted messages");
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws SQLException, IOException
	{
		final int instanceId = defaulted(optionSet, InstanceIdOption);
		final File pgpassFile;
		if (optionSet.has(PgpassFileOption))
		{
			pgpassFile = readableFile(optionSet, PgpassFileOption);
		}
		else
		{
			pgpassFile = new File(".pgpass"); //findDefaultPgpassFileIfNoneSpecified(null);
		}

		final File persistedMessagesPath =  readableDirectory(optionSet, PersistedMessagesPathOption);

		final PostgresqlConnectionProvider postgresqlConnectionProvider = new PostgresqlConnectionProvider("localhost", "postgres", "repository-example", "postgres", "postgres");
		new HazelcastRepositoryExampleApplication(persistedMessagesPath, UserName, "", instanceId, HostName, VirtualHostName, QueueName, new HazelcastConfiguration((char) DefaultHazelcastPort, DefaultHazelcastTcp)).run(postgresqlConnectionProvider);
	}
}
