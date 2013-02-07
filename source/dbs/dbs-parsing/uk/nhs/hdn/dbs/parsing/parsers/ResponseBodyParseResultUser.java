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
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.parsers.parseResultUsers.ParseResultUser;
import uk.nhs.hdn.dbs.response.ResponseBody;

public final class ResponseBodyParseResultUser implements ParseResultUser<ResponseBody>
{
	@NotNull
	public final ResponseBody[] responseBodies;

	private int lineIndex;

	public ResponseBodyParseResultUser(final int numberOfResponseRecords)
	{
		responseBodies = new ResponseBody[numberOfResponseRecords];
		lineIndex = 0;
	}

	@Override
	public void use(@Nullable final ResponseBody value)
	{
		responseBodies[lineIndex] = value;
		lineIndex++;
	}
}
