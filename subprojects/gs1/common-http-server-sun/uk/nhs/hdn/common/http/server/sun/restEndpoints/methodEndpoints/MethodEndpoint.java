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
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.clientError4xxs.BadRequestException;
import uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;

import java.io.IOException;

public interface MethodEndpoint<R extends ResourceStateSnapshot>
{
	@NotNull
	String ServerHeaderValue = "hdn";

	@NotNull
	String ContentLanguageHeaderValue = "en";

	@NotNull
	String StrictTransportSecurityHeaderValueMaximum = "max-age=315360000";

	@NotNull
	String CacheControlHeaderValueMaximum = "public, max-age=315360000";

	@NotNull
	String CacheControlHeaderValueOneHour = "public, max-age=3600";

	@NotNull
	String ExpiresHeaderValueMaximum = "Thu, 31 Dec 2037 23:55:55 GMT";
	long NoContentBodyMagicValue = -1L;

	void handle(@NotNull final String rawRelativeUriPath, @Nullable final String rawQueryString, @NotNull final HttpExchange httpExchange, @NotNull final R resourceStateSnapshot) throws IOException, BadRequestException;
}
