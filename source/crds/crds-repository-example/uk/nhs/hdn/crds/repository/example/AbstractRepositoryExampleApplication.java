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

package uk.nhs.hdn.crds.repository.example;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.sql.PollingConnectionUserRunnable;
import uk.nhs.hdn.common.sql.postgresql.PostgresqlConnectionProvider;
import uk.nhs.hdn.common.sql.postgresql.PostgresqlListenerRunnable;
import uk.nhs.hdn.crds.repository.StuffEventMessageUser;
import uk.nhs.hdn.crds.repository.parsing.StuffEventMessageParser;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import static uk.nhs.hdn.crds.repository.example.DemonstrateChangesConnectionUser.DemonstrateChangesConnectionUserInstance;
import static uk.nhs.hdn.crds.repository.example.SchemaCreation.createOrReplaceSchema;

public abstract class AbstractRepositoryExampleApplication<U extends StuffEventMessageUser>
{
	@NotNull protected final U sendingStuffEventMessageUser;

	protected AbstractRepositoryExampleApplication(@NotNull final U sendingStuffEventMessageUser)
	{
		this.sendingStuffEventMessageUser = sendingStuffEventMessageUser;
	}

	/*
		For a production system, the threads need to be managed so that a nearly definitive shutdown ordering is possible, so that all messages are read from psql, are enqueued and then locally persisted ready for restart

		Also so that postgresqlConnectionProvider.close() occurs AFTER the postgresql connection using threads have ended (currently not invoked, and so PSQL connections are left dangling)

		The stormmq client has the necessary logic embedded to do so using implementations of AbstractTerminableRunnable and VolatileTerminationSignal
	 */
	public final void run(@NotNull final PostgresqlConnectionProvider postgresqlConnectionProvider) throws SQLException
	{
		createOrReplaceSchema(postgresqlConnectionProvider);

		final AtomicBoolean terminationSignal = startMessageSender();

		final Runnable runnable = new PostgresqlListenerRunnable
		(
			postgresqlConnectionProvider,
			"patients",
			terminationSignal,
			new MessageSendingProcessNotificationUser
			(
				StuffEventMessageParser.singleLineStuffEventMessageParser
				(
					sendingStuffEventMessageUser
				)
			)
		);

		new Thread(runnable, "Received Postgresql Notifications").start();

		new Thread(new PollingConnectionUserRunnable(postgresqlConnectionProvider, DemonstrateChangesConnectionUserInstance, terminationSignal), "Demonstrate Record Changes in Postgresql so we have some live data to be notified about").start();
	}

	@NotNull
	protected abstract AtomicBoolean startMessageSender();
}
