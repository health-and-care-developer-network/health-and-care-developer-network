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

package uk.nhs.hcdn.dts.rats.response.schema;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.TextXmlConstructor;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.XmlConstructor;
import uk.nhs.hcdn.common.xml.XmlSchemaViolationException;
import uk.nhs.hcdn.dts.rats.response.ResponseStatus;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hcdn.dts.rats.response.ResponseStatus.responseStatus;

public final class ResponseStatusTextXmlConstructor extends TextXmlConstructor<ResponseStatus>
{
	@NotNull
	public static final XmlConstructor<?, ?> ResponseStatusTextXmlConstructorInstance = new ResponseStatusTextXmlConstructor();

	private ResponseStatusTextXmlConstructor()
	{
		super(null);
	}

	@NotNull
	@Override
	protected ResponseStatus convert(@NotNull final String text) throws XmlSchemaViolationException
	{
		@Nullable final ResponseStatus result = responseStatus(text);
		if (result == null)
		{
			throw new XmlSchemaViolationException(format(ENGLISH, "text %1$s is not a valid ResponseStatus", text));
		}
		return result;
	}

	@NotNull
	@Override
	public Class<ResponseStatus> type()
	{
		return ResponseStatus.class;
	}
}
