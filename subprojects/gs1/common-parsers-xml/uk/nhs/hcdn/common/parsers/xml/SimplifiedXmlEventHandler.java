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

package uk.nhs.hcdn.common.parsers.xml;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.XmlParseEventHandler;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlCollectors.XmlSchemaViolationException;

public final class SimplifiedXmlEventHandler extends AbstractNonDtdXmlEventHandler
{
	private boolean parsingRootElement;

	@NotNull
	private final XmlParseEventHandler xmlParseEventHandler;

	@SuppressWarnings("StringBufferField")
	@Nullable
	private StringBuilder currentText;

	public SimplifiedXmlEventHandler(@NotNull final XmlParseEventHandler xmlParseEventHandler)
	{
		this.xmlParseEventHandler = xmlParseEventHandler;
		currentText = null;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public void startDocument()
	{
		xmlParseEventHandler.startDocument();
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public void endDocument() throws SAXException
	{
		try
		{
			xmlParseEventHandler.endDocument();
		}
		catch (XmlSchemaViolationException e)
		{
			throw new SAXException(e);
		}
	}

	@SuppressWarnings({"RefusedBequest", "FeatureEnvy"})
	@Override
	public void startElement(@NotNull final String uri, @NotNull final String localName, @NotNull final String qName, @NotNull final Attributes attributes) throws SAXException
	{
		try
		{
			if (currentText != null)
			{
				final String text = currentText.toString();
				xmlParseEventHandler.text(text);
				currentText = null;
			}
			xmlParseEventHandler.startElement(key(uri, localName));
			final int length = attributes.getLength();
			for(int index = 0; index < length; index++)
			{
				final String attributeUri = attributes.getURI(index);
				final String attributeLocalName = attributes.getLocalName(index);
				xmlParseEventHandler.attribute(key(attributeUri, attributeLocalName), attributes.getValue(index));
			}
		}
		catch (XmlSchemaViolationException e)
		{
			throw new SAXException(e);
		}
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public void endElement(@NotNull final String uri, @NotNull final String localName, @NotNull final String qName) throws SAXException
	{
		try
		{
			if (currentText != null)
			{
				final String text = currentText.toString();
				xmlParseEventHandler.text(text);
				currentText = null;
			}
			xmlParseEventHandler.endElement(key(uri, localName));
		}
		catch (XmlSchemaViolationException e)
		{
			throw new SAXException(e);
		}
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public void characters(@SuppressWarnings("StandardVariableNames") @NotNull final char[] ch, final int start, final int length)
	{
		if (currentText == null)
		{
			currentText = new StringBuilder(length);
		}
		currentText.append(ch, start, length);
	}

	private static String key(@NonNls final String uri, @NonNls final String localName)
	{
		return uri + localName;
	}
}
