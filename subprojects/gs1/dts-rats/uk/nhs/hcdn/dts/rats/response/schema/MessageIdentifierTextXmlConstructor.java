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
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.TextXmlConstructor;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.XmlConstructor;
import uk.nhs.hcdn.common.xml.XmlSchemaViolationException;
import uk.nhs.hcdn.dts.rats.response.MessageIdentifier;

import static java.lang.String.format;

public final class MessageIdentifierTextXmlConstructor extends TextXmlConstructor<MessageIdentifier>
{
	@NotNull
	public static final XmlConstructor<?, ?> MessageIdentifierTextXmlConstructorInstance = new MessageIdentifierTextXmlConstructor();

	private MessageIdentifierTextXmlConstructor()
	{
		super(null);
	}

	@NotNull
	@Override
	protected MessageIdentifier convert(@NotNull final String text) throws XmlSchemaViolationException
	{
		try
		{
			return new MessageIdentifier(text);
		}
		catch (RuntimeException e)
		{
			throw new XmlSchemaViolationException(e);
		}
	}

	@NotNull
	@Override
	public Class<MessageIdentifier> type()
	{
		return MessageIdentifier.class;
	}
}
