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
import uk.nhs.hdn.common.parsers.parseResultUsers.ParseResultUser;
import uk.nhs.hdn.common.parsers.separatedValueParsers.SingleLineCommaSeparatedValueParser;
import uk.nhs.hdn.dbs.FileVersion;
import uk.nhs.hdn.dbs.response.parsing.fixedWidthLineUsers.ResponseBodyFixedWidthLineUser;
import uk.nhs.hdn.dbs.response.parsing.fixedWidthLineUsers.ResponseBodySingleLineSeparatedValueParseEventHandler;
import uk.nhs.hdn.dbs.response.ResponseBody;

public final class ResponseBodySingleLineCommaSeparatedValueParser extends SingleLineCommaSeparatedValueParser{

	public ResponseBodySingleLineCommaSeparatedValueParser(@NotNull final FileVersion fileVersion, final int zeroBasedLineIndex, @NotNull final ParseResultUser<ResponseBody> parseResultUser)
	{
		super(new ResponseBodySingleLineSeparatedValueParseEventHandler(new ResponseBodyFixedWidthLineUser(fileVersion), zeroBasedLineIndex, parseResultUser));
	}

}
