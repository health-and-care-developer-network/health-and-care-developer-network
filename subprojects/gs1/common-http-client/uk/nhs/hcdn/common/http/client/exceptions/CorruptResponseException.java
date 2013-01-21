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

package uk.nhs.hcdn.common.http.client.exceptions;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.http.ResponseCodeOutsideOfValidRangeInvalidException;

import java.io.IOException;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class CorruptResponseException extends Exception
{
	public CorruptResponseException(@NonNls final String because, @NotNull final IOException cause)
	{
		super(format(ENGLISH, "Corrupt response because %1$s caused by IOException %2$s", because, cause.getMessage()), cause);
	}

	public CorruptResponseException(@NonNls final String because)
	{
		super(format(ENGLISH, "Corrupt response because %1$s", because));
	}

	public CorruptResponseException(final ResponseCodeOutsideOfValidRangeInvalidException e)
	{
		super(format(ENGLISH, "Corrupt response because response code was outside of valid range"), e);
	}
}
