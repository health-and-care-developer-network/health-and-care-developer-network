/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.response;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsExchange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.GregorianCalendar;

import static uk.nhs.hcdn.common.GregorianCalendarHelper.rfc2822DateForNow;
import static uk.nhs.hcdn.common.GregorianCalendarHelper.toRfc2822Form;
import static uk.nhs.hcdn.common.http.restEndpoints.methodEndpoints.MethodEndpoint.*;

public final class ResponseHeadersHelper
{
	private ResponseHeadersHelper()
	{
	}

	@NotNull
	public static Headers emptyInvariantResponseHeaders(@NotNull final HttpExchange httpExchange, @NotNull final String lastModifiedInRfc2822Form)
	{
		return anyResponseHeaders(httpExchange, "text/plain;charset=utf-8", CacheControlHeaderValueMaximum, ExpiresHeaderValueMaximum, lastModifiedInRfc2822Form);
	}

	@NotNull
	public static Headers anyResponseHeaders(@NotNull final HttpExchange httpExchange, @NonNls @NotNull final String contentTypeValue, @NotNull final String cacheControlHeaderValue, @NotNull final String expiresHeaderValue, @NotNull final GregorianCalendar lastModified)
	{
		return anyResponseHeaders(httpExchange, contentTypeValue, cacheControlHeaderValue, expiresHeaderValue, toRfc2822Form(lastModified));
	}

	@NotNull
	public static Headers anyResponseHeaders(@NotNull final HttpExchange httpExchange, @NonNls @NotNull final String contentTypeValue, @NotNull final String cacheControlHeaderValue, @NotNull final String expiresHeaderValue, @NotNull final String lastModifiedInRfc2822Form)
	{
		final Headers responseHeaders = httpExchange.getResponseHeaders();
		responseHeaders.set(ServerHeaderName, ServerHeaderValue);
		responseHeaders.set(DateHeaderName, rfc2822DateForNow());
		responseHeaders.set(ContentLanguageHeaderName, ContentLanguageHeaderValue);
		responseHeaders.set(ConnectionHeaderName, "close");
		final boolean isHttps = httpExchange instanceof HttpsExchange;
		if (isHttps)
		{
			responseHeaders.set(StrictTransportSecurityHeaderName, StrictTransportSecurityHeaderValueMaximum);
		}
		responseHeaders.set(XFrameOptionsHeaderName, "DENY");
		responseHeaders.set(XXssProtectionHeaderName, "1; mode=block");
		responseHeaders.set(XRimAutoMatchHeaderName, "none");
		responseHeaders.set(XRobotsTagHeaderName, "none");
		responseHeaders.set(AccessControlAllowOriginHeaderName, "*");
		responseHeaders.set(ContentTypeHeaderName, contentTypeValue);
		responseHeaders.set(CacheControlHeaderName, cacheControlHeaderValue);
		responseHeaders.set(ExpiresHeaderName, expiresHeaderValue);
		responseHeaders.set(LastModifiedHeaderName, lastModifiedInRfc2822Form);
		return responseHeaders;
	}
}
