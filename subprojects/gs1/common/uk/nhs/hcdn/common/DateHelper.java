/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Locale.ENGLISH;

public final class DateHelper
{
	private DateHelper()
	{
	}

	@NotNull
	public static Date fromRfc2822Form(@NotNull final String rfc2822Form) throws ParseException
	{
		final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", ENGLISH);
		return format.parse(rfc2822Form);
	}
}
