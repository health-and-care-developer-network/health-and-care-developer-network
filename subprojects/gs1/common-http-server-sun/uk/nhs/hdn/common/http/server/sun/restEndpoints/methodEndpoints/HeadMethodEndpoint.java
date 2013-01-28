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

package uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints;

import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ResourceContent;

import java.io.IOException;

import static uk.nhs.hdn.common.http.Method.HEAD;

public final class HeadMethodEndpoint<R extends ResourceStateSnapshot> extends AbstractHeadOrGetRegisterableMethodEndpoint<R>
{
	@NotNull
	private static final RegisterableMethodEndpoint<?> Instance = new HeadMethodEndpoint<>();

	@SuppressWarnings({"unchecked", "MethodNamesDifferingOnlyByCase"})
	@NotNull
	public static <R extends ResourceStateSnapshot> RegisterableMethodEndpoint<R> headMethodEndpoint()
	{
		return (RegisterableMethodEndpoint<R>) Instance;
	}

	private HeadMethodEndpoint()
	{
		super(HEAD);
	}

	@Override
	protected void send(@NotNull final HttpExchange httpExchange, @NotNull final ResourceContent content) throws IOException
	{
		content.head(httpExchange);
	}
}
