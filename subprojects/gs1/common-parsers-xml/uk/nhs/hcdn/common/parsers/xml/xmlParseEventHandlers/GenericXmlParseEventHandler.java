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
import uk.nhs.hcdn.common.parsers.parseResultUsers.ParseResultUser;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.XmlConstructor;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.XmlSchemaViolationException;

import java.util.Stack;

public final class GenericXmlParseEventHandler<V> implements XmlParseEventHandler
{
	@NotNull
	private final Stack<NodeState<?, ?>> nodeStates;

	@Nullable
	private NodeState<?, ?> current;

	@NotNull
	private final ParseResultUser<V> parseResultUser;

	@SuppressWarnings("unchecked")
	public GenericXmlParseEventHandler(@NotNull final XmlConstructor<?, V> rootXmlConstructor, @NotNull final ParseResultUser<V> parseResultUser)
	{
		this.parseResultUser = parseResultUser;
		nodeStates = new Stack<>();
		current = new NodeState(rootXmlConstructor);
	}

	@Override
	public void startDocument()
	{
	}

	@SuppressWarnings("unchecked")
	@Override
	public void endDocument() throws XmlSchemaViolationException
	{
		final Object result = current().finish();
		parseResultUser.use((V) result);
	}

	@Override
	public void startElement(@NotNull final String name) throws XmlSchemaViolationException
	{
		nodeStates.push(current);
		current = current().node(name);
	}

	@Override
	public void endElement(@NotNull final String name) throws XmlSchemaViolationException
	{
		final Object result = current().finish();
		current = nodeStates.pop();
		current.node(name, result);
	}

	@Override
	public void attribute(@NotNull final String key, @NotNull final String value) throws XmlSchemaViolationException
	{
		current().attribute(key, value);
	}

	@Override
	public void text(@NotNull final String text) throws XmlSchemaViolationException
	{
		current().text(text);
	}

	@NotNull
	private NodeState<? ,?> current()
	{
		assert current != null;
		return current;
	}
}
