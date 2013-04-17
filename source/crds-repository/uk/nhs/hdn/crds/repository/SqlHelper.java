package uk.nhs.hdn.crds.repository;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static uk.nhs.hdn.crds.repository.ResourceLoader.utf8TextResourceContents;

public final class SqlHelper
{
	private static final Pattern SplitPattern = compile("-- SPLIT");

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
	public void createSchema(@NotNull final String... groupsOfSplittableSqlStatements)
	{
		final Connection connection = postgresqlConnectionHelper.connection();
		try
		{
			connection.setAutoCommit(true);

			try (Statement statement = connection.createStatement())
			{
				for (final String groupOfSplittableSqlStatements : groupsOfSplittableSqlStatements)
				{
					final String[] sqlStatements = SplitPattern.split(groupOfSplittableSqlStatements);
					for (final String sqlStatement : sqlStatements)
					{
						statement.addBatch(sqlStatement);
					}
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
