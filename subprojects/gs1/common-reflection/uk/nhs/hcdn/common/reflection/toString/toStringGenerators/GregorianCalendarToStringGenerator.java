package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Calendar.*;
import static java.util.Locale.UK;
import static uk.nhs.hcdn.common.StringHelper.pad;

public final class GregorianCalendarToStringGenerator extends AbstractToStringGenerator<GregorianCalendar>
{
	private static final int MinutesInHour = 60;

	@NotNull
	public static final ToStringGenerator<?> GregorianToStringGeneratorInstance = new GregorianCalendarToStringGenerator();

	private GregorianCalendarToStringGenerator()
	{
		super(GregorianCalendar.class);
	}

	@NotNull
	@Override
	public String toString(@NotNull final GregorianCalendar value)
	{
		final long millisecondsSince1970AsOfDate = currentTimeMillis();

		final TimeZone timeZone = value.getTimeZone();
		final int offsetInMilliseconds = timeZone.getOffset(millisecondsSince1970AsOfDate);
		@NonNls final String timeZoneString;
		if (offsetInMilliseconds == 0)
		{
			timeZoneString = "Z";
		}
		else
		{
			final int offsetInMinutes = abs(offsetInMilliseconds / 1000 / MinutesInHour);
			final String sign = (offsetInMilliseconds < 0) ? "-" : "+";
			final int offsetHours = offsetInMinutes / MinutesInHour;
			final int offsetMinutes = offsetInMinutes % MinutesInHour;
			@NonNls final String format = "%1$s%2$s:%3$s";
			timeZoneString = format(UK, format, sign, pad(offsetHours, 2), pad(offsetMinutes, 2));
		}
		@NonNls final String format = "%1$s-%2$s-%3$sT%4$s:%5$s:%6$s.%7$s%8$s";
		return format(UK, format, pad(value.get(YEAR), 4), pad(value.get(MONTH) + 1, 2), pad(value.get(DAY_OF_MONTH), 2), pad(value.get(HOUR_OF_DAY), 2), pad(value.get(MINUTE), 2), pad(value.get(SECOND), 2), pad(value.get(MILLISECOND), 3), timeZoneString);
	}

}
