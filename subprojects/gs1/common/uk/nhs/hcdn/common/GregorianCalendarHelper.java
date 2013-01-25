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
import java.util.Locale;
import java.util.TimeZone;

import static java.lang.System.currentTimeMillis;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.ROOT;
import static uk.nhs.hcdn.common.TimeZoneHelper.BST;
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
	public static GregorianCalendar utc(@MillisecondsSince1970 final long millisecondsSince1970)
	{
		return fromMillisecondsSince1970(millisecondsSince1970, UTC);
	}

	@NotNull
	public static GregorianCalendar bst(@MillisecondsSince1970 final long millisecondsSince1970)
	{
		return fromMillisecondsSince1970(millisecondsSince1970, BST);
	}

	@NotNull
	public static GregorianCalendar utcWithOneBasedMonth(final int year, final int oneBasedMonth, final int day, final int hour, final int minute, final int second)
	{
		return fromComponents(year, oneBasedMonth, day, hour, minute, second, UTC);
	}

	@NotNull
	public static GregorianCalendar bstWithOneBasedMonth(final int year, final int oneBasedMonth, final int day, final int hour, final int minute, final int second)
	{
		return fromComponents(year, oneBasedMonth, day, hour, minute, second, BST);
	}

	private static GregorianCalendar fromMillisecondsSince1970(@MillisecondsSince1970 final long millisecondsSince1970, final TimeZone timeZone)
	{
		final GregorianCalendar instance = newGregorianCalendar(timeZone, ROOT);
		instance.setTimeInMillis(millisecondsSince1970);
		return instance;
	}

	@SuppressWarnings("MagicConstant")
	private static GregorianCalendar fromComponents(final int year, final int oneBasedMonth, final int day, final int hour, final int minute, final int second, final TimeZone timeZone)
	{
		final GregorianCalendar instance = newGregorianCalendar(timeZone, ROOT);
		instance.set(year, oneBasedMonth - 1, day, hour, minute, second);
		return instance;
	}

	@NotNull
	private static GregorianCalendar newGregorianCalendar(@NotNull final TimeZone timeZone, @NotNull final Locale locale)
	{
		final GregorianCalendar instance = new GregorianCalendar(timeZone, locale);
		instance.setLenient(false);
		return instance;
	}
}
