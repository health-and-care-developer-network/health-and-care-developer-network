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

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.io.IOException;
import java.net.Authenticator;

public abstract class AbstractRestEndpoint extends AbstractToString implements RestEndpoint
{
	@NotNull
	private final String absoluteRawPath;

	@Nullable
	private final Authenticator authenticator;

	protected AbstractRestEndpoint(@NonNls @NotNull final String absoluteRawPath, @Nullable final Authenticator authenticator)
	{
		this.absoluteRawPath = absoluteRawPath;
		this.authenticator = authenticator;
	}

	@Override
	public final void register(@NotNull final HttpServer httpServer)
	{
		final HttpContext context = httpServer.createContext(absoluteRawPath, this);
		if (authenticator != null)
		{
			context.setAuthenticator(null);
		}
	}

	@SuppressWarnings("AbstractMethodOverridesAbstractMethod") // to add @NotNull annotation
	@Override
	public abstract void handle(@NotNull final HttpExchange httpExchange) throws IOException;
}
