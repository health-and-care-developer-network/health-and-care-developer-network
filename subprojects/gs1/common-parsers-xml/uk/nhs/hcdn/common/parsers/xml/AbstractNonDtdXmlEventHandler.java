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
import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import uk.nhs.hcdn.common.reflection.toString.ToStringHelper;

public abstract class AbstractNonDtdXmlEventHandler extends DefaultHandler2
{
	@SuppressWarnings("RefusedBequest")
	@Override
	public void startDTD(@NotNull final String name, @Nullable final String publicId, @Nullable final String systemId) throws SAXException
	{
		throw new SAXException("DOCTYPE declarations are unsupported");
	}

	// Overridden in case we are used with a SAX 1 parser
	@SuppressWarnings("RefusedBequest")
	@Override
	public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException
	{
		throw new SAXException("DOCTYPE notations are unsupported");
	}

	// Overridden in case we are used with a SAX 1 parser
	@SuppressWarnings("RefusedBequest")
	@Override
	public final void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) throws SAXException
	{
		throw new SAXException("DOCTYPE entity declarations are unsupported");
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public final void skippedEntity(final String name) throws SAXException
	{
		throw new SAXException("Entity declarations should not be skipped");
	}

	@Override
	@NotNull
	public final String toString()
	{
		return ToStringHelper.toString(this);
	}
}
