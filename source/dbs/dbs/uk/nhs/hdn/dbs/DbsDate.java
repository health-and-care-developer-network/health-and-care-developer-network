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

package uk.nhs.hdn.dbs;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;
import uk.nhs.hdn.common.serialisers.ValueSerialiser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Locale.ENGLISH;

public final class DbsDate extends AbstractToString implements ValueSerialisable
{
	// Used by Jopt-Simple
	@SuppressWarnings("UnusedDeclaration")
	@NotNull
	public static DbsDate valueOf(@NotNull final String value)
	{
		try
		{
			return new DbsDate(newSimpleDateFormat().parse(value).getTime());
		}
		catch (ParseException e)
		{
			throw new IllegalArgumentException(value, e);
		}
	}

	@NotNull
	@NonNls
	public static final String DbsDateFormat = "YYYYMMDD";

	@MillisecondsSince1970 public final long date;

	public DbsDate(@MillisecondsSince1970 final long date)
	{
		this.date = date;
	}

	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(newSimpleDateFormat().format(new Date(date)));
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}

	private static SimpleDateFormat newSimpleDateFormat()
	{
		return new SimpleDateFormat(DbsDateFormat, ENGLISH);
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

		final DbsDate that = (DbsDate) obj;

		if (date != that.date)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return (int) (date ^ (date >>> 32));
	}
}
