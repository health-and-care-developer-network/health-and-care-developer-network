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

package uk.nhs.hdn.crds.repository.example.windows.application;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.commandLine.AbstractConsoleEntryPoint;
import uk.nhs.hdn.common.sql.ConnectionProvider;
import uk.nhs.hdn.common.sql.sqlserver.SqlServerConnectionProvider;
import uk.nhs.hdn.crds.registry.server.HazelcastConfiguration;
import uk.nhs.hdn.crds.repository.senders.hazelcast.HazelcastRepositoryExampleWindowsApplication;

import java.io.File;
import java.io.IOException;

import static uk.nhs.hdn.crds.registry.domain.Configuration.*;
import static uk.nhs.hdn.crds.registry.server.HazelcastConfiguration.DefaultHazelcastPort;

public final class RepositoryExampleWindowsConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	private static final String InstanceIdOption = "instance-id";
	private static final String PgpassFileOption = "pgpass-file";
	private static final String PersistedMessagesPathOption = "persisted-messages-path";

	private static final int DefaultInstanceId = 0;
	private static final String DefaultPersistedMessagesPath = "/srv/hdn-crds-repository-example";

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		AbstractConsoleEntryPoint.execute(RepositoryExampleWindowsConsoleEntryPoint.class, commandLineArguments);
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
	protected void execute(@NotNull final OptionSet optionSet) throws IOException
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

		final File persistedMessagesPath = readableDirectory(optionSet, PersistedMessagesPathOption);

		final ConnectionProvider provider = new SqlServerConnectionProvider("localhost", "crds", "repository-example-windows", "sa", "password");
		new HazelcastRepositoryExampleWindowsApplication(persistedMessagesPath, UserName, "", instanceId, HostName, VirtualHostName, QueueName, new HazelcastConfiguration((char) DefaultHazelcastPort)).run(provider);
	}
}
