package uk.nhs.hdn.common.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

import static java.lang.Thread.sleep;

public final class PollingConnectionUserRunnable implements Runnable
{
	public static final long OneSecond = 1000L;
	@NotNull private final ConnectionProvider connectionProvider;
	@NotNull private final ConnectionUser connectionUser;

	public PollingConnectionUserRunnable(@NotNull final ConnectionProvider connectionProvider, @NotNull final ConnectionUser connectionUser)
	{
		this.connectionProvider = connectionProvider;
		this.connectionUser = connectionUser;
	}

	@Override
	public void run()
	{
		// The loop design here relinquishes the connection after each loop. This is a better design when using pooled connections and long pauses between their use
		do
		{
			boolean exceptionNotThrown = false;
			final Connection connection = connectionProvider.connection();
			try
			{
				connectionUser.useConnection(connection);
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

	private static void pause() throws InterruptedException {sleep(OneSecond);}
}
