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

package uk.nhs.hdn.common.http.client.headHttpResponseUsers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.http.ResponseCode;
import uk.nhs.hdn.common.http.ResponseCodeRange;
import uk.nhs.hdn.common.http.ResponseHeader;

import java.io.PrintStream;

import static java.lang.System.err;
import static java.lang.System.out;
import static uk.nhs.hdn.common.GregorianCalendarHelper.toRfc2822Form;
import static uk.nhs.hdn.common.GregorianCalendarHelper.utc;
import static uk.nhs.hdn.common.http.ResponseHeader.*;

public final class ConsoleLoggingHeadHttpResponseUser implements HeadHttpResponseUser
{
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	@NotNull
	public static final HeadHttpResponseUser OutConsoleLoggingHeadHttpResponseUserInstance = new ConsoleLoggingHeadHttpResponseUser(out);

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	@NotNull
	public static final HeadHttpResponseUser ErrorConsoleLoggingHeadHttpResponseUserInstance = new ConsoleLoggingHeadHttpResponseUser(err);

	@NotNull
	private final PrintStream printStream;

	public ConsoleLoggingHeadHttpResponseUser(@NotNull final PrintStream printStream)
	{
		this.printStream = printStream;
	}

	@Override
	public void response(@ResponseCode final int responseCode, @NotNull final ResponseCodeRange responseCodeRange, @MillisecondsSince1970 final long date, @MillisecondsSince1970 final long expires, final long contentLengthOrMinusOneIfNoneSupplied, @Nullable final String contentType, @Nullable final String contentEncoding)
	{
		printLongLikeHeader("ResponseCode", (long) responseCode);
		printDateLikeHeader(DateHeaderName, date);
		printDateLikeHeader(ExpiresHeaderName, expires);
		if (contentLengthOrMinusOneIfNoneSupplied >= 0L)
		{
			printLongLikeHeader(ContentLanguageHeaderName, contentLengthOrMinusOneIfNoneSupplied);
		}
		printHeader(ContentTypeHeaderName, contentType);
		printHeader(ContentEncodingHeaderName, contentEncoding);
	}

	private void printLongLikeHeader(@NonNls @ResponseHeader final String headerName, final long value)
	{
		printHeader(headerName, Long.toString(value));
	}

	private void printDateLikeHeader(@ResponseHeader final String headerName, @MillisecondsSince1970 final long date)
	{
		if (date > 0L)
		{
			printHeader(headerName, toRfc2822Form(utc(date)));
		}
	}

	private void printHeader(@NonNls @ResponseHeader final String headerName, @Nullable final String formattedValue)
	{
		if (formattedValue == null)
		{
			return;
		}
		printStream.print(headerName);
		printStream.print(": ");
		printStream.println(formattedValue);
		printStream.flush();
	}
}
