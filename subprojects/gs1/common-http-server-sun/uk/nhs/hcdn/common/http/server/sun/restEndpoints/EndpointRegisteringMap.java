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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.http.Method;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints.MethodEndpoint;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;

import java.util.HashMap;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.common.http.Method.OPTIONS;

@SuppressWarnings("SerializableHasSerializationMethods")
public final class EndpointRegisteringMap<R extends ResourceStateSnapshot> extends HashMap<String, MethodEndpoint<R>>
{
	private static final long serialVersionUID = 7459132099441033256L;

	public EndpointRegisteringMap()
	{
		super(10);
	}

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
}
