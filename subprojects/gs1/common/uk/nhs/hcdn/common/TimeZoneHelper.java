/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.TimeZone;

import static java.util.TimeZone.getTimeZone;

public final class TimeZoneHelper
{
	private TimeZoneHelper()
	{}

	@NonNls
	@NotNull
	public static final TimeZone UTC = getTimeZone("Etc/UTC");
}
