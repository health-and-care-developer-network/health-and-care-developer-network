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

import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;
import uk.nhs.hcdn.common.parsers.parseResultUsers.NonNullValueReturningParseResultUser;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.GenericXmlParseEventHandler;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.xmlConstructors.AbstractRootXmlConstructor;

import java.io.IOException;
import java.io.InputStream;

public class SchemaParser<V, F>
{
	@NotNull
	private final AbstractRootXmlConstructor<V, F> rootXmlConstructor;

	public SchemaParser(@NotNull final AbstractRootXmlConstructor<V, F> rootXmlConstructor)
	{
		this.rootXmlConstructor = rootXmlConstructor;
	}

	@NotNull
	public final F parse(@NotNull final InputStream inputStream) throws IOException, SAXException
	{
		final NonNullValueReturningParseResultUser<F> parseResultUser = new NonNullValueReturningParseResultUser<>();
		final ConvenientSaxParser convenientSaxParser = new ConvenientSaxParser(new GenericXmlParseEventHandler<>(rootXmlConstructor, parseResultUser));
		convenientSaxParser.parse(inputStream);

		return parseResultUser.value();
	}
}
