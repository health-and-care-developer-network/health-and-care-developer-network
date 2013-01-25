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

package uk.nhs.hcdn.dts.domain.statusRecords.dateTime;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.MillisecondsSince1970;
import uk.nhs.hcdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hcdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hcdn.common.serialisers.ValueSerialiser;
import uk.nhs.hcdn.common.unknown.AbstractIsUnknown;

import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.ROOT;
import static uk.nhs.hcdn.common.GregorianCalendarHelper.*;

public final class KnownDateTime extends AbstractIsUnknown implements DateTime
{
	private static final int Size = 14;
	private static final int PlusSign = (int) '+';
	private static final int MinusSign = (int) '-';

	@SuppressWarnings("MagicNumber")
	@NotNull
	public static KnownDateTime parseDateTime(@NonNls @NotNull final String dateTime)
	{
		if (dateTime.length() != Size)
		{
			throw new IllegalArgumentException(format(ENGLISH, "DateTime %1$s must be %2$s characters", dateTime, Size));
		}

		return new KnownDateTime
		(
			bstWithOneBasedMonth
			(
				paddedDecimalValueToInteger(dateTime, 0, 4, "year"),
				paddedDecimalValueToInteger(dateTime, 4, 6, "month"),
				paddedDecimalValueToInteger(dateTime, 6, 8, "day"),
				paddedDecimalValueToInteger(dateTime, 8, 10, "hour"),
				paddedDecimalValueToInteger(dateTime, 10, 12, "minute"),
				paddedDecimalValueToInteger(dateTime, 12, Size, "seconds")
			).getTimeInMillis()
		);
	}

	@NotNull
	public static KnownDateTime parseXmlSchemaDateTime(@NonNls @NotNull final String dateTime)
	{
		return new KnownDateTime(DatatypeConverter.parseDateTime(dateTime).getTimeInMillis());
	}

	private static int paddedDecimalValueToInteger(@NotNull final String value, final int from, final int to, @NonNls @NotNull final String fieldName)
	{
		final String fieldValue = value.substring(from, to);
		final int first = (int) fieldValue.charAt(0);
		if (first == PlusSign || first == MinusSign)
		{
			throw new IllegalArgumentException(format(ENGLISH, "Could not parse DateTime string because field %1$s (value %2$s) has a leading = or - sign", fieldName, fieldValue));
		}

		try
		{
			return Integer.parseInt(fieldValue);
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException(format(ENGLISH, "Could not parse DateTime string because field %1$s (value %2$s) was not an integer", fieldName, fieldValue), e);
		}
	}

	@MillisecondsSince1970
	private final long dateTime;

	public KnownDateTime(@MillisecondsSince1970 final long dateTime)
	{
		super(false);
		this.dateTime = dateTime;
	}

	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(asYYYYMMDDhhmmss());
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}

	@MillisecondsSince1970
	public long asUtcMillisecondsSince1980()
	{
		return dateTime;
	}

	@NotNull
	public GregorianCalendar asUtcGregorianCalendar()
	{
		return utc(dateTime);
	}

	@NotNull
	public Date asDate()
	{
		return new Date(dateTime);
	}

	// Note: The DTS specifications use a different convention for naming the parts compared to Java's DateFormat or GNU / POSIX date function
	@NonNls
	@NotNull
	public String asYYYYMMDDhhmmss()
	{
		final GregorianCalendar bst = bst(dateTime);
		return new SimpleDateFormat("yyyyMMddHHmmss", ROOT).format(bst.getTime());
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

		final KnownDateTime dateTime1 = (KnownDateTime) obj;

		if (dateTime != dateTime1.dateTime)
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings("NumericCastThatLosesPrecision")
	@Override
	public int hashCode()
	{
		return (int) (dateTime ^ (dateTime >>> 32));
	}
}
