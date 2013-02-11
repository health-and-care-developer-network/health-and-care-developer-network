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

package uk.nhs.hdn.dbs.request;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.dbs.request.fixedWidthWriters.FixedWidthWriter;
import uk.nhs.hdn.dbs.request.fixedWidthWriters.RightPaddedStringLeftPaddedNumberFixedWidthWriter;
import uk.nhs.hdn.dbs.request.requestBodies.RequestBody;
import uk.nhs.hdn.common.reflection.toString.AbstractToString;
import uk.nhs.hdn.common.serialisers.CouldNotEncodeDataException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;

import static uk.nhs.hdn.dbs.request.requestBodies.AbstractRequestBody.csvSerialiserForRequestBody;
import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;

public final class Request extends AbstractToString
{
	@NotNull
	private final RequestHeader requestHeader;
	@NotNull
	private final RequestBody[] requestBodies;

	public Request(@NotNull final RequestHeader requestHeader, @NotNull final RequestBody... requestBodies)
	{
		this.requestHeader = requestHeader;
		this.requestBodies = copyOf(requestBodies);
	}

	public void serialise(@NotNull final OutputStream outputStream) throws IOException, CouldNotEncodeDataException
	{
		try (Writer writer = new OutputStreamWriter(outputStream))
		{
			final FixedWidthWriter fixedWidthWriter = new RightPaddedStringLeftPaddedNumberFixedWidthWriter(writer);
			final RequestTrailer requestTrailer = requestHeader.serialiseAsFixedWidth(fixedWidthWriter, requestBodies.length);
			csvSerialiserForRequestBody(false).writeOut(outputStream, requestBodies);
			requestTrailer.serialiseAsFixedWidth(fixedWidthWriter);
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

		final Request request = (Request) obj;

		if (!Arrays.equals(requestBodies, request.requestBodies))
		{
			return false;
		}
		if (!requestHeader.equals(request.requestHeader))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = requestHeader.hashCode();
		result = 31 * result + Arrays.hashCode(requestBodies);
		return result;
	}
}
