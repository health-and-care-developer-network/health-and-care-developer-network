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

package uk.nhs.hcdn.common.http;

import org.jetbrains.annotations.NotNull;

public enum ResponseCodeRange
{
	Informational1xx(CompilerWorkaround.MinimumResponseCode),
	Successful2xx(200),
	Redirectional3xx(300),
	ClientError4xx(400),
	ServerError5xx(500),
	;

	private static final class CompilerWorkaround
	{
		@ResponseCode private static final int MinimumResponseCode = 100;
		private static final int MaximumLength = 600;
		private static final ResponseCodeRange[] Index = new ResponseCodeRange[MaximumLength];
	}

	ResponseCodeRange(@ResponseCode final int fromInclusive)
	{
		for(int index = fromInclusive; index < fromInclusive + 100; index++)
		{
			CompilerWorkaround.Index[index] = this;
		}
	}

	@NotNull
	public static ResponseCodeRange responseCodeRange(@ResponseCode final int responseCode) throws ResponseCodeOutsideOfValidRangeInvalidException
	{
		return CompilerWorkaround.Index[validateResponseCode(responseCode)];
	}

	@ResponseCode
	public static int validateResponseCode(final int responseCode) throws ResponseCodeOutsideOfValidRangeInvalidException
	{
		if (responseCode < CompilerWorkaround.MinimumResponseCode || responseCode > CompilerWorkaround.MaximumLength)
		{
			throw new ResponseCodeOutsideOfValidRangeInvalidException(responseCode);
		}
		return responseCode;
	}
}
