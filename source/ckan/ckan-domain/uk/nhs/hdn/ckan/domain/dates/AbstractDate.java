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

package uk.nhs.hdn.ckan.domain.dates;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.ValueSerialiser;
import uk.nhs.hdn.common.unknown.AbstractIsUnknown;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static java.util.Locale.ROOT;

public abstract class AbstractDate extends AbstractIsUnknown implements Date
{
	@NotNull private static final String DateExampleFormat = "30/09/2012";
	@NotNull private static final String TruncatedDateExampleFormat = "04/12/12";
	private static final int DateFormatLength = DateExampleFormat.length();
	private static final int TruncatedDateFormatLength = TruncatedDateExampleFormat.length();

	@SuppressWarnings({"MethodNamesDifferingOnlyByCase", "UnusedDeclaration"}) // used reflectively
	@NotNull
	public static Date parseDate(@NotNull final String dateFormatString)
	{
		final int length = dateFormatString.length();
		final SimpleDateFormat simpleDateFormat;
		if (length == DateFormatLength)
		{
			simpleDateFormat = simpleDateFormat();
		}
		else if (length == TruncatedDateFormatLength)
		{
			simpleDateFormat = new SimpleDateFormat("dd'/'MM'/'yy", ROOT);
		}
		else
		{
			return new UnrecognisedDate(dateFormatString);
		}
		final java.util.Date parse;
		try
		{
			parse = simpleDateFormat.parse(dateFormatString);
		}
		catch (ParseException e)
		{
			return new UnparseableDate(dateFormatString, e);
		}
		return new KnownDate(parse.getTime());
	}

	protected AbstractDate(final boolean isUnknown)
	{
		super(isUnknown);
	}

	@Override
	public final void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		try
		{
			valueSerialiser.writeValue(toString());
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}

	protected static SimpleDateFormat simpleDateFormat()
	{
		return new SimpleDateFormat("dd'/'MM'/'yyyy", ROOT);
	}
}
