/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.ExampleRestEndpoint;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class ServerConsoleEnttryPoint
{
	private ServerConsoleEnttryPoint()
	{
	}

	public static void main(@NotNull final String... commandLineArguments) throws IOException
	{
		final Server server = new Server(new InetSocketAddress(8080), 20, new ExampleRestEndpoint());
		server.start();
	}
}
