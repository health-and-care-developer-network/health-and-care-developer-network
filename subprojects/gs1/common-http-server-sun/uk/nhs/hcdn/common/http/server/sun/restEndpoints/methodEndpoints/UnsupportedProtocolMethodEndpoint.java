/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.Headers;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;

import static uk.nhs.hcdn.common.http.ResponseCode.HttpVersionNotSupportedResponseCode;

// A body SHOULD be provided. We violate this rule - it is neither useful nor sensible
public final class UnsupportedProtocolMethodEndpoint<R extends ResourceStateSnapshot> extends AbstractBodylessMethodEndpoint<R>
{
	@NotNull
	private static final MethodEndpoint<?> UnsupportedProtocolMethodEndpointInstance = new UnsupportedProtocolMethodEndpoint();

	@SuppressWarnings({"unchecked", "MethodNamesDifferingOnlyByCase"})
	@NotNull
	public static <R extends ResourceStateSnapshot> MethodEndpoint<R> unsupportedProtocolMethodEndpoint()
	{
		return (MethodEndpoint<R>) UnsupportedProtocolMethodEndpointInstance;
	}

	private UnsupportedProtocolMethodEndpoint()
	{
	}

	@Override
	protected void validateRequestHeaders(@NotNull final Headers requestHeaders)
	{
	}

	@Override
	protected int addResponseHeaders(@NotNull final Headers responseHeaders)
	{
		return HttpVersionNotSupportedResponseCode;
	}
}
