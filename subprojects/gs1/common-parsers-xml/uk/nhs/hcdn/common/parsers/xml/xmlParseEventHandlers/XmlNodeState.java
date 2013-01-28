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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.XmlConstructor;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.xml.XmlSchemaViolationException;
import uk.nhs.hcdn.common.tuples.Pair;

import static uk.nhs.hcdn.common.xml.XmlAttributesHelper.*;

public final class XmlNodeState<C, V> extends AbstractToString
{
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
		@Nullable String type = null;
		for (final Pair<String, String> attribute : attributes)
		{
			if (attribute.a.equals(XmlSpaceAttribute))
			{
				childShouldPreserveWhitespace = shouldPreserveWhitespace(attribute.b, shouldPreserveWhitespace);
			}
			else if (attribute.a.equals(XmlSchemaNilAttribute))
			{
				isNil = isNil(attribute.b);
			}
			else if (attribute.a.equals(XmlSchemaTypeAttribute))
			{
				type = attribute.b;
			}
		}
		return new XmlNodeState(xmlConstructor.childNode(name, attributes, isNil, type), childShouldPreserveWhitespace);
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
