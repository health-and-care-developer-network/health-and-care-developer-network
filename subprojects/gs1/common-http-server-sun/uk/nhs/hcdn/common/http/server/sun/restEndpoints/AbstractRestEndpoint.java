/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.io.IOException;
import java.net.Authenticator;

public abstract class AbstractRestEndpoint extends AbstractToString implements RestEndpoint
{
	@NotNull
	private final String absoluteRawPath;

	@Nullable
	private final Authenticator authenticator;

	protected AbstractRestEndpoint(@NonNls @NotNull final String absoluteRawPath, @Nullable final Authenticator authenticator)
	{
		this.absoluteRawPath = absoluteRawPath;
		this.authenticator = authenticator;
	}

	@Override
	public final void register(@NotNull final HttpServer httpServer)
	{
		final HttpContext context = httpServer.createContext(absoluteRawPath, this);
		if (authenticator != null)
		{
			context.setAuthenticator(null);
		}
	}

	@SuppressWarnings("AbstractMethodOverridesAbstractMethod") // to add @NotNull annotation
	@Override
	public abstract void handle(@NotNull final HttpExchange httpExchange) throws IOException;
}
