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

package uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.XmlConstructor;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.XmlSchemaViolationException;
import uk.nhs.hcdn.common.tuples.Pair;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class XmlNodeState<C, V>
{
	@NonNls
	private static final String XmlSpaceAttribute = "http://www.w3.org/XML/1998/namespace:space";

	@NonNls
	private static final String XmlSchemaNilAttribute = "http://www.w3.org/2001/XMLSchema:nil";

	private final XmlConstructor<C, V> xmlConstructor;
	private final boolean shouldPreserveWhitespace;
	private final C collector;

	public XmlNodeState(@NotNull final XmlConstructor<C, V> xmlConstructor, final boolean shouldPreserveWhitespace)
	{
		this.xmlConstructor = xmlConstructor;
		this.shouldPreserveWhitespace = shouldPreserveWhitespace;
		collector = xmlConstructor.start();
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public XmlNodeState<?, ?> node(@NotNull final String name, @NotNull final Iterable<Pair<String, String>> attributes) throws XmlSchemaViolationException
	{
		boolean childShouldPreserveWhitespace = shouldPreserveWhitespace;
		boolean isNil = false;
		for (final Pair<String, String> attribute : attributes)
		{
			if (attribute.a.equals(XmlSpaceAttribute))
			{
				childShouldPreserveWhitespace = shouldPreserveWhitespace(attribute.b);
			}
			else if (attribute.a.equals(XmlSchemaNilAttribute))
			{
				isNil = isNil(attribute.b);
			}
		}
		return new XmlNodeState(xmlConstructor.childNode(name, attributes, isNil), childShouldPreserveWhitespace);
	}

	@SuppressWarnings({"HardCodedStringLiteral", "DuplicateStringLiteralInspection"})
	private boolean shouldPreserveWhitespace(final String attributeValue) throws XmlSchemaViolationException
	{
		switch (attributeValue)
		{
			case "preserve":
				return true;

			case "default":
				return shouldPreserveWhitespace;

			default:
				throw new XmlSchemaViolationException(format(ENGLISH, "Unknown value '%1$s' for xml:space", attributeValue));
		}
	}

	@SuppressWarnings({"HardCodedStringLiteral", "DuplicateStringLiteralInspection"})
	private static boolean isNil(final String attributeValue) throws XmlSchemaViolationException
	{
		switch(attributeValue)
		{
			case "true":
				return true;

			case "false":
				return false;

			default:
				throw new XmlSchemaViolationException(format(ENGLISH, "Unknown value '%1$s' for xsi:nil", attributeValue));
		}
	}

	public void text(@NotNull final String text) throws XmlSchemaViolationException
	{
		xmlConstructor.collectText(collector, text, shouldPreserveWhitespace);
	}

	public void node(@NotNull final String name, @NotNull final Object value) throws XmlSchemaViolationException
	{
		xmlConstructor.collectNode(collector, name, value);
	}

	@NotNull
	public V finish() throws XmlSchemaViolationException
	{
		return xmlConstructor.finish(collector);
	}
}
