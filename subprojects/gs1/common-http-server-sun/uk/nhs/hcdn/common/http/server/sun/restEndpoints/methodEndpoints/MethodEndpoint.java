/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStates.ResourceStateSnapshot;

import java.io.IOException;

public interface MethodEndpoint<R extends ResourceStateSnapshot>
{
	@NotNull
	String ServerHeaderValue = "hcdn";

	@NotNull
	String ContentLanguageHeaderValue = "en";

	@NotNull
	String StrictTransportSecurityHeaderValueMaximum = "max-age=315360000";

	@NotNull
	String CacheControlHeaderValueMaximum = "public, max-age=315360000";

	@NotNull
	String CacheControlHeaderValueOneHour = "public, max-age=3600";

	@NotNull
	String ExpiresHeaderValueMaximum = "Thu, 31 Dec 2037 23:55:55 GMT";
	long NoContentBodyMagicValue = -1L;

	void handle(@NotNull final String rawRelativeUriPath, @Nullable final String rawQueryString, @NotNull final HttpExchange httpExchange, @NotNull final R resourceStateSnapshot) throws IOException, BadRequestException;
}
