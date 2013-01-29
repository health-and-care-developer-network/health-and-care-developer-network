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

package uk.nhs.hdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.tuples.Pair;
import uk.nhs.hdn.common.xml.XmlSchemaViolationException;

public final class MayBeMissingFieldXmlConstructor<C, V> implements MissingFieldXmlConstructor<C, V>
{
	@NotNull
	private final XmlConstructor<C, V> underlyingXmlConstructor;
	@NotNull
	private final V valueIfMissing;
	@NotNull
	private final NilXmlConstructor<?, ?> nilXmlConstructor;
	@NotNull
	private final Class<?> type;

	public MayBeMissingFieldXmlConstructor(@NotNull final XmlConstructor<C, V> underlyingXmlConstructor, @NotNull final V valueIfMissing)
	{
		this(underlyingXmlConstructor, valueIfMissing, underlyingXmlConstructor.type());
	}

	public MayBeMissingFieldXmlConstructor(@NotNull final XmlConstructor<C, V> underlyingXmlConstructor, @NotNull final V valueIfMissing, @NotNull final Class<?> type)
	{
		this.underlyingXmlConstructor = underlyingXmlConstructor;
		this.valueIfMissing = valueIfMissing;
		nilXmlConstructor = new NilXmlConstructor<>(type, valueIfMissing);
		this.type = type;
	}

	@Override
	public V missingFieldValue()
	{
		return valueIfMissing;
	}

	@NotNull
	@Override
	public XmlConstructor<?, ?> nilFieldConstructor()
	{
		return nilXmlConstructor;
	}

	@NotNull
	@Override
	public Class<?> type()
	{
		return type;
	}

	@NotNull
	@Override
	public C start()
	{
		return underlyingXmlConstructor.start();
	}

	@NotNull
	@Override
	public XmlConstructor<?, ?> childNode(@NotNull final String name, @NotNull final Iterable<Pair<String, String>> attributes, final boolean isNil, @Nullable final String type) throws XmlSchemaViolationException
	{
		if (isNil)
		{
			return nilXmlConstructor;
		}
		return underlyingXmlConstructor.childNode(name, attributes, isNil, type);
	}

	@Override
	public void collectText(@NotNull final C collector, @NotNull final String text, final boolean shouldPreserveWhitespace) throws XmlSchemaViolationException
	{
		underlyingXmlConstructor.collectText(collector, text, shouldPreserveWhitespace);
	}

	@Override
	public void collectNode(@NotNull final C collector, @NotNull final String name, @NotNull final Object value) throws XmlSchemaViolationException
	{
		underlyingXmlConstructor.collectNode(collector, name, value);
	}

	@NotNull
	@Override
	public V finish(@NotNull final C collector) throws XmlSchemaViolationException
	{
		return underlyingXmlConstructor.finish(collector);
	}

}
