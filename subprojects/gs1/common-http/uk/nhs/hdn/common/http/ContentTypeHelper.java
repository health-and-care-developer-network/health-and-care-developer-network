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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class ContentTypeHelper
{
	@NotNull @ContentTypeWithCharacterSet @NonNls
	public static String withCharacterSet(@NotNull @NonNls final String contentType, @NotNull final Charset charset)
	{
		return format(ENGLISH, "%s;charset=%s", toLowerCase(contentType), toLowerCase(charset.name()));
	}

	private static String toLowerCase(@NonNls @NotNull final String contentType)
	{
		return contentType.toLowerCase(ENGLISH);
	}

	private ContentTypeHelper()
	{
	}
}
