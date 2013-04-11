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

package uk.nhs.hdn.crds.repository;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.String.format;
import static java.util.Locale.UK;
import static uk.nhs.hdn.common.CharsetHelper.Utf8;

public final class ResourceLoader
{
	private ResourceLoader()
	{
	}

	@NotNull
	public static String utf8TextResourceContents(@NonNls @NotNull final String resourceName)
	{
		@Nullable final InputStream resourceAsStream = ResourceLoader.class.getResourceAsStream(resourceName);
		if (resourceAsStream == null)
		{
			throw new IllegalArgumentException(format(UK, "resourceName %1$s not found", resourceName));
		}
		try
		{
			@SuppressWarnings("IOResourceOpenedButNotSafelyClosed") final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream, Utf8));
			try
			{
				final StringBuilder stringBuilder = new StringBuilder(1000);
				final char[] buffer = new char[1000];
				int read;
				//noinspection NestedAssignment
				while((read = bufferedReader.read(buffer)) != -1)
				{
					stringBuilder.append(buffer, 0, read);
				}
				return stringBuilder.toString();
			}
			catch (IOException e)
			{
				throw new IllegalStateException(e);
			}
			finally
			{
				try
				{
					bufferedReader.close();
				}
				catch (IOException ignored)
				{
				}
			}
		}
		finally
		{
			try
			{
				resourceAsStream.close();
			}
			catch (IOException ignored)
			{
			}
		}
	}
}
