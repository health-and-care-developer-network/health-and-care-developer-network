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

package uk.nhs.hdn.ckan.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.common.StringHelper.padAsDecimal;

public final class MicrosecondTimestamp
{
	@NotNull
	private static final String ExampleFormat = "2012-06-28T20:06:30.061645";
	public static final int MicrosecondsSize = 3;
	private static final int ExpectedLength = ExampleFormat.length();
	private static final int MillisecondsOnlyLength = ExampleFormat.length() - MicrosecondsSize;
	private static final int MicrosecondsStartsAt = MillisecondsOnlyLength;
	private static final int MinusSign = (int) '-';
	private static final int PlusSign = (int) '+';

	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static MicrosecondTimestamp microsecondTimestamp(@NotNull final String microsecondFormatString)
	{
		if (microsecondFormatString.length() != ExpectedLength)
		{
			throw new IllegalArgumentException(format(ENGLISH, "microsecondFormatString %1$s is the wrong length", microsecondFormatString));
		}

		final SimpleDateFormat simpleDateFormat = simpleDateFormat();
		final Date parse;
		try
		{
			parse = simpleDateFormat.parse(microsecondFormatString.substring(0, MillisecondsOnlyLength));
		}
		catch (ParseException e)
		{
			throw new IllegalArgumentException(format(ENGLISH, "microsecondFormatString %1$s is not correctly formatted", microsecondFormatString), e);
		}

		final String microsecondsString = microsecondFormatString.substring(MicrosecondsStartsAt, MicrosecondsSize);
		final int firstCharacter = (int) microsecondsString.charAt(0);
		if (firstCharacter == PlusSign || firstCharacter == MinusSign)
		{
			throw new IllegalArgumentException(format(ENGLISH, "microsecondFormatString %1$s has a plus or minus sign in th microsecond value", microsecondFormatString));
		}

		final long microseconds;
		try
		{
			microseconds = parseLong(microsecondsString);
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException(format(ENGLISH, "microsecondFormatString %1$s has a non-numeric microsecond value", microsecondFormatString), e);
		}

		return new MicrosecondTimestamp(parse.getTime(), microseconds);
	}

	@MillisecondsSince1970
	private final long milliseconds;
	private final long additionalMicroseconds;

	public MicrosecondTimestamp(@MillisecondsSince1970 final long milliseconds, final long additionalMicroseconds)
	{
		this.milliseconds = milliseconds;
		this.additionalMicroseconds = additionalMicroseconds;
	}

	@NotNull
	@Override
	public String toString()
	{
		// eg "2012-06-28T20:06:30.061645"
		return simpleDateFormat().format(new Date(milliseconds)) + padAsDecimal(additionalMicroseconds, 3);
	}

	@Override
	public boolean equals(@Nullable final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final MicrosecondTimestamp that = (MicrosecondTimestamp) obj;

		if (additionalMicroseconds != that.additionalMicroseconds)
		{
			return false;
		}
		if (milliseconds != that.milliseconds)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = (int) (milliseconds ^ (milliseconds >>> 32));
		result = 31 * result + (int) (additionalMicroseconds ^ (additionalMicroseconds >>> 32));
		return result;
	}

	@NotNull
	private static SimpleDateFormat simpleDateFormat()
	{
		return new SimpleDateFormat("yyyy-MM-ddThh:mm:ss.SSS", ENGLISH);
	}
}
