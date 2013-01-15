/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StringWriter;
import java.util.Set;

public final class UnsupportedMethodEndpointException extends Exception implements MethodEndpoint
{
	private static final char Comma = ',';

	@NotNull
	private static final String AllowHeaderName = "Allow";

	@NotNull
	private final String allowHeaderValue;

	public UnsupportedMethodEndpointException(@SuppressWarnings("TypeMayBeWeakened") @NotNull final Set<String> supportedMethodEndpoints)
	{
		final StringWriter stringWriter = new StringWriter(100);
		boolean first = true;
		for (final String supportedMethodEndpoint : supportedMethodEndpoints)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				stringWriter.append(Comma);
			}
			stringWriter.append(supportedMethodEndpoint);
		}
		allowHeaderValue = stringWriter.toString();
	}

	@Override
	public void handle(@NotNull final String rawRelativeUriPath, @Nullable final String rawQueryString, @NotNull final HttpExchange httpExchange)
	{
		final Headers responseHeaders = httpExchange.getResponseHeaders();
		responseHeaders.set(AllowHeaderName, allowHeaderValue);
	}
}
