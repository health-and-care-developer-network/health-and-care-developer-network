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

package uk.nhs.hdn.common.http;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({PARAMETER, FIELD, METHOD, LOCAL_VARIABLE})
public @interface ResponseHeader
{
	@NotNull
	@ResponseHeader
	String ServerHeaderName = "Server";

	@NotNull
	@ResponseHeader
	String AllowHeaderName = "Allow";

	@NotNull
	@ResponseHeader
	String DateHeaderName = "Date";

	@NotNull
	@ResponseHeader
	String ContentTypeHeaderName = "Content-Type";

	@NotNull
	@ResponseHeader
	String ContentEncodingHeaderName = "Content-Encoding";

	@NotNull
	@ResponseHeader
	String ContentLanguageHeaderName = "Content-Language";

	@NotNull
	@ResponseHeader
	String ConnectionHeaderName = "Connection";

	@NotNull
	@ResponseHeader
	String StrictTransportSecurityHeaderName = "Strict-Transport-Security";

	@NotNull
	@ResponseHeader
	String XFrameOptionsHeaderName = "X-Frame-Options";

	@NotNull
	@ResponseHeader
	String XXssProtectionHeaderName = "X-XSS-Protection";

	@NotNull
	@ResponseHeader
	String XRimAutoMatchHeaderName = "X-RIM-Auto-Match";

	@NotNull
	@ResponseHeader
	String XRobotsTagHeaderName = "X-Robots-Tag";

	@NotNull
	@ResponseHeader
	String AccessControlAllowOriginHeaderName = "Access-Control-Allow-Origin";

	@NotNull
	@ResponseHeader
	String LastModifiedHeaderName = "Last-Modified";

	@NotNull
	@ResponseHeader
	String CacheControlHeaderName = "Cache-Control";

	@NotNull
	@ResponseHeader
	String ExpiresHeaderName = "Expires";

	@NotNull
	@ResponseHeader
	String AcceptRangesHeaderName = "Accept-Ranges";
}
