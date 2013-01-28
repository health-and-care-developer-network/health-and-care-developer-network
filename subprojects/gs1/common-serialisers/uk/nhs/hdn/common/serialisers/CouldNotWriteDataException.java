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

package uk.nhs.hdn.common.serialisers;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class CouldNotWriteDataException extends AbstractDataException
{
	public CouldNotWriteDataException(@NotNull final IOException cause)
	{
		super(message(cause), cause);
	}

	public CouldNotWriteDataException(@NotNull final CouldNotEncodeDataException cause)
	{
		super(message(cause), cause);
	}

	private static String message(final Exception cause)
	{
		return format(ENGLISH, "Could not write data due to %1$s (%2$s)", cause.getClass().getSimpleName(), cause.getMessage());
	}
}
