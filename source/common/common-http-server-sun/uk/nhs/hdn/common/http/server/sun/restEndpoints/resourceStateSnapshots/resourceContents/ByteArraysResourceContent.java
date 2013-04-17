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

package uk.nhs.hdn.common.http.server.sun.restEndpoints.resourceStateSnapshots.resourceContents;

import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.http.ContentTypeWithCharacterSet;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.reflection.toString.ExcludeFromToString;

import java.io.IOException;
import java.io.OutputStream;

import static java.lang.System.currentTimeMillis;
import static uk.nhs.hdn.common.CharsetHelper.Utf8;
import static uk.nhs.hdn.common.GregorianCalendarHelper.toRfc2822Form;
import static uk.nhs.hdn.common.GregorianCalendarHelper.utc;
import static uk.nhs.hdn.common.http.ContentTypeWithCharacterSet.JsonContentTypeUtf8;
import static uk.nhs.hdn.common.http.ResponseCode.OkResponseCode;
import static uk.nhs.hdn.common.http.server.sun.helpers.ResponseHeadersHelper.withEntityHeaders;
import static uk.nhs.hdn.common.http.server.sun.restEndpoints.methodEndpoints.MethodEndpoint.CacheControlHeaderValueOneHour;

public final class ByteArraysResourceContent extends AbstractToString implements ResourceContent
{
	private static final byte[] OpenBraceBytes = {(byte) '{'};
	private static final byte[] EndingBytes = ");\n".getBytes(Utf8);

	@NotNull
	public static ByteArraysResourceContent jsonpByteArraysResourceContent(@NotNull final String jsonpFunctionName, @NotNull final ByteArrayResourceContent jsonUtf8Content)
	{
		final byte[] bytes = jsonpFunctionName.getBytes(Utf8);
		return new ByteArraysResourceContent(JsonContentTypeUtf8, bytes, OpenBraceBytes, jsonUtf8Content.content(), EndingBytes);
	}

	private static final long OneHourInMilliseconds = 60 * 60 * 1000;

	@NotNull
	@ContentTypeWithCharacterSet
	@NonNls
	private final String contentType;

	@NotNull @ExcludeFromToString
	private final byte[][] contents;

	private final long length;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public ByteArraysResourceContent(@ContentTypeWithCharacterSet @NonNls @NotNull final String contentType, @NotNull final byte[]... contents)
	{
		this.contentType = contentType;
		this.contents = contents;
		long lengthAccumulator = 0L;
		for (final byte[] content : contents)
		{
			lengthAccumulator += (long) content.length;
		}
		this.length = lengthAccumulator;
	}

	@Override
	public void head(@NotNull final HttpExchange httpExchange) throws IOException
	{
		httpExchange.sendResponseHeaders(OkResponseCode, length);
	}

	@Override
	public void get(@NotNull final HttpExchange httpExchange) throws IOException
	{
		head(httpExchange);
		try (OutputStream responseBody = httpExchange.getResponseBody())
		{
			for (final byte[] content : contents)
			{
				responseBody.write(content);
			}
		}
	}

	@Override
	public void setHeaders(@NotNull final HttpExchange httpExchange, @NotNull final String lastModifiedInRfc2822Form)
	{
		final String expiresOneHourFromNow = toRfc2822Form(utc(currentTimeMillis() + OneHourInMilliseconds));
		withEntityHeaders(httpExchange, contentType, CacheControlHeaderValueOneHour, expiresOneHourFromNow, lastModifiedInRfc2822Form);
	}
}
