/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.Headers;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStates.ResourceStateSnapshot;

import static uk.nhs.hcdn.common.http.ResponseCode.BadRequestResponseCode;

public final class BadRequestMethodEndpoint<R extends ResourceStateSnapshot> extends AbstractBodylessMethodEndpoint<R>
{
	@NotNull
	private static final MethodEndpoint<?> BadRequestMethodEndpointInstance = new BadRequestMethodEndpoint();

	@SuppressWarnings({"unchecked", "MethodNamesDifferingOnlyByCase"})
	@NotNull
	public static <R extends ResourceStateSnapshot> MethodEndpoint<R> badRequestMethodEndpoint()
	{
		return (MethodEndpoint<R>) BadRequestMethodEndpointInstance;
	}

	private BadRequestMethodEndpoint()
	{
	}

	@Override
	protected void validateRequestHeaders(@NotNull final Headers requestHeaders)
	{
	}

	@Override
	protected int addResponseHeaders(@NotNull final Headers responseHeaders)
	{
		return BadRequestResponseCode;
	}
}
