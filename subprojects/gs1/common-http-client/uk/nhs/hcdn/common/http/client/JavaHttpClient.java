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

package uk.nhs.hcdn.common.http.client;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hcdn.common.http.ResponseCode;
import uk.nhs.hcdn.common.http.ResponseCodeOutsideOfValidRangeInvalidException;
import uk.nhs.hcdn.common.http.ResponseCodeRange;
import uk.nhs.hcdn.common.http.client.connectionConfigurations.ChunkedUploadsConnectionConfiguration;
import uk.nhs.hcdn.common.http.client.connectionConfigurations.CombinedConnectionConfiguration;
import uk.nhs.hcdn.common.http.client.connectionConfigurations.ConnectionConfiguration;
import uk.nhs.hcdn.common.http.client.connectionConfigurations.TcpConnectionConfiguration;
import uk.nhs.hcdn.common.http.client.exceptions.CorruptResponseException;
import uk.nhs.hcdn.common.http.client.exceptions.CouldNotConnectHttpException;
import uk.nhs.hcdn.common.http.client.exceptions.UnacceptableResponseException;
import uk.nhs.hcdn.common.http.client.getHttpResponseUsers.GetHttpResponseUser;
import uk.nhs.hcdn.common.http.client.headHttpResponseUsers.HeadHttpResponseUser;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.tuples.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static uk.nhs.hcdn.common.VariableArgumentsHelper.copyOf;
import static uk.nhs.hcdn.common.http.ResponseCode.NoContentResponseCode;
import static uk.nhs.hcdn.common.http.ResponseCodeRange.Successful2xx;
import static uk.nhs.hcdn.common.http.ResponseCodeRange.responseCodeRange;
import static uk.nhs.hcdn.common.http.client.connectionConfigurations.HttpMethodConnectionConfiguration.GET;
import static uk.nhs.hcdn.common.http.client.connectionConfigurations.HttpMethodConnectionConfiguration.PUT;
import static uk.nhs.hcdn.common.http.client.connectionConfigurations.TcpConnectionConfiguration.tcpConnectionConfiguration;
import static uk.nhs.hcdn.common.inputStreams.EmptyInputStream.EmptyInputStreamInstance;

public final class JavaHttpClient extends AbstractToString implements HttpClient
{
	@NotNull
	private final URL httpUrl;
	@NotNull
	private final Pair<String, String>[] httpHeaders;

	private final CombinedConnectionConfiguration getConfiguration;
	private final CombinedConnectionConfiguration putConfiguration;

	@SuppressWarnings("FeatureEnvy")
	@SafeVarargs
	public JavaHttpClient(@NotNull final URL httpUrl, @SuppressWarnings("TypeMayBeWeakened") @NotNull final ChunkedUploadsConnectionConfiguration supportsChunkedUploads, @NotNull final Pair<String, String>... httpHeaders)
	{
		this.httpUrl = httpUrl;
		this.httpHeaders = copyOf(httpHeaders);
		final TcpConnectionConfiguration tcpConnectionConfiguration = tcpConnectionConfiguration(true, 10 * 1000, 10 * 1000);
		getConfiguration = tcpConnectionConfiguration.with(GET);
		putConfiguration = tcpConnectionConfiguration.with(PUT).with(supportsChunkedUploads);
	}

	@Override
	public void head(@NotNull final HeadHttpResponseUser headHttpResponseUser) throws CouldNotConnectHttpException, UnacceptableResponseException, CorruptResponseException
	{
		final HttpURLConnection httpConnection = connectedHttpConnection();

		try
		{
			final int responseCode = responseCode(httpConnection);
			final ResponseCodeRange responseCodeRange = validateResponseCodeRange(responseCode);
			final long contentLengthLong = httpConnection.getContentLengthLong();

			headHttpResponseUser.response(responseCode, responseCodeRange, httpConnection.getDate(), httpConnection.getExpiration(), contentLengthLong, httpConnection.getContentType(), httpConnection.getContentEncoding());
		}
		finally
		{
			httpConnection.disconnect();
		}
	}

	@Override
	@NotNull
	public <V> V get(@NotNull final GetHttpResponseUser<V> getHttpResponseUser) throws CouldNotConnectHttpException, UnacceptableResponseException, CorruptResponseException
	{
		final HttpURLConnection httpConnection = connectedHttpConnection();

		// PUT, POST here

		try
		{
			final int responseCode = responseCode(httpConnection);
			final ResponseCodeRange responseCodeRange = validateResponseCodeRange(responseCode);
			final long contentLengthLong = httpConnection.getContentLengthLong();
			final InputStream inputStream = inputStream(httpConnection, responseCode, responseCodeRange, contentLengthLong);
			try
			{
				return getHttpResponseUser.response(responseCode, responseCodeRange, httpConnection.getDate(), httpConnection.getExpiration(), contentLengthLong, httpConnection.getContentType(), httpConnection.getContentEncoding(), inputStream);
			}
			finally
			{
				try
				{
					inputStream.close();
				}
				catch (IOException ignored)
				{
				}
			}
		}
		finally
		{
			httpConnection.disconnect();
		}
	}

	private static ResponseCodeRange validateResponseCodeRange(final int responseCode) throws CorruptResponseException
	{
		final ResponseCodeRange responseCodeRange;
		try
		{
			responseCodeRange = responseCodeRange(responseCode);
		}
		catch (ResponseCodeOutsideOfValidRangeInvalidException e)
		{
			throw new CorruptResponseException(e);
		}
		return responseCodeRange;
	}

	private HttpURLConnection connectedHttpConnection() throws CouldNotConnectHttpException
	{
		final HttpURLConnection httpConnection = newHttpConnection(getConfiguration);

		try
		{
			httpConnection.connect();
		}
		catch (IOException e)
		{
			// seems that 400 / 500 happens here...
			throw new CouldNotConnectHttpException(httpUrl, e);
		}
		return httpConnection;
	}

	private static int responseCode(final HttpURLConnection httpConnection) throws CorruptResponseException
	{
		final int responseCode;
		try
		{
			responseCode = httpConnection.getResponseCode();
		}
		catch (IOException e)
		{
			throw new CorruptResponseException("could not obtain response code", e);
		}
		return responseCode;
	}

	private static InputStream inputStream(final HttpURLConnection httpConnection, @ResponseCode final int responseCode, final ResponseCodeRange responseCodeRange, final long contentLengthLong) throws CorruptResponseException
	{
		final InputStream inputStream;
		if (responseCode == NoContentResponseCode || contentLengthLong == 0L)
		{
			inputStream = EmptyInputStreamInstance;
		}
		else if (responseCodeRange == Successful2xx)
		{
			try
			{
				inputStream = httpConnection.getInputStream();
			}
			catch (IOException e)
			{
				throw new CorruptResponseException("could not obtain response body", e);
			}
		}
		else
		{
			inputStream = httpConnection.getErrorStream();
		}
		return inputStream;
	}

	private HttpURLConnection newHttpConnection(@NotNull final ConnectionConfiguration connectionConfiguration)
	{
		final HttpURLConnection httpConnection;
		try
		{
			httpConnection = (HttpURLConnection) httpUrl.openConnection();
		}
		catch (IOException e)
		{
			throw new ShouldNeverHappenException(e);
		}
		connectionConfiguration.configure(httpConnection);
		for (final Pair<String, String> httpHeader : httpHeaders)
		{
			httpConnection.setRequestProperty(httpHeader.a, httpHeader.b);
		}
		return httpConnection;
	}
}
