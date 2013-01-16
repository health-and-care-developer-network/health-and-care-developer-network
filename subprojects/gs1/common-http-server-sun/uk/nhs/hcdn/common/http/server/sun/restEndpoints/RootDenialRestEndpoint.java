/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints.MethodEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.NotFoundResourceStateSnapshot;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;

import static uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints.NotFoundMethodEndpoint.NotFoundMethodEndpointInstance;

public final class RootDenialRestEndpoint extends AbstractMethodRoutingRestEndpoint<ResourceStateSnapshot>
{
	@NotNull
	private final NotFoundResourceStateSnapshot notFoundResourceStateSnapshot;

	public RootDenialRestEndpoint()
	{
		super("/", NoAuthentication);
		notFoundResourceStateSnapshot = new NotFoundResourceStateSnapshot();
	}

	@SuppressWarnings("unchecked")
	@NotNull
	@Override
	protected MethodEndpoint<ResourceStateSnapshot> methodEndpoint(@NotNull final String method)
	{
		return (MethodEndpoint<ResourceStateSnapshot>) NotFoundMethodEndpointInstance;
	}

	@NotNull
	@Override
	protected ResourceStateSnapshot snapshotOfStateThatIsInvariantForRequest()
	{
		return notFoundResourceStateSnapshot;
	}
}
