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

package uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;

import static uk.nhs.hdn.common.parsers.separatedValueParsers.fieldParsers.NonEmptyStringFieldParser.NonEmptyStringFieldParserInstance;

public final class NonEmptyURIFieldParser implements FieldParser<URI>
{
	@NotNull public static final FieldParser<URI> NonEmptyURIFieldParserInstance = new NonEmptyURIFieldParser();

	private NonEmptyURIFieldParser()
	{
	}

	@Override
	public boolean skipIfEmpty()
	{
		return NonEmptyStringFieldParserInstance.skipIfEmpty();
	}

	@NotNull
	@Override
	public URI parse(final int fieldIndex, @NotNull final String fieldValue) throws CouldNotParseFieldException
	{
		final String parsed = NonEmptyStringFieldParserInstance.parse(fieldIndex, fieldValue);
		try
		{
			return new URI(parsed);
		}
		catch (URISyntaxException e)
		{
			throw new CouldNotParseFieldException(fieldIndex, fieldValue, e);
		}
	}
}
