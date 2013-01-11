package uk.nhs.hcdn.barcodes.gs1.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.io.IOException;
import java.net.Authenticator;

public abstract class AbstractRestEndpoint extends AbstractToString implements RestEndpoint
{
	@NotNull
	private final String relativePath;

	@Nullable
	private final Authenticator authenticator;

	protected AbstractRestEndpoint(@NotNull final String relativePath, @Nullable final Authenticator authenticator)
	{
		this.relativePath = relativePath;
		this.authenticator = authenticator;
	}

	@Override
	public void register(@NotNull final HttpServer httpServer)
	{
		final HttpContext context = httpServer.createContext(relativePath);
		if (authenticator != null)
		{
			context.setAuthenticator(null);
		}
	}

	@SuppressWarnings("AbstractMethodOverridesAbstractMethod") // to add @NotNull annotation
	@Override
	public abstract void handle(@NotNull final HttpExchange httpExchange) throws IOException;
}
