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

package uk.nhs.hdn.dbs.response;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.*;
import uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser;
import uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher;
import uk.nhs.hdn.dbs.*;

import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.commaSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.SeparatedValueSerialiser.tabSeparatedValueSerialiser;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.LeafMatcher.leaf;
import static uk.nhs.hdn.common.serialisers.separatedValues.matchers.RecurseMatcher.rootMatcher;

public final class ResponseHeader extends AbstractToString implements Serialisable, MapSerialisable
{
	@FieldTokenName @NonNls @NotNull public static final String FileVersionField = "fileVersion";
	@FieldTokenName @NonNls @NotNull public static final String FileFormatField = "fileFormat";
	@FieldTokenName @NonNls @NotNull public static final String RequestingOrganisationCodeField = "requestingOrganisationCode";
	@FieldTokenName @NonNls @NotNull public static final String TracingServiceCodeField = "tracingServiceCode";
	@FieldTokenName @NonNls @NotNull public static final String FilePreparationTimeAndDateField = "filePreparationTimeAndDateField";
	@FieldTokenName @NonNls @NotNull public static final String FileSequenceNumber = "fileSequenceNumber";
	@FieldTokenName @NonNls @NotNull public static final String NumberOfResponseRecords = "numberOfResponseRecords";

	@NotNull
	public static SeparatedValueSerialiser csvSerialiserForResponseHeader(final boolean writeHeaderLine)
	{
		return commaSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@NotNull
	public static SeparatedValueSerialiser tsvSerialiserForResponseHeader(final boolean writeHeaderLine)
	{
		return tabSeparatedValueSerialiser(SeparatedValuesSchema, writeHeaderLine, SeparatedValuesHeadings);
	}

	@SuppressWarnings("PublicStaticArrayField")
	@NotNull
	public static final String[] SeparatedValuesHeadings =
	{
		FileVersionField,
		FileFormatField,
		RequestingOrganisationCodeField,
		TracingServiceCodeField,
		FilePreparationTimeAndDateField,
		FileSequenceNumber,
		NumberOfResponseRecords
	};

	@NotNull
	public static final RecurseMatcher SeparatedValuesSchema = rootMatcher
	(
		leaf(FileVersionField, 0),
		leaf(FileFormatField, 1),
		leaf(RequestingOrganisationCodeField, 2),
		leaf(TracingServiceCodeField, 3),
		leaf(FilePreparationTimeAndDateField, 4),
		leaf(FileSequenceNumber, 5),
		leaf(NumberOfResponseRecords, 6)
	);

	@NotNull
	public final FileVersion fileVersion;
	@NotNull
	public final FileFormat fileFormat;
	@NotNull
	public final SpineDirectoryServiceOrganisationCode requestingOrgansationCode;
	@NotNull
	public final TracingServiceCode tracingServiceCode;
	@NotNull
	public final DbsDateTime filePreparationTimeAndDate;
	public final int fileSequenceNumber;
	public final int numberOfResponseRecords;

	public ResponseHeader(@NotNull final FileVersion fileVersion, @NotNull final FileFormat fileFormat, @NotNull final SpineDirectoryServiceOrganisationCode requestingOrgansationCode, @NotNull final TracingServiceCode tracingServiceCode, @NotNull final DbsDateTime filePreparationTimeAndDate, final int fileSequenceNumber, final int numberOfResponseRecords)
	{
		this.fileVersion = fileVersion;
		this.fileFormat = fileFormat;
		this.requestingOrgansationCode = requestingOrgansationCode;
		this.tracingServiceCode = tracingServiceCode;
		this.filePreparationTimeAndDate = filePreparationTimeAndDate;
		this.fileSequenceNumber = fileSequenceNumber;
		this.numberOfResponseRecords = numberOfResponseRecords;
	}

	@Override
	public void serialise(@NotNull final Serialiser serialiser) throws CouldNotSerialiseException
	{
		try
		{
			serialiseMap(serialiser);
		}
		catch (CouldNotSerialiseMapException e)
		{
			throw new CouldNotSerialiseException(this, e);
		}
	}

	@SuppressWarnings("FeatureEnvy")
	@Override
	public void serialiseMap(@NotNull final MapSerialiser mapSerialiser) throws CouldNotSerialiseMapException
	{
		try
		{
			mapSerialiser.writeProperty(FileVersionField, fileVersion);
			mapSerialiser.writeProperty(FileFormatField, fileFormat);
			mapSerialiser.writeProperty(RequestingOrganisationCodeField, requestingOrgansationCode);
			mapSerialiser.writeProperty(TracingServiceCodeField, tracingServiceCode);
			mapSerialiser.writeProperty(FilePreparationTimeAndDateField, filePreparationTimeAndDate);
			mapSerialiser.writeProperty(FileSequenceNumber, fileSequenceNumber);
			mapSerialiser.writeProperty(NumberOfResponseRecords, numberOfResponseRecords);
		}
		catch (CouldNotWritePropertyException e)
		{
			throw new CouldNotSerialiseMapException(this, e);
		}
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

		final ResponseHeader that = (ResponseHeader) obj;

		if (fileSequenceNumber != that.fileSequenceNumber)
		{
			return false;
		}
		if (numberOfResponseRecords != that.numberOfResponseRecords)
		{
			return false;
		}
		if (fileFormat != that.fileFormat)
		{
			return false;
		}
		if (!filePreparationTimeAndDate.equals(that.filePreparationTimeAndDate))
		{
			return false;
		}
		if (fileVersion != that.fileVersion)
		{
			return false;
		}
		if (!requestingOrgansationCode.equals(that.requestingOrgansationCode))
		{
			return false;
		}
		if (!tracingServiceCode.equals(that.tracingServiceCode))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = fileVersion.hashCode();
		result = 31 * result + fileFormat.hashCode();
		result = 31 * result + requestingOrgansationCode.hashCode();
		result = 31 * result + tracingServiceCode.hashCode();
		result = 31 * result + filePreparationTimeAndDate.hashCode();
		result = 31 * result + fileSequenceNumber;
		result = 31 * result + numberOfResponseRecords;
		return result;
	}
}
