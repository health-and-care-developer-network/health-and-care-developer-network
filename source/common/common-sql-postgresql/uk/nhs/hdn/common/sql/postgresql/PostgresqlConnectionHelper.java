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
import org.postgresql.ds.PGPoolingDataSource;
import uk.nhs.hdn.common.sql.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;

import static java.util.UUID.randomUUID;

public final class PostgresqlConnectionHelper implements AutoCloseable, ConnectionProvider
{
	private static final int DefaultPostgresPort = 5432;
	private static final int ThirtySeconds = 30;
	private static final int FourKilobytes = 4096;

	@NotNull private final PGPoolingDataSource dataSource;

	public PostgresqlConnectionHelper(@NonNls @NotNull final String serverDnsHostName, @NonNls @NotNull final String databaseName, @NonNls @NotNull final String applicationName, @NonNls @NotNull final String userName, @NonNls @NotNull final String password)
	{
		dataSource = new PGPoolingDataSource();
		dataSource.setDataSourceName(randomUUID().toString());
		dataSource.setInitialConnections(2);
		dataSource.setMaxConnections(10);

		dataSource.setServerName(serverDnsHostName);
		dataSource.setPortNumber(DefaultPostgresPort);
		dataSource.setTcpKeepAlive(true);
		dataSource.setSocketTimeout(ThirtySeconds);

		dataSource.setSsl(false);
		dataSource.setProtocolVersion(0);
		dataSource.setSendBufferSize(FourKilobytes);
		dataSource.setReceiveBufferSize(FourKilobytes);
		dataSource.setBinaryTransfer(true);

		dataSource.setDatabaseName(databaseName);
		dataSource.setApplicationName(applicationName);
		dataSource.setUser(userName);
		dataSource.setPassword(password);
	}

	@Override
	@NotNull
	public Connection connection()
	{
		try
		{
			return dataSource.getConnection();
		}
		catch (SQLException e)
		{
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void close()
	{
		dataSource.close();
	}
}
