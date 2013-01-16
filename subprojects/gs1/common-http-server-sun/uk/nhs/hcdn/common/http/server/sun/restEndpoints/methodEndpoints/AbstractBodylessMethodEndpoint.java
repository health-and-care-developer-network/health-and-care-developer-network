/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;
import uk.nhs.hcdn.common.http.ResponseCode;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.io.IOException;

import static uk.nhs.hcdn.common.http.server.sun.helpers.ResponseHeadersHelper.emptyInvariantResponseHeaders;

public abstract class AbstractBodylessMethodEndpoint<R extends ResourceStateSnapshot> extends AbstractToString implements MethodEndpoint<R>
{
	protected AbstractBodylessMethodEndpoint()
	{
	}

	@Override
	public final void handle(@NotNull final String rawRelativeUriPath, @Nullable final String rawQueryString, @NotNull final HttpExchange httpExchange, @NotNull final R resourceStateSnapshot) throws IOException, BadRequestException
	{
		validateRequestHeaders(httpExchange.getRequestHeaders());
		final Headers headers = emptyInvariantResponseHeaders(httpExchange, resourceStateSnapshot.lastModifiedInRfc2822Form());
		@ResponseCode final int responseCode = addResponseHeaders(headers);
		httpExchange.sendResponseHeaders(responseCode, NoContentBodyMagicValue);
		httpExchange.close();
	}

	protected abstract void validateRequestHeaders(@NotNull final Headers requestHeaders) throws BadRequestException;

	@ResponseCode
	protected abstract int addResponseHeaders(@NotNull final Headers responseHeaders);
}
