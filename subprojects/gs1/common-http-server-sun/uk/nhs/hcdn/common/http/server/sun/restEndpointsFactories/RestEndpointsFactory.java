/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpointsFactories;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.RestEndpoint;

import java.io.File;

public interface RestEndpointsFactory
{
	@NotNull
	RestEndpoint[] restEndpoints(@NotNull final File dataPath) throws CouldNotCreateRestEndpointsException;
}
