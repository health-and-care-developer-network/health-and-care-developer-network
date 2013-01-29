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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.arrayCreators.ArrayCreator;
import uk.nhs.hdn.common.tuples.Pair;
import uk.nhs.hdn.common.xml.XmlSchemaViolationException;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.JavaObjectXmlConstructor.isOnlyWhitespace;

public final class ArrayXmlConstructor<A> implements XmlConstructor<List<A>, A[]>
{
	@NotNull
	public static <A> ArrayXmlConstructor<A> arrayOf(@NonNls @NotNull final String elementNodeName, @NotNull final ArrayCreator<A> arrayCreator, @NotNull final XmlConstructor<?, ?> elementXmlConstructor)
	{
		return new ArrayXmlConstructor<>(elementNodeName, arrayCreator, elementXmlConstructor);
	}

	private static final int Guess = 100;

	@NotNull
	@NonNls
	private final String elementNodeName;
	@NotNull
	private final ArrayCreator<A> arrayCreator;
	@NotNull
	private final XmlConstructor<?, ?> elementXmlConstructor;
	@NotNull
	private final Class<A> elementType;

	public ArrayXmlConstructor(@NonNls @NotNull final String elementNodeName, @NotNull final ArrayCreator<A> arrayCreator, @NotNull final XmlConstructor<?, ?> elementXmlConstructor)
	{
		this.elementNodeName = elementNodeName;
		this.arrayCreator = arrayCreator;
		this.elementXmlConstructor = elementXmlConstructor;
		elementType = arrayCreator.type();
	}

	@NotNull
	@Override
	public Class<?> type()
	{
		return arrayCreator.arrayType();
	}

	@NotNull
	@Override
	public List<A> start()
	{
		return new ArrayList<>(Guess);
	}

	@NotNull
	@Override
	public XmlConstructor<?, ?> childNode(@NotNull final String name, @NotNull final Iterable<Pair<String, String>> attributes, final boolean isNil, @Nullable final String type) throws XmlSchemaViolationException
	{
		if (!name.equals(elementNodeName))
		{
			throw new XmlSchemaViolationException(format(ENGLISH, "expected an array element called %1$s not %2$s", elementNodeName, name));
		}
		return elementXmlConstructor;
	}

	@Override
	public void collectText(@NotNull final List<A> collector, @NotNull final String text, final boolean shouldPreserveWhitespace) throws XmlSchemaViolationException
	{
		if (text.isEmpty())
		{
			return;
		}
		if (!shouldPreserveWhitespace && isOnlyWhitespace(text))
		{
			return;
		}
		throw new XmlSchemaViolationException(format(ENGLISH, "text value %1$s was unexpected", text));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void collectNode(@NotNull final List<A> collector, @NotNull final String name, @NotNull final Object value) throws XmlSchemaViolationException
	{
		if (!elementType.isAssignableFrom(value.getClass()))
		{
			throw new XmlSchemaViolationException("mismatch of type");
		}
		collector.add((A) value);
	}

	@NotNull
	@Override
	public A[] finish(@NotNull final List<A> collector)
	{
		return collector.toArray(arrayCreator.newInstance1(collector.size()));
	}
}
