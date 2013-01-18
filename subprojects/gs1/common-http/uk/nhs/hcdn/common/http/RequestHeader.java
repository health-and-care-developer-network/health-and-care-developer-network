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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({PARAMETER, FIELD, METHOD, LOCAL_VARIABLE})
public @interface RequestHeader
{
	@NotNull
	@RequestHeader
	String HostHeaderName = "Host";

	@NotNull
	@RequestHeader
	String RefererHeaderName = "Referer"; // note mis-spelling

	@NotNull
	@RequestHeader
	String UserAgentHeaderName = "User-Agent"; // note mis-spelling

	@NotNull
	@RequestHeader
	String DateHeaderName = "Date";

	@NotNull
	@RequestHeader
	String ExpectHeaderName = "Expect";

	@NotNull
	@RequestHeader
	String IfMatchHeaderName = "If-Match";
	@NotNull
	@RequestHeader
	String IfNoneMatchHeaderName = "If-None-Match";

	@NotNull
	@RequestHeader
	String IfModifiedSinceHeaderName = "If-Modified-Since";
	@NotNull
	@RequestHeader
	String IfUnmodifiedSinceHeaderName = "If-Unmodified-Since";

	@NotNull
	@RequestHeader
	String RangeHeaderName = "Range";
	@NotNull
	@RequestHeader
	String IfRangeHeaderName = "If-Range";





	@NotNull
	@RequestHeader
	String AcceptHeaderName = "Accept";

	@NotNull
	@RequestHeader
	String AcceptLanguageHeaderName = "Accept-Language";

	@NotNull
	@RequestHeader
	String AcceptEncodingHeaderName = "Accept-Encoding";

	@NotNull
	@RequestHeader
	String AcceptDatetimeHeaderName = "Accept-Datetime";

	@NotNull
	@RequestHeader
	String AuthorizationHeaderName = "Authorization";

	@NotNull
	@RequestHeader
	String ContentTypeHeaderName = "Content-Type";

	@NotNull
	@RequestHeader
	String ContentLengthHeaderName = "Content-Length";

	@NotNull
	@RequestHeader
	String TransferEncodingHeaderName = "TE";

	@NotNull
	@RequestHeader
	String ConnectionHeaderName = "Connection";
}
