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

package uk.nhs.hcdn.common.parsers.json;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.json.jsonReaders.EndOfFileException;
import uk.nhs.hcdn.common.exceptions.AbstractRethrowableException;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public class InvalidJsonException extends AbstractRethrowableException
{
	public InvalidJsonException(@NonNls @NotNull final String because)
	{
		super(message(because));
	}

	public InvalidJsonException(@NotNull final EndOfFileException prematureEndOfFile)
	{
		super("Invalid JSON because of premature end of file", prematureEndOfFile);
	}

	public InvalidJsonException(@NonNls @NotNull final String because, @NotNull final Exception cause)
	{
		super(message(because), cause);
	}

	private static String message(final String because)
	{
		return format(ENGLISH, "Invalid JSON because %1$s", because);
	}
}
