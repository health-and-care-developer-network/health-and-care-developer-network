/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents;

import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface ResourceContent
{
	void head(@NotNull final HttpExchange httpExchange) throws IOException;

	void get(@NotNull final HttpExchange httpExchange) throws IOException;

	void setHeaders(@NotNull final HttpExchange httpExchange, @NotNull final String lastModifiedInRfc2822Form);
}
