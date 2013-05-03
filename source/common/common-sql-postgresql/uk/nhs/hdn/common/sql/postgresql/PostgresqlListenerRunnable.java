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

package uk.nhs.hdn.common.sql.postgresql;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.Thread.sleep;

public final class PostgresqlListenerRunnable implements Runnable, AutoCloseable
{
	public static final long TenthOfASecond = 100L;
	@NotNull private final Connection connection;
	@NotNull private final ProcessNotificationUser processNotificationUser;

	@SuppressWarnings("JDBCExecuteWithNonConstantString")
	public PostgresqlListenerRunnable(@NotNull final PostgresqlConnectionProvider postgresqlConnectionProvider, @NotNull @NonNls final String channel, @NotNull final ProcessNotificationUser processNotificationUser) throws SQLException
	{
		connection = postgresqlConnectionProvider.connection();
		connection.setAutoCommit(true);
		connection.setReadOnly(true);
		this.processNotificationUser = processNotificationUser;
		try (Statement statement = connection.createStatement())
		{
			statement.execute("LISTEN " + channel);
		}
	}

	@Override
	public void close() throws SQLException
	{
		connection.close();
	}

	@SuppressWarnings("CallToStringEquals")
	@Override
	public void run()
	{
		final PGConnection pgconn = (PGConnection) connection;
		do
		{
			try
			{
				dummyQueryToForceBackendToSendAnyPendingNotifications();
			}
			catch (SQLException e)
			{
				if (!"No results were returned by the query.".equals(e.getMessage()))
				{
					throw new IllegalStateException(e);
				}
			}

			final PGNotification[] notifications;
			try
			{
				notifications = pgconn.getNotifications();
			}
			catch (SQLException e)
			{
				throw new IllegalStateException(e);
			}
			processNotifications(notifications);

			try
			{
				pause();
			}
			catch (InterruptedException ignored)
			{
				return;
			}
		}
		while (true);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	private void processNotifications(@Nullable final PGNotification[] notifications)
	{
		if (notifications == null)
		{
			return;
		}
		for (final PGNotification notification : notifications)
		{
			final String notificationName = notification.getName(); // same as channel parameter
			final String notificationParameter = notification.getParameter();
			processNotificationUser.processNotification(notificationName, notificationParameter);
		}
	}

	private void dummyQueryToForceBackendToSendAnyPendingNotifications() throws SQLException
	{
		try (Statement statement = connection.createStatement())
		{
			//noinspection EmptyTryBlock
			try (final ResultSet resultSet = statement.executeQuery("SELECT 1;"))
			{
			}
		}
	}

	private static void pause() throws InterruptedException
	{
		// a yield might be a better choice if heavily loaded.
		sleep(TenthOfASecond);
	}
}
