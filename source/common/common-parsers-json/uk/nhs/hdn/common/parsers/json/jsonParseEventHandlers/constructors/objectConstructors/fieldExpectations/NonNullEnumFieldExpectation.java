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

package uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;
import uk.nhs.hdn.common.serialisers.FieldTokenName;

import static java.lang.Enum.valueOf;
import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class NonNullEnumFieldExpectation<E extends Enum<E>> extends FieldExpectation<E>
{
	@NotNull
	public static <E extends Enum<E>> FieldExpectation<E> nonNullEnumField(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<E> constructorParameterType)
	{
		return new NonNullEnumFieldExpectation<>(key, constructorParameterType);
	}

	public NonNullEnumFieldExpectation(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<E> constructorParameterType)
	{
		super(key, constructorParameterType, false, null, null, null);
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public void putConstantStringValue(@NotNull final Object[] constructorArguments, @NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		@Nullable final E enumValue = valueOf(constructorParameterType, value);
		if (enumValue == null)
		{
			throw new SchemaViolationInvalidJsonException(format(ENGLISH, "enum for parameter %1$s can not be value '%2$s'", constructorParameterType, value));
		}
		putValue(constructorArguments, enumValue);
	}
}
