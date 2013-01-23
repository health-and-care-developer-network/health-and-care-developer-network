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
import uk.nhs.hcdn.common.parsers.xml.xmlEventHandlers.SimplifiedXmlEventHandler;
import uk.nhs.hcdn.common.parsers.xml.xmlParseEventHandlers.XmlParseEventHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

import static uk.nhs.hcdn.common.parsers.xml.SaxParserFactoryHelper.newNamespaceAwareNonValidatingSaxParserFactory;
import static uk.nhs.hcdn.common.parsers.xml.SaxParserHelper.newSaxParser;

public final class ConvenientSaxParser
{
	@NotNull
	private static final SAXParserFactory AppropriateSaxParserFactory = newNamespaceAwareNonValidatingSaxParserFactory();

	public ConvenientSaxParser(@NotNull final XmlParseEventHandler xmlParseEventHandler)
	{
	}

	public void parser(@NotNull final InputStream inputStream)
	{
		final SAXParser saxParser = newSaxParser(AppropriateSaxParserFactory);
		saxParser.parse(inputStream, new SimplifiedXmlEventHandler());
	}
}
