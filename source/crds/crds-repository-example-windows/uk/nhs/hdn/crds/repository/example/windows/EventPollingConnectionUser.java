package uk.nhs.hdn.crds.repository.example.windows;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.sql.ConnectionUser;
import uk.nhs.hdn.crds.registry.domain.StuffEvent;
import uk.nhs.hdn.crds.registry.domain.StuffEventKind;
import uk.nhs.hdn.crds.registry.domain.StuffEventMessage;
import uk.nhs.hdn.crds.registry.domain.identifiers.ProviderIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.RepositoryIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffEventIdentifier;
import uk.nhs.hdn.crds.registry.domain.identifiers.StuffIdentifier;
import uk.nhs.hdn.crds.repository.StuffEventMessageUser;
import uk.nhs.hdn.number.NhsNumber;

import java.sql.*;
import java.util.UUID;

import static java.util.Calendar.getInstance;
import static uk.nhs.hdn.common.TimeZoneHelper.UTC;
import static uk.nhs.hdn.common.sql.ResultSetHelper.getUuid;

public final class EventPollingConnectionUser implements ConnectionUser
{
	@NotNull private final StuffEventMessageUser stuffEventMessageUser;

	public EventPollingConnectionUser(@NotNull final StuffEventMessageUser stuffEventMessageUser)
	{
		this.stuffEventMessageUser = stuffEventMessageUser;
	}

	@Override
	public void useConnection(@NotNull final Connection connection) throws SQLException
	{
		connection.setAutoCommit(false);

		@Nullable UUID stuffEventSetIdentifier = null;

		boolean exceptionNotThrown = false;
		final Statement statement = connection.createStatement();
		try
		{
			try(final ResultSet resultSet = statement.executeQuery("SELECT * FROM events;"))
			{
				while(resultSet.next())
				{
					final ProviderIdentifier providerIdentifier = new ProviderIdentifier(getUuid(resultSet, 1));
					final RepositoryIdentifier repositoryIdentifier = new RepositoryIdentifier(getUuid(resultSet, 2));
					final StuffIdentifier stuffIdentifier = new StuffIdentifier(getUuid(resultSet, 3));
					stuffEventSetIdentifier = getUuid(resultSet, 4);
					final NhsNumber nhsNumber = NhsNumber.valueOf(resultSet.getNString(5));
					final StuffEventIdentifier stuffEventIdentifier = new StuffEventIdentifier(getUuid(resultSet, 6));
					final Date stuffEventTimestamp = resultSet.getDate(7, getInstance(UTC));
					final StuffEventKind stuffEventKind = StuffEventKind.valueOf(resultSet.getNString(8));
					stuffEventMessageUser.use(new StuffEventMessage
					(
						nhsNumber,
						providerIdentifier,
						repositoryIdentifier,
						stuffIdentifier,
						new StuffEvent
						(
							stuffEventIdentifier,
							stuffEventTimestamp.getTime(),
							stuffEventKind
						)
					));
				}
			}
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

		if (stuffEventSetIdentifier == null)
		{
			connection.rollback();
		}
		else
		{
			delete(connection, stuffEventSetIdentifier);
			connection.commit();
		}
	}

	private static void delete(final Connection connection, final UUID stuffEventSetIdentifier) throws SQLException
	{
		try (PreparedStatement statement = connection.prepareStatement("DELETE * FROM events WHERE repositoryIdentifierUuid = ?;"))
		{
			statement.setNString(1, stuffEventSetIdentifier.toString());
			boolean exceptionNotThrown = false;
			try
			{
				statement.execute();
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
}
