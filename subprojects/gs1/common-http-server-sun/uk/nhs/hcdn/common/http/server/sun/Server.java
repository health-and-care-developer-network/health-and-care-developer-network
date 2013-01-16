/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun;

import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.RestEndpoint;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

import static com.sun.net.httpserver.HttpServer.create;
import static uk.nhs.hcdn.common.VariableArgumentsHelper.copyOf;

// Documentation: http://docs.oracle.com/javase/6/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/package-summary.html
public final class Server extends AbstractToString
{
	private static final int TenSecondsGraceToStop = 10;

	@NotNull
	private final InetSocketAddress listenOn;

	private final int backlog;

	@NotNull
	private final RestEndpoint[] restEndpoints;

	@Nullable
	private HttpServer httpServer;

	public Server(@NotNull final InetSocketAddress listenOn, final int backlog, @NotNull final RestEndpoint... restEndpoints)
	{
		if (backlog < 0)
		{
			throw new IllegalArgumentException("backlog must not be negative");
		}
		this.listenOn = listenOn;
		this.backlog = backlog;
		this.restEndpoints = copyOf(restEndpoints);
		httpServer = null;
	}

	public void start() throws IOException
	{
		httpServer = create(listenOn, backlog);
		for (final RestEndpoint restEndpoint : restEndpoints)
		{
			restEndpoint.register(httpServer);
		}
		// TODO: threading
		httpServer.setExecutor(null);
		httpServer.start();
	}

	public void stop()
	{
		if (httpServer == null)
		{
			return;
		}
		httpServer.stop(TenSecondsGraceToStop);
		httpServer = null;
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final Server server = (Server) obj;

		if (!listenOn.equals(server.listenOn))
		{
			return false;
		}
		if (!Arrays.equals(restEndpoints, server.restEndpoints))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = listenOn.hashCode();
		result = 31 * result + Arrays.hashCode(restEndpoints);
		return result;
	}
}
