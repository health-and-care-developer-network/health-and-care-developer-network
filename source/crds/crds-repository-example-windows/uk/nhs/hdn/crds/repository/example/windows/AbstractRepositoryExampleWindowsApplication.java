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

package uk.nhs.hdn.crds.repository.example.windows;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.sql.ConnectionProvider;
import uk.nhs.hdn.common.sql.PollingConnectionUserRunnable;
import uk.nhs.hdn.crds.repository.StuffEventMessageUser;

import java.util.concurrent.atomic.AtomicBoolean;

import static uk.nhs.hdn.crds.repository.example.DemonstrateChangesConnectionUser.DemonstrateChangesConnectionUserInstance;
import static uk.nhs.hdn.crds.repository.example.windows.SchemaCreation.createOrReplaceSchema;

public abstract class AbstractRepositoryExampleWindowsApplication<U extends StuffEventMessageUser>
{
	@NotNull protected final U sendingStuffEventMessageUser;

	protected AbstractRepositoryExampleWindowsApplication(@NotNull final U sendingStuffEventMessageUser)
	{
		this.sendingStuffEventMessageUser = sendingStuffEventMessageUser;
	}

	public final void run(@NotNull final ConnectionProvider connectionProvider)
	{
		createOrReplaceSchema(connectionProvider);

		final AtomicBoolean terminationSignal = startMessageSender();

		new Thread
		(
			new PollingConnectionUserRunnable
			(
				connectionProvider,
				new EventPollingConnectionUser
				(
					sendingStuffEventMessageUser
				),
				terminationSignal
			),
			"Retrieve Events"
		).start();

		new Thread(new PollingConnectionUserRunnable(connectionProvider, DemonstrateChangesConnectionUserInstance, terminationSignal), "Demonstrate Record Changes in SQL Server so we have some live data to be notified about").start();
	}

	@NotNull
	protected abstract AtomicBoolean startMessageSender();
}
