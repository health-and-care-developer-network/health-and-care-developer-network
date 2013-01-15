/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.Headers;
import org.jetbrains.annotations.NotNull;

import java.io.StringWriter;
import java.util.GregorianCalendar;
import java.util.Set;

import static uk.nhs.hcdn.common.http.response.ResponseCode.MethodNotAllowedResponseCode;
import static uk.nhs.hcdn.common.http.response.ResponseHeader.AllowHeaderName;

public final class AbstractOptionsMethodEndpoint extends AbstractBodylessMethodEndpoint
{
	private static final char Comma = ',';

	@NotNull
	private final String allowHeaderValue;

	@NotNull
	private final GregorianCalendar lastModified;

	public AbstractOptionsMethodEndpoint(@SuppressWarnings("TypeMayBeWeakened") @NotNull final Set<String> supportedMethodEndpoints, @NotNull final GregorianCalendar lastModified)
	{
		this.lastModified = (GregorianCalendar) lastModified.clone();
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
	protected int addResponseHeaders(@NotNull final Headers responseHeaders)
	{
		responseHeaders.set(AllowHeaderName, allowHeaderValue);
		return MethodNotAllowedResponseCode;
	}
}
