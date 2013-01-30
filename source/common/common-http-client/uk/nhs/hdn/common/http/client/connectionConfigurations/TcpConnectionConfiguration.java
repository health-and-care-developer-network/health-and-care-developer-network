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

package uk.nhs.hdn.common.http.client.connectionConfigurations;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.Milliseconds;

import java.net.HttpURLConnection;

public final class TcpConnectionConfiguration implements ConnectionConfiguration
{
	@NotNull
	public static TcpConnectionConfiguration tcpConnectionConfiguration(final boolean useCaches, @Milliseconds final int connectTimeout, @Milliseconds final int readTimeout)
	{
		return new TcpConnectionConfiguration(useCaches, connectTimeout, readTimeout);
	}

	private final boolean useCaches;
	private final int connectTimeout;
	private final int readTimeout;

	public TcpConnectionConfiguration(final boolean useCaches, @Milliseconds final int connectTimeout, @Milliseconds final int readTimeout)
	{
		this.useCaches = useCaches;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
	}

	@Override
	public void configure(@NotNull final HttpURLConnection httpConnection)
	{
		httpConnection.setDefaultUseCaches(useCaches);
		httpConnection.setUseCaches(useCaches);
		httpConnection.setConnectTimeout(connectTimeout);
		httpConnection.setReadTimeout(readTimeout);
		httpConnection.setAllowUserInteraction(false);
	}

	@NotNull
	public CombinedConnectionConfiguration with(@NotNull final ConnectionConfiguration connectionConfiguration)
	{
		return new CombinedConnectionConfiguration(this, connectionConfiguration);
	}
}
