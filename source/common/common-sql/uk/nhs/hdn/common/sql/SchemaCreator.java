package uk.nhs.hdn.common.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public final class SchemaCreator
{
	public static final Pattern SplitPattern = compile("-- SPLIT");

	@NotNull private final ConnectionProvider connectionProvider;

	public SchemaCreator(@NotNull final ConnectionProvider connectionProvider)
	{
		this.connectionProvider = connectionProvider;
	}

	@SuppressWarnings("JDBCExecuteWithNonConstantString")
	public void createSchema(@NotNull final String... groupsOfSplittableSqlStatements)
	{
		final Connection connection = connectionProvider.connection();
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
