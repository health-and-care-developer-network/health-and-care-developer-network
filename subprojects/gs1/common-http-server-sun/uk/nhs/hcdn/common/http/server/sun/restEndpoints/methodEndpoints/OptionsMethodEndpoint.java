/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.Headers;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.Method;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;

import java.util.HashSet;
import java.util.Set;

import static uk.nhs.hcdn.common.http.Method.OPTIONS;
import static uk.nhs.hcdn.common.http.RequestHeader.*;
import static uk.nhs.hcdn.common.http.server.sun.helpers.RequestHeadersHelper.validateHeadersOmitted;
import static uk.nhs.hcdn.common.http.ResponseCode.NoContentResponseCode;

public final class OptionsMethodEndpoint<R extends ResourceStateSnapshot> extends AbstractOptionsMethodEndpoint<R>
{
	@SuppressWarnings("ClassExtendsConcreteCollection")
	public OptionsMethodEndpoint(@SuppressWarnings("TypeMayBeWeakened") @Method @NotNull final Set<String> supportedMethodEndpoints)
	{
		super(NoContentResponseCode, new HashSet<String>(supportedMethodEndpoints)
		{{
			add(OPTIONS);
		}});
	}

	@Override
	protected void validateRequestHeaders(@NotNull final Headers requestHeaders) throws BadRequestException
	{
		validateHeadersOmitted(requestHeaders, DateHeaderName, ExpectHeaderName, IfModifiedSinceHeaderName, IfUnmodifiedSinceHeaderName, RangeHeaderName, IfRangeHeaderName, IfMatchHeaderName, IfNoneMatchHeaderName);
	}
}
