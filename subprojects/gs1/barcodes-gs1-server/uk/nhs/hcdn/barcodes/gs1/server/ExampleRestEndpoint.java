package uk.nhs.hcdn.barcodes.gs1.server;

import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Authenticator;

public final class ExampleRestEndpoint extends AbstractRestEndpoint
{
	private static final int OK = 200;

	public ExampleRestEndpoint(@NotNull final String relativePath, @Nullable final Authenticator authenticator)
	{
		super(relativePath, authenticator);
	}

	@SuppressWarnings("OverlyBroadThrowsClause")
	@Override
	public void handle(@NotNull final HttpExchange httpExchange) throws IOException
	{
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
