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

package uk.nhs.hcdn.common;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import static java.lang.System.currentTimeMillis;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.ROOT;
import static uk.nhs.hcdn.common.TimeZoneHelper.UTC;

public final class GregorianCalendarHelper
{
	private GregorianCalendarHelper()
	{
	}

	@NotNull
	public static String rfc2822DateForNow()
	{
		return toRfc2822Form(utcNow());
	}

	@NotNull
	public static String toRfc2822Form(@NotNull final GregorianCalendar calendar)
	{
		return new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", ENGLISH).format(calendar.getTime());
	}

	@NotNull
	public static GregorianCalendar utcNow()
	{
		return utc(currentTimeMillis());
	}

	@NotNull
	public static GregorianCalendar utc(final long millisecondsSince1970)
	{
		final GregorianCalendar instance = new GregorianCalendar(UTC, ROOT);
		instance.setLenient(false);
		instance.setTimeInMillis(millisecondsSince1970);
		return instance;
	}
}
