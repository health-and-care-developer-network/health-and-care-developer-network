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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.dbs.*;

public final class ResponseTrailer extends AbstractToString
{
	@NotNull
	private final FileVersion fileVersion;
	@NotNull
	private final SpineDirectoryServiceOrganisationCode requestingOrgansationCode;
	@NotNull
	private final TracingServiceCode tracingServiceCode;
	@NotNull
	private final DbsDateTime filePreparationTimeAndDate;
	private final int fileSequenceNumber;
	private final int numberOfResponseRecords;

	public ResponseTrailer(@NotNull final FileVersion fileVersion, @NotNull final SpineDirectoryServiceOrganisationCode requestingOrgansationCode, @NotNull final TracingServiceCode tracingServiceCode, @NotNull final DbsDateTime filePreparationTimeAndDate, final int fileSequenceNumber, final int numberOfResponseRecords)
	{
		this.fileVersion = fileVersion;
		this.requestingOrgansationCode = requestingOrgansationCode;
		this.tracingServiceCode = tracingServiceCode;
		this.filePreparationTimeAndDate = filePreparationTimeAndDate;
		this.fileSequenceNumber = fileSequenceNumber;
		this.numberOfResponseRecords = numberOfResponseRecords;
	}

	public boolean doesResponseHeaderMatchOurDetails(@NotNull final ResponseHeader responseHeader)
	{
		return new ResponseHeader(fileVersion, responseHeader.fileFormat, requestingOrgansationCode, tracingServiceCode, filePreparationTimeAndDate, fileSequenceNumber, numberOfResponseRecords).equals(responseHeader);
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

		final ResponseTrailer that = (ResponseTrailer) obj;

		if (fileSequenceNumber != that.fileSequenceNumber)
		{
			return false;
		}
		if (numberOfResponseRecords != that.numberOfResponseRecords)
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
		result = 31 * result + requestingOrgansationCode.hashCode();
		result = 31 * result + tracingServiceCode.hashCode();
		result = 31 * result + filePreparationTimeAndDate.hashCode();
		result = 31 * result + fileSequenceNumber;
		result = 31 * result + numberOfResponseRecords;
		return result;
	}
}
