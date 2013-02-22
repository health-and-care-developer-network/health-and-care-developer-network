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
import uk.nhs.hdn.common.reflection.toString.AbstractToString;

import java.net.HttpURLConnection;
import java.util.Arrays;

import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;

public final class CombinedConnectionConfiguration extends AbstractToString implements ConnectionConfiguration
{
	@NotNull
	private final ConnectionConfiguration[] connectionConfigurations;

	public CombinedConnectionConfiguration(@NotNull final ConnectionConfiguration... connectionConfigurations)
	{
		this.connectionConfigurations = copyOf(connectionConfigurations);
	}

	@Override
	public void configure(@NotNull final HttpURLConnection httpConnection)
	{
		for (final ConnectionConfiguration connectionConfiguration : connectionConfigurations)
		{
			connectionConfiguration.configure(httpConnection);
		}
	}

	@NotNull
	public CombinedConnectionConfiguration with(@NotNull final ConnectionConfiguration connectionConfiguration)
	{
		final int length = connectionConfigurations.length;
		final ConnectionConfiguration[] copy = Arrays.copyOf(connectionConfigurations, length + 1);
		copy[length] = connectionConfiguration;
		return new CombinedConnectionConfiguration(copy);
	}
}
