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

package uk.nhs.hdn.common.parsers.xml.xmlEventHandlers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import uk.nhs.hdn.common.parsers.xml.xmlParseEventHandlers.XmlParseEventHandler;
import uk.nhs.hdn.common.xml.XmlNamespaceUri;
import uk.nhs.hdn.common.xml.XmlSchemaViolationException;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.common.xml.XmlNamespaceUriHelper.namespaceQualifiedNodeOrAttributeName;

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
			throw new SAXException("endDocument XML schema violated", e);
		}
	}

	@SuppressWarnings({"RefusedBequest", "FeatureEnvy"})
	@Override
	public void startElement(@XmlNamespaceUri @NonNls @NotNull final String uri, @NotNull final String localName, @NotNull final String qName, @NotNull final Attributes attributes) throws SAXException
	{
		final String fullName = namespaceQualifiedNodeOrAttributeName(uri, localName);
		final IterableAttributes iterableAttributes = new IterableAttributes(attributes);

		if (currentText != null)
			{
				final String text = currentText.toString();
				try
				{
					xmlParseEventHandler.text(text);
				}
				catch (XmlSchemaViolationException e)
				{
					throw new SAXException(format(ENGLISH, "startElement %1$s(%2$s) preceeded by invalid text %3$s", fullName, iterableAttributes, text), e);
				}
				currentText = null;
			}
		try
		{
			xmlParseEventHandler.startElement(fullName, iterableAttributes);
		}
		catch (XmlSchemaViolationException e)
		{
			throw new SAXException(format(ENGLISH, "startElement %1$s(%2$s) XML schema violated", fullName, iterableAttributes), e);
		}
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public void endElement(@XmlNamespaceUri @NonNls @NotNull final String uri, @NotNull final String localName, @NotNull final String qName) throws SAXException
	{
		final String fullName = namespaceQualifiedNodeOrAttributeName(uri, localName);
		if (currentText != null)
		{
			final String text = currentText.toString();
			try
			{
				xmlParseEventHandler.text(text);
			}
			catch (XmlSchemaViolationException e)
			{
				throw new SAXException(format(ENGLISH, "endElement %1$s preceeded by invalid text %2$s", fullName, text), e);
			}
			currentText = null;
		}
		try
		{
			xmlParseEventHandler.endElement(fullName);
		}
		catch (XmlSchemaViolationException e)
		{
			throw new SAXException(format(ENGLISH, "endElement %1$s XML schema violated", fullName), e);
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

}
