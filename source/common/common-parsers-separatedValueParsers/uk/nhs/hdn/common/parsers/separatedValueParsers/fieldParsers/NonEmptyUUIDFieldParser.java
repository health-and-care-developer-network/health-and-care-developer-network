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

import java.util.UUID;

public final class NonEmptyUUIDFieldParser extends AbstractMandatoryPrefixFieldParser<UUID>
{
	@NotNull public static final FieldParser<UUID> NonEmptyUUIDFieldParserInstance = new NonEmptyUUIDFieldParser();

	private NonEmptyUUIDFieldParser()
	{
	}

	@NotNull
	@Override
	public UUID parse(final int fieldIndex, @NotNull final String fieldValue) throws CouldNotParseFieldException
	{
		validateIsNotEmpty(fieldIndex, fieldValue);
		try
		{
			return UUID.fromString(fieldValue);
		}
		catch (IllegalArgumentException e)
		{
			throw new CouldNotParseFieldException(fieldIndex, fieldValue, e);
		}
	}
}
