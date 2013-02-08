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
import uk.nhs.dbs.request.fixedWidthWriters.FixedWidthWriter;
import uk.nhs.hdn.common.serialisers.CouldNotEncodeDataException;
import uk.nhs.hdn.dbs.FileVersion;
import uk.nhs.hdn.dbs.SpineDirectoryServiceOrganisationCode;
import uk.nhs.hdn.dbs.TracingServiceCode;

import java.io.IOException;

import static uk.nhs.hdn.dbs.FileType.Request;
import static uk.nhs.hdn.dbs.RecordType.Trailer;

public final class RequestTrailer
{
	@NotNull
	public final FileVersion fileVersion;
	@NotNull
	public final SpineDirectoryServiceOrganisationCode requestingOrgansationCode;
	@NotNull
	public final TracingServiceCode tracingServiceCode;
	public final int fileSequenceNumber;
	@NotNull
	private final String dbsDateTime;
	private final int numberOfRequestRecords;

	public RequestTrailer(@NotNull final FileVersion fileVersion, @NotNull final SpineDirectoryServiceOrganisationCode requestingOrgansationCode, @NotNull final TracingServiceCode tracingServiceCode, final int fileSequenceNumber, @NotNull final String dbsDateTime, final int numberOfRequestRecords)
	{
		this.fileVersion = fileVersion;
		this.requestingOrgansationCode = requestingOrgansationCode;
		this.tracingServiceCode = tracingServiceCode;
		this.fileSequenceNumber = fileSequenceNumber;
		this.dbsDateTime = dbsDateTime;
		this.numberOfRequestRecords = numberOfRequestRecords;
	}

	@SuppressWarnings({"MagicNumber", "FeatureEnvy"})
	public void serialiseAsFixedWidth(@NotNull final FixedWidthWriter fixedWidthWriter) throws IOException, CouldNotEncodeDataException
	{
		fixedWidthWriter.writeString(2, Trailer.recordType);
		fixedWidthWriter.writeUnsignedNumber(1, Request.value);
		fixedWidthWriter.writeString(3, fileVersion.version);
		fixedWidthWriter.writeString(14, requestingOrgansationCode.value());
		fixedWidthWriter.writeString(14, tracingServiceCode.value());
		fixedWidthWriter.writeString(14, dbsDateTime);
		fixedWidthWriter.writeUnsignedNumber(8, fileSequenceNumber);
		fixedWidthWriter.writeUnsignedNumber(6, numberOfRequestRecords);
	}
}
