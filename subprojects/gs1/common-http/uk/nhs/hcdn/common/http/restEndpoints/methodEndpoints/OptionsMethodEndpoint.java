/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.restEndpoints.methodEndpoints;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static uk.nhs.hcdn.common.http.response.ResponseCode.MethodNotAllowedResponseCode;

public final class OptionsMethodEndpoint extends AbstractOptionsMethodEndpoint
{
	public OptionsMethodEndpoint(@SuppressWarnings("TypeMayBeWeakened") @NotNull final Set<String> supportedMethodEndpoints)
	{
		super(MethodNotAllowedResponseCode, supportedMethodEndpoints);
}
