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

package uk.nhs.hcdn.common.parsers.xml;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;
import uk.nhs.hcdn.common.parsers.parseResultUsers.NonNullValueReturningParseResultUser;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.GenericXmlParseEventHandler;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.*;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.rootValueHolders.XRootXmlConstructor;
import uk.nhs.hcdn.common.tuples.Pair;

import java.io.IOException;
import java.io.InputStream;

public class SchemaParser<V>
{
	@NotNull
	private final XRootXmlConstructor<V> rootXmlConstructor;

	public SchemaParser(@NotNull final XRootXmlConstructor<V> rootXmlConstructor)
	{
		this.rootXmlConstructor = rootXmlConstructor;
	}

	@SuppressWarnings("unchecked")
	public static <C, V, X, Y extends V> Pair<String, MissingFieldXmlConstructor<?, ?>> nodeMayBeMissing(@NonNls final String value, @NotNull final X valueIfMissing, final XmlConstructor<C, Y> xmlConstructor)
	{
		return new Pair<String, MissingFieldXmlConstructor<?, ?>>(value, new MayBeMissingFieldXmlConstructor(xmlConstructor, valueIfMissing));
	}

	@SuppressWarnings("unchecked")
	public static <C, V, X, Y> Pair<String, MissingFieldXmlConstructor<?, ?>> nodeMayBeMissing(@NonNls final String value, @NotNull final Class<? super V> type, @NotNull final X valueIfMissing, final XmlConstructor<C, Y> xmlConstructor)
	{
		return new Pair<String, MissingFieldXmlConstructor<?, ?>>(value, new MayBeMissingFieldXmlConstructor(xmlConstructor, valueIfMissing, type));
	}

	public static <C, V> Pair<String, MissingFieldXmlConstructor<?, ?>> node(@NonNls @NotNull final String value, @NotNull final XmlConstructor<C, V> xmlConstructor)
	{
		return new Pair<String, MissingFieldXmlConstructor<?, ?>>(value, new RequiredMissingFieldXmlConstructor<>(xmlConstructor));
	}

	@NotNull
	public final V parse(@NotNull final InputStream inputStream) throws IOException, SAXException
	{
		final NonNullValueReturningParseResultUser<V> parseResultUser = new NonNullValueReturningParseResultUser<>();
		final ConvenientSaxParser convenientSaxParser = new ConvenientSaxParser(new GenericXmlParseEventHandler<>(rootXmlConstructor, parseResultUser));
		convenientSaxParser.parse(inputStream);

		return parseResultUser.value();
	}
}
