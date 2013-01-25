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

package uk.nhs.hcdn.dts.domain.identifiers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.dts.domain.statusRecords.dateTime.KnownDateTime;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class DtsIdentifier extends AbstractIdentifier
{
	private static final int MaximumCharacters = 100;

	@NotNull
	public static final DtsIdentifier UnknownDtsIdentifier = new DtsIdentifier("");

	public DtsIdentifier(@NonNls @NotNull final String value)
	{
		super(value, MaximumCharacters);
		// it seems there is a format for these...
	}

	public DtsIdentifier(@NonNls @NotNull final String serverName, @NotNull final KnownDateTime dateTime, @NotNull @NonNls final String xxxx)
	{
		super(serverName + '-' + dateTime.asYYYYMMDDhhmmss() + xxxx, MaximumCharacters);
		if (serverName.contains("-"))
		{
			throw new IllegalArgumentException(format(ENGLISH, "Server name %1$s contains a hyphen", serverName));
		}
		dateTime.asYYYYMMDDhhmmss();
		if (xxxx.length() != 4)
		{
			throw new IllegalArgumentException("DTSUNC-02 defines the format for a DTS Identifier");
		}
	}
}
