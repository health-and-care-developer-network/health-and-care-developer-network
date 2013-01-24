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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.RootValueHolder;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.tuples.Pair;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class RootXmlConstructor<V> extends AbstractToString implements XmlConstructor<RootValueHolder<V>, V>
{
	@SafeVarargs
	@NotNull
	public static <V, W extends V> RootXmlConstructor<V> rootSchemaFor(@NotNull @NonNls final String rootNodeName, final boolean shouldPreserveWhitespace, @NotNull final Class<V> returnsType, @NotNull final Class<W> implementsType, @NotNull final Pair<String, MissingFieldXmlConstructor<?, ?>>... xmlConstructorsForFields)
	{
		return root(rootNodeName, shouldPreserveWhitespace, new JavaObjectXmlConstructor<>(returnsType, implementsType, xmlConstructorsForFields));
	}

	@NotNull
	public static <V> RootXmlConstructor<V> root(@NotNull @NonNls final String rootNodeName, final boolean shouldPreserveWhitespace, @NotNull final XmlConstructor<?, V> xmlConstructor)
	{
		return new RootXmlConstructor<>(rootNodeName, shouldPreserveWhitespace, xmlConstructor);
	}

	@NonNls
	@NotNull
	private final String rootNodeName;
	private final boolean shouldPreserveWhitespace;
	@NotNull
	private final XmlConstructor<?, V> xmlConstructor;

	public RootXmlConstructor(@NotNull @NonNls final String rootNodeName, final boolean shouldPreserveWhitespace, @NotNull final XmlConstructor<?, V> xmlConstructor)
	{
		this.rootNodeName = rootNodeName;
		this.shouldPreserveWhitespace = shouldPreserveWhitespace;
		this.xmlConstructor = xmlConstructor;
	}

	public boolean shouldPreserveWhitespace()
	{
		return shouldPreserveWhitespace;
	}

	@NotNull
	@Override
	public Class<V> type()
	{
		return xmlConstructor.type();
	}

	@Override
	public void collectText(@NotNull final RootValueHolder<V> collector, @NotNull final String text, final boolean shouldPreserveWhitespace)
	{
		throw new IllegalStateException("text should not occur before or after a root node");
	}

	@NotNull
	@Override
	public RootValueHolder<V> start()
	{
		return new RootValueHolder<>();
	}

	@NotNull
	@Override
	public XmlConstructor<?, ?> childNode(@NotNull final String name, @NotNull final Iterable<Pair<String, String>> attributes, final boolean isNil) throws XmlSchemaViolationException
	{
		if (!name.equals(rootNodeName))
		{
			throw new XmlSchemaViolationException(format(ENGLISH, "expected root node %1$s not %2$s", rootNodeName, name));
		}
		if (isNil)
		{
			throw new XmlSchemaViolationException(format(ENGLISH, "Root node %1$s should not be nil", rootNodeName));
		}
		return xmlConstructor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void collectNode(@NotNull final RootValueHolder<V> collector, @NotNull final String name, @NotNull final Object value)
	{
		collector.assign((V) value);
	}

	@NotNull
	@Override
	public V finish(@NotNull final RootValueHolder<V> collector) throws XmlSchemaViolationException
	{
		return collector.retrieve();
	}
}
