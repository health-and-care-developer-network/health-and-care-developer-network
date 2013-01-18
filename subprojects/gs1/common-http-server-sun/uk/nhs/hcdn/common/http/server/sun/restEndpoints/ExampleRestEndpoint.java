/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
		@Nullable final String query = requestURI.getQuery();
		// nlull => no string, "" => empty string (ie ?)

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
