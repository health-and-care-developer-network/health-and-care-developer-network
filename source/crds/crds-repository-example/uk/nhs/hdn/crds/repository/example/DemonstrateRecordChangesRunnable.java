package uk.nhs.hdn.crds.repository.example;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.sql.postgresql.PostgresqlConnectionHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static java.lang.Thread.sleep;

public final class DemonstrateRecordChangesRunnable implements Runnable
{
	public static final long OneSecond = 1000L;
	@NotNull private final PostgresqlConnectionHelper postgresqlConnectionHelper;

	public DemonstrateRecordChangesRunnable(@NotNull final PostgresqlConnectionHelper postgresqlConnectionHelper)
	{
		this.postgresqlConnectionHelper = postgresqlConnectionHelper;
	}

	@Override
	public void run()
	{
		// The loop design here relinquishes the connection after each loop. This is a better design when using pooled connections and long pauses between their use
		do
		{
			boolean exceptionNotThrown = false;
			final Connection connection = postgresqlConnectionHelper.connection();
			try
			{
				useConnection(connection);
				exceptionNotThrown = true;
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
				catch (SQLException e)
				{
					if (exceptionNotThrown)
					{
						throw new IllegalStateException(e);
					}
				}
			}

			try
			{
				pause();
			}
			catch (InterruptedException ignored)
			{
				return;
			}

		} while (true);
	}

	private static void useConnection(final Connection connection) throws SQLException
	{
		connection.setAutoCommit(true);

		boolean exceptionNotThrown = false;
		final Statement statement = connection.createStatement();
		try
		{
			statement.addBatch("INSERT INTO patients(patient_identifier_nhs_number, firstname, lastname, address) VALUES ('1234567880', 'Raph', 'Cohn', 'Skipton');");
			statement.addBatch("UPDATE patients SET address='Leeds' WHERE patient_identifier_nhs_number='1234567880';");
			statement.addBatch("DELETE FROM patients WHERE patient_identifier_nhs_number='1234567880';");
			statement.executeBatch();
			exceptionNotThrown = true;
		}
		finally
		{
			try
			{
				statement.close();
			}
			catch (SQLException e)
			{
				if (exceptionNotThrown)
				{
					throw new IllegalStateException(e);
				}
			}
		}
	}

	private static void pause() throws InterruptedException {sleep(OneSecond);}
}
