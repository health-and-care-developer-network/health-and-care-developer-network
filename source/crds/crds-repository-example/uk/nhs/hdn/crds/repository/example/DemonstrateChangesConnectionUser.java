package uk.nhs.hdn.crds.repository.example;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.sql.ConnectionUser;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DemonstrateChangesConnectionUser implements ConnectionUser
{
	@NotNull public static final ConnectionUser DemonstrateChangesConnectionUserInstance = new DemonstrateChangesConnectionUser();

	private DemonstrateChangesConnectionUser()
	{
	}

	@Override
	public void useConnection(@NotNull final Connection connection) throws SQLException
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
}
