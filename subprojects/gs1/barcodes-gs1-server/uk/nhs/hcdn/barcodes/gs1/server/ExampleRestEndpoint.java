/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.barcodes.gs1.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public final class ExampleRestEndpoint extends AbstractWithoutAuthenticationRestEndpoint
{
	private static final int OK = 200;

	public ExampleRestEndpoint()
	{
		super("/example");
	}

	@SuppressWarnings("OverlyBroadThrowsClause")
	@Override
	public void handle(@NotNull final HttpExchange httpExchange) throws IOException
	{
		final Headers requestHeaders = httpExchange.getRequestHeaders();
		final String requestMethod = httpExchange.getRequestMethod();
		final URI requestURI = httpExchange.getRequestURI();

		final String host = requestHeaders.getFirst("HOST");

		@NonNls final String response = "Hello World";
		final byte[] responseBytes = response.getBytes("UTF-8");
		httpExchange.sendResponseHeaders(OK, (long) responseBytes.length);
		try (OutputStream responseBody = httpExchange.getResponseBody())
		{
			responseBody.write(responseBytes);
		}

		/*

			JSON
			JSON-P
			CSV
			TSV
			XML

		 */
	}
}
