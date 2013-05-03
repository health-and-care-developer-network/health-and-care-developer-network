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
import uk.nhs.hdn.common.sql.PollingConnectionUserRunnable;
import uk.nhs.hdn.common.sql.sqlserver.SqlServerConnectionProvider;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;
import uk.nhs.hdn.crds.repository.StuffEventMessageUser;
import uk.nhs.hdn.crds.repository.example.windows.EventPollingConnectionUser;

import java.sql.SQLException;

import static java.lang.System.out;
import static uk.nhs.hdn.crds.repository.example.DemonstrateChangesConnectionUser.DemonstrateChangesConnectionUserInstance;
import static uk.nhs.hdn.crds.repository.example.windows.SchemaCreation.createOrReplaceSchema;

public final class RepositoryExampleConsoleEntryPoint extends AbstractConsoleEntryPoint
{
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(@NotNull final String... commandLineArguments)
	{
		AbstractConsoleEntryPoint.execute(RepositoryExampleConsoleEntryPoint.class, commandLineArguments);
	}

	@Override
	protected boolean options(@NotNull final OptionParser options)
	{
		return true;
	}

	@Override
	protected void execute(@NotNull final OptionSet optionSet) throws SQLException
	{
		final ConnectionProvider provider = new SqlServerConnectionProvider("localhost", "crds", "repository-example-windows", "sa", "password");
		run(provider);
	}

	private static void run(final ConnectionProvider connectionProvider)
	{
		createOrReplaceSchema(connectionProvider);

		new Thread
		(
			new PollingConnectionUserRunnable
			(
				connectionProvider,
				DemonstrateChangesConnectionUserInstance
			),
			"Demonstrate Record Changes in SQL Server so we have some live data to be notified about"
		).start();

		new PollingConnectionUserRunnable
		(
			connectionProvider,
			new EventPollingConnectionUser
			(
				new StuffEventMessageUser()
				{
					@SuppressWarnings("UseOfSystemOutOrSystemErr")
					@Override
					public void use(@NotNull final StuffEventMessage stuffEventMessage)
					{
						out.println(stuffEventMessage);
					}
				}
			)
		).run();
	}

}
