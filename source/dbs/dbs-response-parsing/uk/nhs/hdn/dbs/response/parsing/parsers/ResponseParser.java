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

package uk.nhs.hdn.dbs.response.parsing.parsers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.exceptions.ImpossibleEnumeratedStateException;
import uk.nhs.hdn.common.parsers.CouldNotParseException;
import uk.nhs.hdn.common.parsers.Parser;
import uk.nhs.hdn.common.parsers.parseResultUsers.NonNullValueReturningParseResultUser;
import uk.nhs.hdn.common.parsers.parseResultUsers.ParseResultUser;
import uk.nhs.hdn.dbs.FileFormat;
import uk.nhs.hdn.dbs.FileVersion;
import uk.nhs.hdn.dbs.response.Response;
import uk.nhs.hdn.dbs.response.ResponseBody;
import uk.nhs.hdn.dbs.response.ResponseHeader;
import uk.nhs.hdn.dbs.response.ResponseTrailer;

import java.io.*;

import static uk.nhs.hdn.common.CharsetHelper.Utf8;

public final class ResponseParser implements Parser
{
	@NotNull
	public static Response parseResponse(@NotNull final InputStream inputStream) throws IOException, CouldNotParseException
	{
		boolean throwExceptionInFinallyDuringClose = false;
		final Response response;
		try
		{
			response = readResponse(inputStream);

			throwExceptionInFinallyDuringClose = true;
		}
		finally
		{
			try
			{
				inputStream.close();
			}
			catch (IOException e)
			{
				if (throwExceptionInFinallyDuringClose)
				{
					throw e;
				}
			}
		}
		return response;
	}

	private static Response readResponse(final InputStream inputStream) throws IOException, CouldNotParseException
	{
		final NonNullValueReturningParseResultUser<Response> parseResultUser = new NonNullValueReturningParseResultUser<>();

		boolean throwExceptionInFinallyDuringClose = false;
		final Reader reader = new InputStreamReader(inputStream, Utf8);
		try
		{
			new ResponseParser(parseResultUser).parse(new BufferedReader(reader), 0L);

			throwExceptionInFinallyDuringClose = true;
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (IOException e)
			{
				if (throwExceptionInFinallyDuringClose)
				{
					throw e;
				}
			}
		}

		return parseResultUser.value();
	}

	@NotNull
	private final ParseResultUser<Response> responseParseResultUser;

	public ResponseParser(@NotNull final ParseResultUser<Response> responseParseResultUser)
	{
		this.responseParseResultUser = responseParseResultUser;
	}

	@Override
	public void parse(@NotNull final BufferedReader bufferedReader, @MillisecondsSince1970 final long lastModified) throws IOException, CouldNotParseException
	{
		final ResponseHeader responseHeader = parseResponseHeader(bufferedReader, lastModified);

		final ResponseBody[] responseBodies = parseResponseBodies(bufferedReader, lastModified, responseHeader);

		final ResponseTrailer responseTrailer = parseResponseTrailer(bufferedReader, lastModified);

		if (responseTrailer.doesResponseHeaderMatchOurDetails(responseHeader))
		{
			throw new CouldNotParseException(0L, "Trailer details do not match header details");
		}

		responseParseResultUser.use(new Response(responseHeader, responseBodies));
	}

	private static ResponseHeader parseResponseHeader(final BufferedReader bufferedReader, final long lastModified) throws IOException, CouldNotParseException
	{
		final NonNullValueReturningParseResultUser<ResponseHeader> responseHeaderParseResultUser = new NonNullValueReturningParseResultUser<>();
		new ResponseHeaderSingleLineFixedWidthParser(responseHeaderParseResultUser).parse(bufferedReader, lastModified);
		return responseHeaderParseResultUser.value();
	}

	private static ResponseBody[] parseResponseBodies(final BufferedReader bufferedReader, final long lastModified, final ResponseHeader responseHeader) throws IOException, CouldNotParseException
	{
		// NOTE: This is a very anaemic domain - good practice would be to encapsulate beahviour in ResponseHeader, perhaps
		final int numberOfResponseRecords = responseHeader.numberOfResponseRecords;
		final FileVersion fileVersion = responseHeader.fileVersion;
		final FileFormat fileFormat = responseHeader.fileFormat;
		final ResponseBodyParseResultUser responseBodyParseResultUser = new ResponseBodyParseResultUser(numberOfResponseRecords);
		for (int lineIndex = 0; lineIndex < numberOfResponseRecords; lineIndex++)
		{
			constructResponseBodyParser(lineIndex, responseBodyParseResultUser, fileVersion, fileFormat).parse(bufferedReader, lastModified);
		}
		return responseBodyParseResultUser.responseBodies;
	}


	private static ResponseTrailer parseResponseTrailer(final BufferedReader bufferedReader, final long lastModified) throws IOException, CouldNotParseException
	{
		final NonNullValueReturningParseResultUser<ResponseTrailer> responseTrailerParseResult = new NonNullValueReturningParseResultUser<>();
		new ResponseTrailerSingleLineFixedWidthParser(responseTrailerParseResult).parse(bufferedReader, lastModified);
		return responseTrailerParseResult.value();
	}

	private static Parser constructResponseBodyParser(final int zeroBasedLineIndex, final ParseResultUser<ResponseBody> responseBodyParseResultUser, final FileVersion fileVersion, final FileFormat fileFormat)
	{
		// NOTE: This is a very anaemic domain - good practice would be to encapsulate beahviour in ResponseHeader, perhaps
		switch (fileFormat)
		{
			case FixedLength:
				return new ResponseBodySingleLineFixedWidthParser(fileVersion, responseBodyParseResultUser);

			case CommaSeparatedValue:
				return new ResponseBodySingleLineCommaSeparatedValueParser(fileVersion, zeroBasedLineIndex, responseBodyParseResultUser);

			default:
				throw new ImpossibleEnumeratedStateException();
		}
	}
}
