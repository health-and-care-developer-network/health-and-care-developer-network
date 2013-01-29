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

package uk.nhs.hdn.common.http.server.sun.restEndpoints.clientError4xxs;

import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.exceptions.AbstractRethrowableException;
import uk.nhs.hdn.common.http.ResponseCode;

import java.io.IOException;
import java.io.OutputStream;

import static uk.nhs.hdn.common.CharsetHelper.Utf8;
import static uk.nhs.hdn.common.GregorianCalendarHelper.toRfc2822Form;
import static uk.nhs.hdn.common.GregorianCalendarHelper.utcNow;
import static uk.nhs.hdn.common.http.ContentTypeWithCharacterSet.TextContentTypeUtf8;
import static uk.nhs.hdn.common.http.server.sun.helpers.ResponseHeadersHelper.withEntityHeaders;
import static uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.MethodEndpoint.CacheControlHeaderValueMaximum;
import static uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.MethodEndpoint.ExpiresHeaderValueMaximum;

public abstract class AbstractClientError4xxException extends AbstractRethrowableException implements ClientError4xx
{
	private static final int CarriageReturn = (int) '\r';
	private static final int LineFeed = (int) '\n';

	@NotNull
	private final String sanitisedClientErrorMessage;
	private final int responseCode;

	protected AbstractClientError4xxException(@NotNull final String sanitisedClientErrorMessage, @ResponseCode final int responseCode, @NotNull final String message)
	{
		super(message);
		this.sanitisedClientErrorMessage = sanitisedClientErrorMessage;
		this.responseCode = responseCode;
	}

	protected AbstractClientError4xxException(@NotNull final String sanitisedClientErrorMessage, @ResponseCode final int responseCode, @NotNull final String message, @NotNull final Exception cause)
	{
		super(message, cause);
		this.sanitisedClientErrorMessage = sanitisedClientErrorMessage;
		this.responseCode = responseCode;
	}

	@Override
	public final void write4xxResponse(@NotNull final HttpExchange httpExchange) throws IOException
	{
		withEntityHeaders(httpExchange, TextContentTypeUtf8, CacheControlHeaderValueMaximum, ExpiresHeaderValueMaximum, toRfc2822Form(utcNow()));
		final byte[] bytes = sanitisedClientErrorMessage.getBytes(Utf8);
		httpExchange.sendResponseHeaders(responseCode, (long) bytes.length + 2L);
		try (OutputStream responseBody = httpExchange.getResponseBody())
		{
			responseBody.write(bytes);
			responseBody.write(CarriageReturn);
			responseBody.write(LineFeed);
		}
		httpExchange.close();
	}
}
