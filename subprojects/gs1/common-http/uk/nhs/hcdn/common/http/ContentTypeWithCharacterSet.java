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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static uk.nhs.hcdn.common.http.ContentType.*;

public @interface ContentTypeWithCharacterSet
{
	@NotNull
	String CharsetUtf8 = ";charset=utf-8";

	@NonNls
	@ContentTypeWithCharacterSet
	@NotNull
	String JsonContentTypeUtf8 = JsonContentType + CharsetUtf8; // should use withCharacterSet() but there is a bug in the javac compiler

	@NonNls
	@ContentTypeWithCharacterSet
	@NotNull
	String XmlContentTypeUtf8 = XmlContentType + CharsetUtf8;

	@NonNls
	@ContentTypeWithCharacterSet
	@NotNull
	String LegacyXmlContentTypeUtf8 = LegacyXmlContentType + CharsetUtf8;

	@NonNls
	@ContentTypeWithCharacterSet
	@NotNull
	String TextContentTypeUtf8 = TextContentType + CharsetUtf8;
}
