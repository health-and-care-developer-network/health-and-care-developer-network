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
import uk.nhs.hdn.common.sql.PollingConnectionUserRunnable;
import uk.nhs.hdn.common.sql.postgresql.PostgresqlConnectionProvider;
import uk.nhs.hdn.common.sql.postgresql.PostgresqlListenerRunnable;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;
import uk.nhs.hdn.crds.repository.StuffEventMessageUser;

import java.sql.SQLException;

import static java.lang.System.out;
import static uk.nhs.hdn.crds.repository.example.DemonstrateChangesConnectionUser.DemonstrateChangesConnectionUserInstance;
import static uk.nhs.hdn.crds.repository.example.SchemaCreation.createOrReplaceSchema;
import static uk.nhs.hdn.crds.repository.parsing.StuffEventMessageParser.singleLineStuffEventMessageParser;

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
		@SuppressWarnings("DuplicateStringLiteralInspection") final PostgresqlConnectionProvider postgresqlConnectionProvider = new PostgresqlConnectionProvider("localhost", "postgres", "repository-example", "postgres", "postgres");
		run(postgresqlConnectionProvider);
	}

	private static void run(final PostgresqlConnectionProvider postgresqlConnectionProvider) throws SQLException
	{
		final StuffEventMessageUser stuffEventMessageUser = new StuffEventMessageUser()
		{
			@SuppressWarnings("UseOfSystemOutOrSystemErr")
			@Override
			public void use(@NotNull final StuffEventMessage stuffEventMessage)
			{
				out.println(stuffEventMessage.toWireFormat());
			}
		};
		try
		{
			createOrReplaceSchema(postgresqlConnectionProvider);

			new Thread
			(
				new PollingConnectionUserRunnable
				(
					postgresqlConnectionProvider, DemonstrateChangesConnectionUserInstance
				),
				"Demonstrate Record Changes in Postgresql so we have some live data to be notified about"
			).start();

			try
			(
				PostgresqlListenerRunnable patients = new PostgresqlListenerRunnable
				(
					postgresqlConnectionProvider,
					"patients",
					new MessageSendingProcessNotificationUser
					(
						singleLineStuffEventMessageParser(stuffEventMessageUser)
					)
				)
			)
			{
				patients.run();
			}
		}
		finally
		{
			postgresqlConnectionProvider.close();
		}
	}

}
