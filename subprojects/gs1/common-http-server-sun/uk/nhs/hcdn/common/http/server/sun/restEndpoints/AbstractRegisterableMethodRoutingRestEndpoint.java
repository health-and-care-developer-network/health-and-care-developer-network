/*
 * © Crown Copyright 2013
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints.MethodEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints.OptionsMethodEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints.UnsupportedMethodEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;

import java.net.Authenticator;
import java.util.HashMap;
import java.util.Map;

import static uk.nhs.hcdn.common.http.Method.OPTIONS;

public abstract class AbstractRegisterableMethodRoutingRestEndpoint<R extends ResourceStateSnapshot> extends AbstractMethodRoutingRestEndpoint<R>
{
	@NotNull
	private final Map<String, MethodEndpoint<R>> methodEndpointsRegister;

	@NotNull @ExcludeFromToString
	private final UnsupportedMethodEndpoint<R> unsupportedMethodEndpoint;

	protected AbstractRegisterableMethodRoutingRestEndpoint(@NonNls @NotNull final String absoluteRawPath, @Nullable final Authenticator authenticator)
	{
		super(absoluteRawPath, authenticator);
		final Map<String, MethodEndpoint<R>> register = new EndpointRegisteringMap<R>();
		registerMethodEndpointsExcludingOptions(register);
		methodEndpointsRegister = new HashMap<>(register);
		methodEndpointsRegister.put(OPTIONS, new OptionsMethodEndpoint<R>(register.keySet()));
		unsupportedMethodEndpoint = new UnsupportedMethodEndpoint<>(methodEndpointsRegister.keySet());
	}

	@NotNull
	@Override
	protected final MethodEndpoint<R> methodEndpoint(@NotNull final String method)
	{
		@Nullable final MethodEndpoint<R> methodEndpoint = methodEndpointsRegister.get(method);
		if (methodEndpoint == null)
		{
			return unsupportedMethodEndpoint;
		}
		return methodEndpoint;
	}

	protected abstract void registerMethodEndpointsExcludingOptions(@NotNull final Map<String, MethodEndpoint<R>> methodEndpointsRegister);

}
