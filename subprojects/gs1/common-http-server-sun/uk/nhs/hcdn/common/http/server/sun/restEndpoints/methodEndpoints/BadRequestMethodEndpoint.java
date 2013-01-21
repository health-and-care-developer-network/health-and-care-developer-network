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

package uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.clientError4xxs.BadRequestException;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.io.IOException;

public final class BadRequestMethodEndpoint<R extends ResourceStateSnapshot> extends AbstractToString implements MethodEndpoint<R>
{
	@NotNull
	private final BadRequestException cause;

	public BadRequestMethodEndpoint(@NonNls @NotNull final String santisedMessage)
	{
		this(new BadRequestException(santisedMessage));
	}

	public BadRequestMethodEndpoint(@NotNull final BadRequestException cause)
	{
		this.cause = cause;
	}

	@Override
	public void handle(@NotNull final String rawRelativeUriPath, @Nullable final String rawQueryString, @NotNull final HttpExchange httpExchange, @NotNull final R resourceStateSnapshot) throws IOException
	{
		cause.write4xxResponse(httpExchange);
	}
}
