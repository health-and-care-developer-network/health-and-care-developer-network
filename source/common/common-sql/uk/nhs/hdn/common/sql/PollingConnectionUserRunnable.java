package uk.nhs.hdn.common.sql;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

public final class PollingConnectionUserRunnable implements Runnable
{
	public static final long OneSecond = 1000L;
	@NotNull private final ConnectionProvider connectionProvider;
	@NotNull private final ConnectionUser connectionUser;
	@NotNull private final AtomicBoolean terminationSignal;

	public PollingConnectionUserRunnable(@NotNull final ConnectionProvider connectionProvider, @NotNull final ConnectionUser connectionUser, @NotNull final AtomicBoolean terminationSignal)
	{
		this.connectionProvider = connectionProvider;
		this.connectionUser = connectionUser;
		this.terminationSignal = terminationSignal;
	}

	@Override
	public void run()
	{
		// The loop design here relinquishes the connection after each loop. This is a better design when using pooled connections and long pauses between their use
		while(shouldContinueExecution())
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
				// A production design should fire the terminationSignal
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
						// A production design should fire the terminationSignal
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
				// A production design should fire the terminationSignal
				return;
			}

		}
	}

	private boolean shouldContinueExecution()
	{
		return !terminationSignal.get();
	}

	private static void pause() throws InterruptedException {sleep(OneSecond);}
}
