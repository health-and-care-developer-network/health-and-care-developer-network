/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractWithoutAuthenticationRestEndpoint extends AbstractRestEndpoint
{
	protected AbstractWithoutAuthenticationRestEndpoint(@NonNls @NotNull final String absoluteRawPath)
	{
		super(absoluteRawPath, null);
	}
}
