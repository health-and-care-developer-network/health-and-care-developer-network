/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ResourceContent;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;

import java.io.IOException;

public abstract class AbstractHeadMethodEndpoint<R extends ResourceStateSnapshot> extends AbstractHeadOrGetMethodEndpoint<R>
{
	@Override
	protected final void send(@NotNull final HttpExchange httpExchange, @NotNull final ResourceContent content) throws IOException
	{
		content.head(httpExchange);
	}
}
