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

package uk.nhs.hcdn.common.http.client.getHttpResponseUsers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.MillisecondsSince1970;
import uk.nhs.hcdn.common.http.ResponseCode;
import uk.nhs.hcdn.common.http.client.UnacceptableResponseException;
import uk.nhs.hcdn.common.parsers.json.InvalidJsonException;
import uk.nhs.hcdn.common.parsers.json.SchemaUsingParser;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.common.http.ResponseCode.NoContentResponseCode;
import static uk.nhs.hcdn.common.http.ResponseCode.OkResponseCode;

public final class JsonGetHttpResponseUser<V> extends AbstractToString implements GetHttpResponseUser<V[]>
{
	@NotNull @NonNls
	private static final String Identity = "identity";
	@NotNull @NonNls
	private static final String JsonUtf8 = "application/json;charset=utf-8";
	private static final Charset Utf8 = Charset.forName("UTF-8");

	@NotNull
	private final SchemaUsingParser<V> schemaUsingParser;

	public JsonGetHttpResponseUser(@NotNull final SchemaUsingParser<V> schemaUsingParser)
	{
		this.schemaUsingParser = schemaUsingParser;
	}

	@Override
	@NotNull
	public V[] response(@ResponseCode final int responseCode, @NotNull final String responseMessage, @MillisecondsSince1970 final long date, @MillisecondsSince1970 final long expires, final long contentLengthOrMinusOneIfNoneSupplied, @Nullable final String contentType, @NonNls @Nullable final String contentEncoding, @NotNull final InputStream inputStream) throws UnacceptableResponseException
	{
		final Charset charset = guardResponseIsValid(responseCode, contentLengthOrMinusOneIfNoneSupplied, contentType, contentEncoding);

		final InputStreamReader reader = new InputStreamReader(inputStream, charset);
		try
		{
			return schemaUsingParser.parse(reader);
		}
		catch (InvalidJsonException e)
		{
			throw new UnacceptableResponseException("JSON was invalid", e);
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (IOException ignored)
			{
			}
		}
	}

	@SuppressWarnings("MethodWithMoreThanThreeNegations")
	private static Charset guardResponseIsValid(final int responseCode, final long contentLengthOrMinusOneIfNoneSupplied, final String contentType, final String contentEncoding) throws UnacceptableResponseException
	{
		if (responseCode >= 400 && responseCode < 500)
		{
			throw new UnacceptableResponseException("bad response (400)");
		}
		if (responseCode >= 500 && responseCode < 600)
		{
			throw new UnacceptableResponseException("bad response (500)");
		}
		if (responseCode == NoContentResponseCode || contentLengthOrMinusOneIfNoneSupplied == 0L)
		{
			throw new UnacceptableResponseException("no content");
		}

		if (responseCode != OkResponseCode)
		{
			throw new UnacceptableResponseException("unimplemented response code");
		}

		if (contentEncoding != null)
		{
			if (!contentEncoding.isEmpty() && !Identity.equalsIgnoreCase(contentEncoding))
			{
				throw new UnacceptableResponseException("compressed content encodings are not supported yet");
			}
		}

		if (contentType == null)
		{
			throw new UnacceptableResponseException("no Content-Type supplied");
		}
		// Hideous. But until we have to parse anything other than JSON UTF-8, there's no point writing a content-type parser...
		if (!contentType.toLowerCase(ENGLISH).replace(" ", "").equals(JsonUtf8))
		{
			throw new UnacceptableResponseException("content is not appliction/json;charset=utf-8");
		}

		return Utf8;
	}
}
