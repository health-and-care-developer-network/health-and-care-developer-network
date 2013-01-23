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

package uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors;

import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public abstract class TextXmlConstructor<V> implements XmlConstructor<TextValueCollector, V>
{
	protected TextXmlConstructor()
	{
	}

	@Override
	@NotNull
	public TextValueCollector start()
	{
		return new TextValueCollector();
	}

	@NotNull
	@Override
	public XmlConstructor<?, ?> node(@NotNull final String name) throws XmlSchemaViolationException
	{
		throw new XmlSchemaViolationException(format(ENGLISH, "text node should not have child %1$s", name));
	}

	@Override
	public void attribute(@NotNull final TextValueCollector collector, @NotNull final String key, @NotNull final String value) throws XmlSchemaViolationException
	{
		throw new XmlSchemaViolationException(format(ENGLISH, "attribute key %1$s, value %2$s was unexpected", key, value));
	}

	@Override
	public void text(@NotNull final TextValueCollector collector, @NotNull final String text) throws XmlSchemaViolationException
	{
		collector.text(text);
	}

	@Override
	public void node(@NotNull final TextValueCollector collector, @NotNull final String name, @NotNull final Object value) throws XmlSchemaViolationException
	{
		throw new XmlSchemaViolationException(format(ENGLISH, "node key %1$s, value %2$s was unexpected", name, value));
	}

	@NotNull
	@Override
	public V finish(@NotNull final TextValueCollector collector) throws XmlSchemaViolationException
	{
		return convert(collector.finish());
	}

	@NotNull
	protected abstract V convert(@NotNull final String text) throws XmlSchemaViolationException;
}
