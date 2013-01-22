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
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlCollectors.XmlConstructor;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlCollectors.XmlSchemaViolationException;

public final class NodeState<C, V>
{
	private final XmlConstructor<C, V> xmlConstructor;
	private final C collector;

	public NodeState(@NotNull final XmlConstructor<C, V> xmlConstructor)
	{
		this.xmlConstructor = xmlConstructor;
		collector = xmlConstructor.start();
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public NodeState<?, ?> node(@NotNull final String name) throws XmlSchemaViolationException
	{
		return new NodeState(xmlConstructor.node(name));
	}

	public void attribute(@NotNull final String key, @NotNull final String value) throws XmlSchemaViolationException
	{
		xmlConstructor.attribute(collector, key, value);
	}

	public void text(@NotNull final String text) throws XmlSchemaViolationException
	{
		xmlConstructor.text(collector, text);
	}

	public void node(@NotNull final String name, @NotNull final Object value) throws XmlSchemaViolationException
	{
		xmlConstructor.node(collector, name, value);
	}

	@NotNull
	public V finish() throws XmlSchemaViolationException
	{
		return xmlConstructor.finish(collector);
	}
}
