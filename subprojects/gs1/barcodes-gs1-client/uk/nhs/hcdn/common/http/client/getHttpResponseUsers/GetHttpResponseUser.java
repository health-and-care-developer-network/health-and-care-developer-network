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

package uk.nhs.hcdn.common.http.client.getHttpResponseUsers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.MillisecondsSince1970;
import uk.nhs.hcdn.common.http.ResponseCode;
import uk.nhs.hcdn.common.http.client.UnacceptableResponseException;

import java.io.InputStream;

public interface GetHttpResponseUser<X>
{
	@NotNull
	X response(@ResponseCode final int responseCode, @NotNull String responseMessage, @MillisecondsSince1970 final long date, @MillisecondsSince1970 final long expires, final long contentLengthOrMinusOneIfNoneSupplied, @Nullable final String contentType, @Nullable final String contentEncoding, @NotNull final InputStream inputStream) throws UnacceptableResponseException;
}
