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
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.rootValueHolders.XRootXmlConstructor;
import uk.nhs.hcdn.common.tuples.Pair;
import uk.nhs.hcdn.common.xml.XmlSchemaViolationException;

import java.util.Stack;

public final class GenericXmlParseEventHandler<V, F> implements XmlParseEventHandler
{
	@NotNull
	private final Stack<XmlNodeState<?, ?>> xmlNodeStates;

	@Nullable
	private XmlNodeState<?, ?> current;

	@NotNull
	private final ParseResultUser<F> parseResultUser;

	@SuppressWarnings("unchecked")
	public GenericXmlParseEventHandler(@NotNull final XRootXmlConstructor<V> rootXmlConstructor, @NotNull final ParseResultUser<F> parseResultUser)
	{
		this.parseResultUser = parseResultUser;
		xmlNodeStates = new Stack<>();
		current = new XmlNodeState(rootXmlConstructor, rootXmlConstructor.shouldPreserveWhitespace());
	}

	@Override
	public void startDocument()
	{
	}

	@SuppressWarnings("unchecked")
	@Override
	public void endDocument() throws XmlSchemaViolationException
	{
		final F result = (F) current().finish();
		parseResultUser.use(result);
	}

	@Override
	public void startElement(@NotNull final String name, @NotNull final Iterable<Pair<String, String>> attributes) throws XmlSchemaViolationException
	{
		xmlNodeStates.push(current);
		current = current().node(name, attributes);
	}

	@Override
	public void endElement(@NotNull final String name) throws XmlSchemaViolationException
	{
		final Object result = current().finish();
		current = xmlNodeStates.pop();
		current.node(name, result);
	}

	@Override
	public void text(@NotNull final String text) throws XmlSchemaViolationException
	{
		current().text(text);
	}

	@NotNull
	private XmlNodeState<? ,?> current()
	{
		assert current != null;
		return current;
	}
}
