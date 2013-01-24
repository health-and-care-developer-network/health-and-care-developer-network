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
import uk.nhs.hcdn.common.tuples.Pair;

public final class NilXmlConstructor<C, V> implements XmlConstructor<Object, V>
{
	@NotNull
	private static final Object IrrelevantCollector = new Object();

	@NotNull
	private final Class<V> type;
	@NotNull
	private final V valueIfMissing;

	public NilXmlConstructor(@NotNull final Class<V> type, @NotNull final V valueIfMissing)
	{
		this.type = type;
		this.valueIfMissing = valueIfMissing;
	}

	@NotNull
	@Override
	public Class<V> type()
	{
		return type;
	}

	@NotNull
	@Override
	public Object start()
	{
		return IrrelevantCollector;
	}

	@NotNull
	@Override
	public XmlConstructor<?, ?> childNode(@NotNull final String name, @NotNull final Iterable<Pair<String, String>> attributes, final boolean isNil) throws XmlSchemaViolationException
	{
		throw new XmlSchemaViolationException("node has xsi:nil set and so should not have a child node");
	}

	@Override
	public void collectText(@NotNull final Object collector, @NotNull final String text, final boolean shouldPreserveWhitespace) throws XmlSchemaViolationException
	{
		throw new XmlSchemaViolationException("node has xsi:nil set and so should not have a text node");
	}

	@Override
	public void collectNode(@NotNull final Object collector, @NotNull final String name, @NotNull final Object value)
	{
		throw new IllegalStateException("Should not have been called as node() will have blown up");
	}

	@NotNull
	@Override
	public V finish(@NotNull final Object collector)
	{
		return valueIfMissing;
	}
}
