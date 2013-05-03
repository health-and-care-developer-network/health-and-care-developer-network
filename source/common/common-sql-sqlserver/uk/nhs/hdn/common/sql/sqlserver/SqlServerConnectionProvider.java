package uk.nhs.hdn.common.sql.sqlserver;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.sql.ConnectionProvider;

import java.sql.Connection;

public final class SqlServerConnectionProvider implements ConnectionProvider
{
	private final SQLServerDataSource sqlServerDataSource;

	public SqlServerConnectionProvider(@NonNls @NotNull final String serverDnsHostName, @NonNls @NotNull final String databaseName, @NonNls @NotNull final String applicationName, @NonNls @NotNull final String userName, @NonNls @NotNull final String password)
	{
		sqlServerDataSource = new SQLServerDataSource();
		sqlServerDataSource.setServerName(serverDnsHostName);
		sqlServerDataSource.setDatabaseName(databaseName);
		sqlServerDataSource.setApplicationName(applicationName);
		sqlServerDataSource.setUser(userName);
		sqlServerDataSource.setPassword(password);

		sqlServerDataSource.setLoginTimeout(30);
	}

	@NotNull
	@Override
	public Connection connection()
	{
		try
		{
			return sqlServerDataSource.getConnection();
		}
		catch (SQLServerException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
