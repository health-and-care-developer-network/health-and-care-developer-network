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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents.ResourceContent;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.ResourceStateSnapshot;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static uk.nhs.hcdn.common.http.RequestHeader.*;
import static uk.nhs.hcdn.common.http.server.sun.helpers.RequestHeadersHelper.*;
import static uk.nhs.hcdn.common.http.ResponseCode.*;
import static uk.nhs.hcdn.common.http.server.sun.helpers.ResponseHeadersHelper.withoutEntityHeaders;

public abstract class AbstractHeadOrGetMethodEndpoint<R extends ResourceStateSnapshot> implements MethodEndpoint<R>
{

	private static final int EndOfFileOnRead = -1;

	// last modified is starting to look quite egregious
	@Override
	public final void handle(@NotNull final String rawRelativeUriPath, @Nullable final String rawQueryString, @NotNull final HttpExchange httpExchange, @NotNull final R resourceStateSnapshot) throws IOException, BadRequestException
	{
		final Headers requestHeaders = httpExchange.getRequestHeaders();
		validateNoContentLength(requestHeaders);
		validateNoRequestBody(requestHeaders, httpExchange);
		validateHeadersOmitted(requestHeaders, DateHeaderName);

		@Nullable final String expect = validateZeroOrOneInstanceOfRequestHeader(requestHeaders, ExpectHeaderName);
		if (expect != null)
		{
			httpExchange.sendResponseHeaders(ExpectationFailedResponseCode, NoContentBodyMagicValue);
			httpExchange.close();
			return;
		}

		// now if-match when etags supported

		@Nullable final Date ifUnmodifiedSince = validateZeroOrOneInstanceOfRfc2822Header(requestHeaders, IfUnmodifiedSinceHeaderName);
		if (ifUnmodifiedSince != null && resourceStateSnapshot.ifUnmodifiedSincePreconditionFailed(ifUnmodifiedSince))
		{
			withoutEntityHeaders(httpExchange, CacheControlHeaderValueMaximum);
			httpExchange.sendResponseHeaders(PreconditionFailedResponseCode, NoContentBodyMagicValue);
			httpExchange.close();
			return;
		}

		// now if-none-match when etags supported

		@Nullable final Date ifModifiedSince = validateZeroOrOneInstanceOfRfc2822Header(requestHeaders, IfModifiedSinceHeaderName);
		if (ifModifiedSince != null && !resourceStateSnapshot.ifModifiedSinceNotModified(ifModifiedSince))
		{
			withoutEntityHeaders(httpExchange, CacheControlHeaderValueMaximum);
			httpExchange.sendResponseHeaders(NotModifiedResponseCode, NoContentBodyMagicValue);
			httpExchange.close();
			return;
		}

		final ResourceContent content;
		try
		{
			content = resourceStateSnapshot.content(rawRelativeUriPath, rawQueryString);
		}
		catch (NotFoundException ignored)
		{
			httpExchange.sendResponseHeaders(NotFoundResponseCode, NoContentBodyMagicValue);
			httpExchange.close();
			return;
		}

		content.setHeaders(httpExchange, resourceStateSnapshot.lastModifiedInRfc2822Form());
		send(httpExchange, content);
		httpExchange.close();
	}

	protected abstract void send(@NotNull final HttpExchange httpExchange, @NotNull final ResourceContent content) throws IOException;

	private static void validateNoContentLength(final Headers requestHeaders) throws BadRequestException
	{
		@NonNls @Nullable final String contentLength = requestHeaders.getFirst(ContentLengthHeaderName);
		final boolean hasContent = contentLength != null && !"0".equals(contentLength);
		if (hasContent)
		{
			throw new BadRequestException("HEAD and GET should not have a non-zero Content-Length header");
		}
	}

	private static void validateNoRequestBody(final Headers requestHeaders, final HttpExchange httpExchange) throws BadRequestException
	{
		validateTransferEncodingRequestHeader(requestHeaders);

		final InputStream requestBody = httpExchange.getRequestBody();
		try
		{
			final int read = requestBody.read();
			if (read != EndOfFileOnRead)
			{
				throw new BadRequestException("HEAD and GET should not be sent a request body");
			}
		}
		catch (IOException ignored)
		{
		}
		finally
		{
			try
			{
				requestBody.close();
			}
			catch (IOException ignored)
			{
			}
		}
	}

}
