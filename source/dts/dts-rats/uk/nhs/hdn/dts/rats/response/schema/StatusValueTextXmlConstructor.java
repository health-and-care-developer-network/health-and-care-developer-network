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

package uk.nhs.hdn.dts.rats.response.schema;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.TextXmlConstructor;
import uk.nhs.hdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.XmlConstructor;
import uk.nhs.hdn.common.xml.XmlSchemaViolationException;
import uk.nhs.hdn.dts.rats.response.StatusValue;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.dts.rats.response.StatusValue.statusValue;

public final class StatusValueTextXmlConstructor extends TextXmlConstructor<StatusValue>
{
	@NotNull
	public static final XmlConstructor<?, ?> StatusValueTextXmlConstructorInstance = new StatusValueTextXmlConstructor();

	private StatusValueTextXmlConstructor()
	{
		super(null, StatusValue.class);
	}

	@NotNull
	@Override
	protected StatusValue convert(@NotNull final String text) throws XmlSchemaViolationException
	{
		final int integer;
		try
		{
			integer = parseInt(text);
		}
		catch(NumberFormatException ignored)
		{
			throw new XmlSchemaViolationException(format(ENGLISH, "text %1$s is not a valid StatusValue because it is not an integer", text));
		}

		@Nullable final StatusValue result = statusValue(integer);
		if (result == null)
		{
			throw new XmlSchemaViolationException(format(ENGLISH, "text %1$s is not a valid StatusValue", text));
		}
		return result;
	}
}
