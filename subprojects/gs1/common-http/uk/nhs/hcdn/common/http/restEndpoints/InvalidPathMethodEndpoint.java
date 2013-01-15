package uk.nhs.hcdn.common.http.restEndpoints;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.http.restEndpoints.methodEndpoints.MethodEndpoint;
import uk.nhs.hcdn.common.http.restEndpoints.methodEndpoints.ResponseHeadersHelper;

import java.io.IOException;

import static uk.nhs.hcdn.common.http.restEndpoints.methodEndpoints.ResponseCode.BadRequestResponseCode;

class InvalidPathMethodEndpoint implements MethodEndpoint
{
	@Override
	public void handle(@NotNull final String rawRelativeUriPath, @Nullable final String rawQueryString, @NotNull final HttpExchange httpExchange) throws IOException
	{
		final Headers headers = ResponseHeadersHelper.emptyInvariantResponseHeaders(httpExchange);
		httpExchange.sendResponseHeaders(BadRequestResponseCode, -1L);
		httpExchange.close();
	}
}
