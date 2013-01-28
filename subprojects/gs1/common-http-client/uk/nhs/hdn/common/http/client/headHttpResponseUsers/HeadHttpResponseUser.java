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

package uk.nhs.hdn.common.http.client.headHttpResponseUsers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.MillisecondsSince1970;
import uk.nhs.hdn.common.http.ResponseCode;
import uk.nhs.hdn.common.http.ResponseCodeRange;
import uk.nhs.hdn.common.http.client.exceptions.UnacceptableResponseException;

public interface HeadHttpResponseUser
{
	void response(@ResponseCode final int responseCode, @NotNull final ResponseCodeRange responseCodeRange, @MillisecondsSince1970 final long date, @MillisecondsSince1970 final long expires, final long contentLengthOrMinusOneIfNoneSupplied, @Nullable final String contentType, @Nullable final String contentEncoding) throws UnacceptableResponseException;
}
