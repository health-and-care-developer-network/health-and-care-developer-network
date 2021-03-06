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
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hdn.common.serialisers.FieldTokenName;

public class NonNullFieldExpectation<X> extends FieldExpectation<X>
{
	@NotNull
	public static NonNullFieldExpectation<String> nonNullStringField(@FieldTokenName @NonNls @NotNull final String key)
	{
		return new NonNullFieldExpectation<>(key, String.class, null, null);
	}

	@NotNull
	public static NonNullFieldExpectation<Boolean> nonNullbooleanField(@FieldTokenName @NonNls @NotNull final String key)
	{
		return nonNullField(key, boolean.class);
	}

	@NotNull
	public static NonNullFieldExpectation<Long> nonNulllongField(@FieldTokenName @NonNls @NotNull final String key)
	{
		return nonNullField(key, long.class);
	}

	@NotNull
	public static <X> NonNullFieldExpectation<X> nonNullField(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<X> constructorParameterType)
	{
		return new NonNullFieldExpectation<>(key, constructorParameterType, null, null);
	}

	@NotNull
	public static <X> NonNullFieldExpectation<X> nonNullField(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<X> constructorParameterType, @NotNull final ArrayConstructor<?> arrayConstructor)
	{
		return new NonNullFieldExpectation<>(key, constructorParameterType, arrayConstructor, null);
	}

	@NotNull
	public static <X> NonNullFieldExpectation<X> nonNullField(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<X> constructorParameterType, @NotNull final ObjectConstructor<?> objectConstructor)
	{
		return new NonNullFieldExpectation<>(key, constructorParameterType, null, objectConstructor);
	}

	@NotNull
	public static <X> NonNullFieldExpectation<X> nonNullField(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<X> constructorParameterType, @NotNull final ArrayConstructor<?> arrayConstructor, @NotNull final ObjectConstructor<?> objectConstructor)
	{
		return new NonNullFieldExpectation<>(key, constructorParameterType, arrayConstructor, objectConstructor);
	}

	public NonNullFieldExpectation(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<X> constructorParameterType, @Nullable final ArrayConstructor<?> arrayConstructor, @Nullable final ObjectConstructor<?> objectConstructor)
	{
		this(key, constructorParameterType, arrayConstructor, objectConstructor, null);
	}

	public NonNullFieldExpectation(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<X> constructorParameterType, @Nullable final ArrayConstructor<?> arrayConstructor, @Nullable final ObjectConstructor<?> objectConstructor, @Nullable final X defaultValueIfMissing)
	{
		super(key, constructorParameterType, false, arrayConstructor, objectConstructor, defaultValueIfMissing);
	}
}
