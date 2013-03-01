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

package uk.nhs.hdn.common.http;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class UrlHelper
{
	public static final char HttpPortNumber = (char) 80;
	public static final char HttpsPortNumber = (char) 443;

	private UrlHelper()
	{
	}

	public static char commonPortNumber(final boolean isHttps)
	{
		return isHttps ? HttpsPortNumber : HttpPortNumber;
	}

	@NotNull
	public static URL toUrl(final boolean isHttps, @NonNls @NotNull final String server, final char port, @NonNls @NotNull final String rawPath)
	{
		if ((int) port == 0)
		{
			throw new IllegalArgumentException("port can not be zero");
		}
		@NonNls final String protocol = isHttps ? "https" : "http";
		final String urlString = format(ENGLISH, "%1$s://%2$s:%3$s%4$s", protocol, server, Integer.toString((int) port), rawPath);
		try
		{
			return new URL(urlString);
		}
		catch (MalformedURLException e)
		{
			throw new IllegalArgumentException("server or rawPath malformed", e);
		}
	}
}
