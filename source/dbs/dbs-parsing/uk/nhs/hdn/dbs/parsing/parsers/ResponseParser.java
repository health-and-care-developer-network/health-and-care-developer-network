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

package uk.nhs.hdn.dbs.parsing.parsers;

import org.jetbrains.annotations.NotNull;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.exceptions.ImpossibleEnumeratedStateException;
import uk.nhs.hdn.common.parsers.CouldNotParseException;
import uk.nhs.hdn.common.parsers.Parser;
import uk.nhs.hdn.common.parsers.parseResultUsers.NonNullValueReturningParseResultUser;
import uk.nhs.hdn.common.parsers.parseResultUsers.ParseResultUser;
import uk.nhs.hdn.dbs.response.Response;
import uk.nhs.hdn.dbs.response.ResponseBody;
import uk.nhs.hdn.dbs.response.ResponseHeader;
import uk.nhs.hdn.dbs.response.ResponseTrailer;

import java.io.BufferedReader;
import java.io.IOException;

public final class ResponseParser implements Parser
{
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
			throw new CouldNotParseException(0, "Trailer details do not match header details");
		}

		responseParseResultUser.use(new Response(responseHeader, responseBodies));
	}

	private static ResponseHeader parseResponseHeader(final BufferedReader bufferedReader, final long lastModified) throws IOException, CouldNotParseException
	{
		final NonNullValueReturningParseResultUser<ResponseHeader> responseHeaderParseResultUser = new NonNullValueReturningParseResultUser<>();
		new ResponseHeaderSingleLineFixedWidthParser(responseHeaderParseResultUser).parse(bufferedReader, lastModified);
		return responseHeaderParseResultUser.value();
	}

	private static Parser constructResponseBodyParser(final ResponseHeader responseHeader, final ParseResultUser<ResponseBody> responseBodyParseResultUser)
	{
		final Parser parser;
		switch (responseHeader.fileFormat)
		{
			case FixedLength:
				parser = new ResponseBodySingleLineFixedWidthParser(responseHeader.fileVersion, responseBodyParseResultUser);
				break;

			case CommaSeparatedValue:
				// parser = new ResponseBodyCommaSeparatedValueParser(responseHeader.fileVersion);
				throw new UnsupportedOperationException();

			default:
				throw new ImpossibleEnumeratedStateException();
		}
		return parser;
	}

	private static ResponseBody[] parseResponseBodies(final BufferedReader bufferedReader, final long lastModified, final ResponseHeader responseHeader) throws IOException, CouldNotParseException
	{
		// NOTE: This is a very anaemic domain - good practice would be to encapsulate beahviour in ResponseHeader, perhaps
		final ResponseBodyParseResultUser responseBodyParseResultUser = new ResponseBodyParseResultUser(responseHeader.numberOfResponseRecords);
		final Parser parser = constructResponseBodyParser(responseHeader, null);
		final int numberOfResponseRecords = responseHeader.numberOfResponseRecords;
		for (int lineIndex = 0; lineIndex < numberOfResponseRecords; lineIndex++)
		{
			parser.parse(bufferedReader, lastModified);
		}
		return responseBodyParseResultUser.responseBodies;
	}


	private static ResponseTrailer parseResponseTrailer(final BufferedReader bufferedReader, final long lastModified) throws IOException, CouldNotParseException
	{
		final NonNullValueReturningParseResultUser<ResponseTrailer> responseTrailerParseResult = new NonNullValueReturningParseResultUser<>();
		new ResponseTrailerSingleLineFixedWidthParser(responseTrailerParseResult).parse(bufferedReader, lastModified);
		return responseTrailerParseResult.value();
	}
}
