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

package uk.nhs.hcdn.common.http.client.connectionConfigurations;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hcdn.common.http.Method;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

public enum HttpMethodConnectionConfiguration implements ConnectionConfiguration
{
	HEAD(false, false, Method.GET),
	GET(true, false, Method.GET),
	PUT(true, true, Method.GET),
	POST(true, true, Method.GET),
	;

	private final boolean input;
	private final boolean output;
	@NotNull
	private final String method;

	HttpMethodConnectionConfiguration(final boolean input, final boolean output, @NotNull @Method final String method)
	{
		this.input = input;
		this.output = output;
		this.method = method;
	}

	@Override
	public void configure(@NotNull final HttpURLConnection httpConnection)
	{
		httpConnection.setInstanceFollowRedirects(true);
		if (output)
		{
			httpConnection.setDoOutput(true);
		}

		try
		{
			httpConnection.setRequestMethod(method);
		}
		catch (ProtocolException e)
		{
			throw new ShouldNeverHappenException(e);
		}
	}
}
