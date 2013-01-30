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

public abstract class AbstractMandatoryPrefixFieldParser<V> implements FieldParser<V>
{
	@Override
	public final boolean skipIfEmpty()
	{
		return false;
	}

	protected static void validateIsNotEmpty(final int fieldIndex, @NotNull final String fieldValue) throws CouldNotParseFieldException
	{
		if (fieldValue.isEmpty())
		{
			throw new CouldNotParseFieldException(fieldIndex, fieldValue, "empty field values are not permitted");
		}
	}
}
