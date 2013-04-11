package uk.nhs.hdn.crds.repository;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static uk.nhs.hdn.crds.repository.ResourceLoader.utf8TextResourceContents;

public final class SqlHelper
{
	public static void createOrReplaceSchema(@NotNull final PostgresqlConnectionHelper postgresqlConnectionHelper)
	{
		new SqlHelper(postgresqlConnectionHelper).createSchema
		(
			utf8TextResourceContents("PatientAdministrationSystem.database.sql"),
			utf8TextResourceContents("PatientAdministrationSystem.patients.sql")
		);
	}

	@NotNull private final PostgresqlConnectionHelper postgresqlConnectionHelper;

	public SqlHelper(@NotNull final PostgresqlConnectionHelper postgresqlConnectionHelper)
	{
		this.postgresqlConnectionHelper = postgresqlConnectionHelper;
	}

	@SuppressWarnings("JDBCExecuteWithNonConstantString")
	public void createSchema(@NotNull final String... sqlStatementsOrGroupsOfStatements)
	{
		final Connection connection = postgresqlConnectionHelper.connection();
		try
		{
			connection.setAutoCommit(true);

			try (Statement statement = connection.createStatement())
			{
				for (final String sqlStatements : sqlStatementsOrGroupsOfStatements)
				{
					statement.addBatch(sqlStatements);
				}
				statement.executeBatch();
			}
		}
		catch (SQLException e)
		{
			throw new IllegalStateException(e);
		}
		finally
		{
			try
			{
				connection.close();
			}
			catch (SQLException ignored)
			{
			}
		}
	}
}
