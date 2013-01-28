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

package uk.nhs.hdn.common.parsers.xml.xmlEventHandlers;

import org.jetbrains.annotations.NotNull;
import org.xml.sax.Attributes;
import uk.nhs.hdn.common.iterators.AbstractReadOnlyIterator;
import uk.nhs.hdn.common.tuples.Pair;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static uk.nhs.hdn.common.tuples.Pair.pair;
import static uk.nhs.hdn.common.xml.XmlNamespaceUriHelper.namespaceQualifiedNodeOrAttributeName;

public final class IterableAttributes extends AbstractReadOnlyIterator<Pair<String, String>> implements Iterable<Pair<String, String>>
{
	private static final int Colon = (int) ':';
	private static final int Equals = (int) '=';
	@NotNull
	private final Attributes attributes;
	private final int length;
	private int index;

	public IterableAttributes(@NotNull final Attributes attributes)
	{
		this.attributes = attributes;
		length = attributes.getLength();
		index = 0;
	}

	@Override
	public String toString()
	{
		final StringWriter writer = new StringWriter(100);
		writer.write(getClass().getSimpleName());
		writer.write("(");
		for(int localIndex = 0; localIndex < length; localIndex++)
		{
			if (localIndex != 0)
			{
				writer.write(", ");
			}
			final String uri = attributes.getURI(localIndex);
			if (!uri.isEmpty())
			{
				writer.write(uri);
				writer.write(Colon);
			}
			writer.write(attributes.getLocalName(localIndex));
			writer.write(Equals);
			writer.write(attributes.getValue(localIndex));
		}
		writer.write(")");
		return writer.toString();
	}

	@Override
	public Iterator<Pair<String, String>> iterator()
	{
		index = 0;
		return this;
	}

	@Override
	public boolean hasNext()
	{
		return index != length;
	}

	@Override
	public Pair<String, String> next()
	{
		if (index == length)
		{
			throw new NoSuchElementException();
		}
		final String attributeUri = attributes.getURI(index);
		final String attributeLocalName = attributes.getLocalName(index);
		try
		{
			return pair(namespaceQualifiedNodeOrAttributeName(attributeUri, attributeLocalName), attributes.getValue(index));
		}
		finally
		{
			index++;
		}
	}
}
