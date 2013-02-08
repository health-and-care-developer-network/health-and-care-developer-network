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

package uk.nhs.dbs.request;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.dbs.request.fixedWidthWriters.FixedWidthWriter;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.CouldNotEncodeDataException;
import uk.nhs.hdn.dbs.FileVersion;
import uk.nhs.hdn.dbs.SpineDirectoryServiceOrganisationCode;
import uk.nhs.hdn.dbs.TracingServiceCode;

import java.io.IOException;

import static uk.nhs.hdn.dbs.DbsDateTime.dbsDateTimeNow;
import static uk.nhs.hdn.dbs.FileFormat.CommaSeparatedValue;
import static uk.nhs.hdn.dbs.FileType.Request;
import static uk.nhs.hdn.dbs.RecordType.Header;

public final class RequestHeader extends AbstractToString
{
	public static final int MaximumFileSequenceNumber = 99999999;
	@NotNull
	private final FileVersion fileVersion;
	@NotNull
	private final SpineDirectoryServiceOrganisationCode requestingOrgansationCode;
	@NotNull
	private final TracingServiceCode tracingServiceCode;
	private final int fileSequenceNumber;

	public RequestHeader(@NotNull final FileVersion fileVersion, @NotNull final SpineDirectoryServiceOrganisationCode requestingOrgansationCode, @NotNull final TracingServiceCode tracingServiceCode, final int fileSequenceNumber)
	{
		if (fileSequenceNumber < 1 || fileSequenceNumber > MaximumFileSequenceNumber)
		{
			throw new IllegalArgumentException("fileSequenceNumber must be between 1 and 99999999");
		}
		this.fileVersion = fileVersion;
		this.requestingOrgansationCode = requestingOrgansationCode;
		this.tracingServiceCode = tracingServiceCode;
		this.fileSequenceNumber = fileSequenceNumber;
	}

	@SuppressWarnings({"MagicNumber", "FeatureEnvy"})
	@NotNull
	public RequestTrailer serialiseAsFixedWidth(@NotNull final FixedWidthWriter fixedWidthWriter, final int numberOfRequestRecords) throws IOException, CouldNotEncodeDataException
	{
		final String dbsDateTime = dbsDateTimeNow();

		fixedWidthWriter.writeString(2, Header.recordType);
		fixedWidthWriter.writeUnsignedNumber(1, Request.value);
		fixedWidthWriter.writeString(3, fileVersion.version);
		fixedWidthWriter.writeUnsignedNumber(1, CommaSeparatedValue.value);
		fixedWidthWriter.writeString(14, requestingOrgansationCode.value());
		fixedWidthWriter.writeString(14, tracingServiceCode.value());
		fixedWidthWriter.writeString(14, dbsDateTime);
		fixedWidthWriter.writeUnsignedNumber(8, fileSequenceNumber);
		fixedWidthWriter.writeUnsignedNumber(6, numberOfRequestRecords);
		return new RequestTrailer(fileVersion, requestingOrgansationCode, tracingServiceCode, fileSequenceNumber, dbsDateTime, numberOfRequestRecords);
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

		final RequestHeader that = (RequestHeader) obj;

		if (fileSequenceNumber != that.fileSequenceNumber)
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
		result = 31 * result + requestingOrgansationCode.hashCode();
		result = 31 * result + tracingServiceCode.hashCode();
		result = 31 * result + fileSequenceNumber;
		return result;
	}
}
