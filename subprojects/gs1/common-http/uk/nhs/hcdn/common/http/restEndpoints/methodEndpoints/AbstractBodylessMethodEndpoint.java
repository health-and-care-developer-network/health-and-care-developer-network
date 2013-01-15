/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.GregorianCalendar;

import static uk.nhs.hcdn.common.http.restEndpoints.methodEndpoints.ResponseHeadersHelper.emptyInvariantResponseHeaders;

public abstract class AbstractBodylessMethodEndpoint implements MethodEndpoint
{
	protected AbstractBodylessMethodEndpoint()
	{
	}

	@Override
	public void handle(@NotNull final String rawRelativeUriPath, @Nullable final String rawQueryString, @NotNull final HttpExchange httpExchange) throws IOException
	{
		final Headers headers = emptyInvariantResponseHeaders(httpExchange, lastModified());
		addHeaders(headers);
		httpExchange.sendResponseHeaders(ResponseCode.BadRequestResponseCode, -1L);
		httpExchange.close();
	}

	@NotNull
	protected abstract GregorianCalendar lastModified();

	@ResponseCode
	protected abstract int addHeaders(@NotNull final Headers headers);
}
