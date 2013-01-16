/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.restEndpoints;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.http.Method;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints.MethodEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints.OptionsMethodEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints.UnsupportedMethodEndpoint;
import uk.nhs.hcdn.common.reflection.toString.ExcludeFromToString;

import java.net.Authenticator;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.common.http.Method.OPTIONS;

public abstract class AbstractRegisterableMethodRoutingRestEndpoint<R extends ResourceStateSnapshot> extends AbstractMethodRoutingRestEndpoint<R>
{
	@NotNull
	private final Map<String, MethodEndpoint<R>> methodEndpointsRegister;

	@NotNull @ExcludeFromToString
	private final UnsupportedMethodEndpoint<R> unsupportedMethodEndpoint;

	@SuppressWarnings("ClassExtendsConcreteCollection")
	protected AbstractRegisterableMethodRoutingRestEndpoint(@NonNls @NotNull final String absoluteRawPath, @Nullable final Authenticator authenticator)
	{
		super(absoluteRawPath, authenticator);
		final Map<String, MethodEndpoint<R>> register = new HashMap<String, MethodEndpoint<R>>(10)
		{
			@Override
			@Nullable
			public MethodEndpoint<R> put(@NotNull @Method final String key, @NotNull final MethodEndpoint<R> value)
			{
				if (key.equals(OPTIONS))
				{
					throw new IllegalArgumentException("Do not register the OPTIONS method");
				}
				if (super.put(key, value) != null)
				{
					throw new IllegalArgumentException(format(ENGLISH, "Method %1$s is already registered", key));
				}
				return null;
			}
		};
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
