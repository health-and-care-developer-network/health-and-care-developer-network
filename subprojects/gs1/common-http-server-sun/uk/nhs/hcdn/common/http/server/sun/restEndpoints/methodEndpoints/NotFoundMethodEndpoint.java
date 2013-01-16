/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.Headers;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;

import static uk.nhs.hcdn.common.http.ResponseCode.NotFoundResponseCode;

public final class NotFoundMethodEndpoint extends AbstractBodylessMethodEndpoint<ResourceStateSnapshot>
{
	@NotNull
	public static final MethodEndpoint<?> NotFoundMethodEndpointInstance = new NotFoundMethodEndpoint();

	private NotFoundMethodEndpoint()
	{
	}

	@Override
	protected void validateRequestHeaders(@NotNull final Headers requestHeaders)
	{
	}

	@Override
	protected int addResponseHeaders(@NotNull final Headers responseHeaders)
	{
		return NotFoundResponseCode;
	}
}
