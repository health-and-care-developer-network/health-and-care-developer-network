/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.Headers;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.Method;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStates.ResourceStateSnapshot;

import java.io.StringWriter;
import java.util.Set;

import static uk.nhs.hcdn.common.http.ResponseHeader.AllowHeaderName;

public abstract class AbstractOptionsMethodEndpoint<R extends ResourceStateSnapshot> extends AbstractBodylessMethodEndpoint<R>
{
	private static final char Comma = ',';

	private final int responseCode;

	@NotNull
	private final String allowHeaderValue;

	protected AbstractOptionsMethodEndpoint(final int responseCode, @SuppressWarnings("TypeMayBeWeakened") @Method @NotNull final Set<String> supportedMethodEndpoints)
	{
		this.responseCode = responseCode;
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
	protected final int addResponseHeaders(@NotNull final Headers responseHeaders)
	{
		responseHeaders.set(AllowHeaderName, allowHeaderValue);
		return responseCode;
	}
}
