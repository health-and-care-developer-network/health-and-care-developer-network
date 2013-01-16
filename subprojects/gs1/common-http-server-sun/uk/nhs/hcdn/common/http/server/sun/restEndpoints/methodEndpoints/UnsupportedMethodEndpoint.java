/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.Headers;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.Method;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStates.ResourceStateSnapshot;

import java.util.Set;

import static uk.nhs.hcdn.common.http.ResponseCode.MethodNotAllowedResponseCode;

public final class UnsupportedMethodEndpoint<R extends ResourceStateSnapshot> extends AbstractOptionsMethodEndpoint<R>
{
	public UnsupportedMethodEndpoint(@SuppressWarnings("TypeMayBeWeakened") @Method @NotNull final Set<String> supportedMethodEndpoints)
	{
		super(MethodNotAllowedResponseCode, supportedMethodEndpoints);
	}

	@Override
	protected void validateRequestHeaders(@NotNull final Headers requestHeaders)
	{
	}
}
