package uk.nhs.hcdn.barcodes.gs1.server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Authenticator;

public abstract class AbstractRestEndpoint implements HttpHandler
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

	public void register(@NotNull final HttpServer httpServer)
	{
		final HttpContext context = httpServer.createContext(relativePath);
		if (authenticator != null)
		{
			context.setAuthenticator(null);
		}
	}
}
