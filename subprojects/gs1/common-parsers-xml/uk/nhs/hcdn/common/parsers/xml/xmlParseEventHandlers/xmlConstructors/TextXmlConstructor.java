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
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.tuples.Pair;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public abstract class TextXmlConstructor<V> extends AbstractToString implements XmlConstructor<TextValueCollector, V>
{
	@Nullable
	private final V defaultIfTextNodeMissing;
	private final boolean defaultUnacceptable;

	protected TextXmlConstructor(@Nullable final V defaultIfTextNodeMissing)
	{
		this.defaultIfTextNodeMissing = defaultIfTextNodeMissing;
		defaultUnacceptable = defaultIfTextNodeMissing == null;
	}

	@Override
	@NotNull
	public final TextValueCollector start()
	{
		return new TextValueCollector();
	}

	@NotNull
	@Override
	public final XmlConstructor<?, ?> childNode(@NotNull final String name, @NotNull final Iterable<Pair<String, String>> attributes, final boolean isNil) throws XmlSchemaViolationException
	{
		throw new XmlSchemaViolationException(format(ENGLISH, "text node should not have child %1$s", name));
	}

	@Override
	public final void collectText(@NotNull final TextValueCollector collector, @NotNull final String text, final boolean shouldPreserveWhitespace) throws XmlSchemaViolationException
	{
		collector.text(text);
	}

	@Override
	public final void collectNode(@NotNull final TextValueCollector collector, @NotNull final String name, @NotNull final Object value) throws XmlSchemaViolationException
	{
		throw new XmlSchemaViolationException(format(ENGLISH, "node key %1$s, value %2$s was unexpected", name, value));
	}

	@NotNull
	@Override
	public final V finish(@NotNull final TextValueCollector collector) throws XmlSchemaViolationException
	{
		@Nullable final String finish = collector.finish();
		if (finish == null)
		{
			if (defaultUnacceptable)
			{
				throw new XmlSchemaViolationException("text node missing");
			}
			assert defaultIfTextNodeMissing != null;
			return defaultIfTextNodeMissing;
		}
		return convert(finish);
	}

	@NotNull
	protected abstract V convert(@NotNull final String text) throws XmlSchemaViolationException;
}
