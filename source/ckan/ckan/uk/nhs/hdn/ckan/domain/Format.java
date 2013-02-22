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

package uk.nhs.hdn.ckan.domain;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.ckan.domain.enumerations.FormatCode;
import uk.nhs.hdn.common.serialisers.CouldNotSerialiseValueException;
import uk.nhs.hdn.common.serialisers.CouldNotWriteValueException;
import uk.nhs.hdn.common.serialisers.ValueSerialisable;
import uk.nhs.hdn.common.serialisers.ValueSerialiser;
import uk.nhs.hdn.common.unknown.AbstractIsUnknown;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static java.lang.System.err;
import static java.util.Locale.ENGLISH;
import static java.util.regex.Pattern.compile;
import static uk.nhs.hdn.ckan.domain.enumerations.FormatCode.*;

public final class Format extends AbstractIsUnknown implements ValueSerialisable
{
	private static final Pattern SplitOnSlashOrPlusOrSpace = compile("/|\\+| |-|\\.");

	@NotNull public static final Format UnknownFormat = new Format();

	private static final Map<String, Format> Oddities = new HashMap<String, Format>(10)
	{{
		put("", UnknownFormat);
		put("52 / XLSX", Excel.singleFormatCodeFormat);
		put("Web page", HTML.singleFormatCodeFormat);
		put("HTML web page", HTML.singleFormatCodeFormat);
		put("CSV file", CSV.singleFormatCodeFormat);
		put("MS Excel", Excel.singleFormatCodeFormat);
		put("XML RPC", XML.singleFormatCodeFormat);
	}};

	private static final int CloseBracket = (int) ')';
	private static final int OpenBracket = (int) '(';

	@NotNull public final FormatCode[] formatCodes;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	public Format(@NotNull final FormatCode... formatCodes)
	{
		super(formatCodes.length == 0);
		this.formatCodes = formatCodes;
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
		if (!super.equals(obj))
		{
			return false;
		}

		final Format format = (Format) obj;

		if (!Arrays.equals(formatCodes, format.formatCodes))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + Arrays.hashCode(formatCodes);
		return result;
	}

	@SuppressWarnings("ConditionalExpression")
	@Override
	public void serialiseValue(@NotNull final ValueSerialiser valueSerialiser) throws CouldNotSerialiseValueException
	{
		final StringWriter writer = new StringWriter(10);
		boolean afterFirst = false;
		for (final FormatCode formatCode : formatCodes)
		{
			if (afterFirst)
			{
				writer.write(" ");
			}
			else
			{
				afterFirst = true;
			}
			writer.write(formatCode.name());
		}
		try
		{
			valueSerialiser.writeValue(writer.toString());
		}
		catch (CouldNotWriteValueException e)
		{
			throw new CouldNotSerialiseValueException(this, e);
		}
	}

	@SuppressWarnings({"ForLoopReplaceableByForEach", "MethodNamesDifferingOnlyByCase"})
	@Nullable
	public static Format format(@NonNls @NotNull final String value)
	{
		final String trimmedValue = value.trim().replace(", ", " ").replace(" / ", "/"); // eg "CSV " or "KML, SHP" or "TXT / Zip"

		final String furtherTrimmedValue = value.startsWith(".") ? trimmedValue.substring(1) : trimmedValue;

		@Nullable final Format format = Oddities.get(furtherTrimmedValue);
		if (format != null)
		{
			return format;
		}

		final String[] potentialFormatCodes = SplitOnSlashOrPlusOrSpace.split(furtherTrimmedValue);
		final int length = potentialFormatCodes.length;
		if (length == 1)
		{
			return toFormatCode(value, potentialFormatCodes[0]).singleFormatCodeFormat;
		}

		final FormatCode[] formatCodes = new FormatCode[length];
		for (int index = 0; index < length; index++)
		{
			final String potentialFormatCode = potentialFormatCodes[index];
			final int potentialFormatCodeLength = potentialFormatCode.length();
			// eg gz (txt) or Zip (csv)
			if (index != 0 && isBracketed(potentialFormatCode, potentialFormatCodeLength))
			{
				final FormatCode formatCode = toFormatCode(value, potentialFormatCode.substring(1, potentialFormatCodeLength - 1));
				final int indexLessOne = index - 1;
				formatCodes[index] = formatCodes[indexLessOne];
				formatCodes[indexLessOne] = formatCode;
			}
			else
			{
				formatCodes[index] = toFormatCode(value, potentialFormatCode);
			}
		}
		return new Format(formatCodes);
	}

	private static boolean isBracketed(final CharSequence potentialFormatCode, final int potentialFormatCodeLength)
	{
		//noinspection SimplifiableIfStatement
		if (potentialFormatCodeLength < 2)
		{
			return false;
		}
		return (int) potentialFormatCode.charAt(0) == OpenBracket && (int) potentialFormatCode.charAt(potentialFormatCodeLength - 1) == CloseBracket;
	}

	@NotNull
	private static FormatCode toFormatCode(final String value, final String potentialFormatCode)
	{
		@Nullable final FormatCode formatCode = formatCode(potentialFormatCode.toUpperCase(ENGLISH));
		if (formatCode == null)
		{
			err.println(String.format(ENGLISH, "Unknown format code string '%1$s' in format string '%2$s'", potentialFormatCode, value));
			return UnknownFormatCode;
		}
		return formatCode;
	}
}
