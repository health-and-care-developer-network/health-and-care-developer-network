/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.http.server.sun.helpers;

import com.sun.net.httpserver.Headers;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.http.RequestHeader;
import uk.nhs.hcdn.common.http.server.sun.restEndpoints.methodEndpoints.BadRequestException;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.common.DateHelper.fromRfc2822Form;
import static uk.nhs.hcdn.common.http.RequestHeader.ConnectionHeaderName;
import static uk.nhs.hcdn.common.http.RequestHeader.TransferEncodingHeaderName;

public final class RequestHeadersHelper
{
	@NotNull @NonNls
	private static final String Chunked = "chunked";

	@NotNull
	@NonNls
	private static final String KeepAlive = "keep-alive";

	@NotNull
	@NonNls
	private static final String Close = "close";

	private RequestHeadersHelper()
	{
	}

	@Nullable
	@NonNls
	public static Date validateZeroOrOneInstanceOfRfc2822Header(@NotNull final Headers requestHeaders, @NotNull @RequestHeader final String requestHeader) throws BadRequestException
	{
		@NonNls @Nullable final String value = validateZeroOrOneInstanceOfRequestHeader(requestHeaders, requestHeader);
		if (value == null)
		{
			return null;
		}
		try
		{
			return fromRfc2822Form(value);
		}
		catch (ParseException e)
		{
			throw new BadRequestException(format(ENGLISH, "Could not parse RFC2822 date (%2$s) in Request header %1$s", requestHeader, value), e);
		}
	}

	@Nullable @NonNls
	public static String validateZeroOrOneInstanceOfRequestHeader(@NotNull final Headers requestHeaders, @NotNull @RequestHeader final String requestHeader) throws BadRequestException
	{
		@Nullable final List<String> strings = requestHeaders.get(requestHeader);
		if (strings == null)
		{
			return null;
		}
		final int size = strings.size();
		if (size == 1)
		{
			return strings.get(0);
		}
		if (size == 0)
		{
			return null;
		}
		throw new BadRequestException(format(ENGLISH, "More than one value for Request header %1$s", requestHeader));
	}

	// true if keep-alive
	public static boolean validateConnectionRequestHeader(@NotNull final Headers requestHeaders) throws BadRequestException
	{
		@NonNls @Nullable final String value = validateZeroOrOneInstanceOfRequestHeader(requestHeaders, ConnectionHeaderName);
		if (value == null)
		{
			return false;
		}
		if (KeepAlive.equalsIgnoreCase(value))
		{
			return true;
		}
		if (Close.equalsIgnoreCase(value))
		{
			return false;
		}
		throw new BadRequestException(format(ENGLISH, "Unrecognised value %1$s for Connection header", value));
	}

	public static void validateTransferEncodingRequestHeader(@NotNull final Headers requestHeaders) throws BadRequestException
	{
		@NonNls @Nullable final String value = validateZeroOrOneInstanceOfRequestHeader(requestHeaders, TransferEncodingHeaderName);
		if (value != null && !Chunked.equalsIgnoreCase(value))
		{
			throw new BadRequestException("Only 'chunked' transfer encodings are supported");
		}
	}

	public static void validateHeadersOmitted(@SuppressWarnings("TypeMayBeWeakened") @NotNull final Headers requestHeaders, @NotNull @RequestHeader final String... requestHeaderNames) throws BadRequestException
	{
		for (final String requestHeaderName : requestHeaderNames)
		{
			if (requestHeaders.containsKey(requestHeaderName))
			{
				throw new BadRequestException(format(ENGLISH, "Request header %1$s should not be present", requestHeaderName));
			}
		}
	}
}
