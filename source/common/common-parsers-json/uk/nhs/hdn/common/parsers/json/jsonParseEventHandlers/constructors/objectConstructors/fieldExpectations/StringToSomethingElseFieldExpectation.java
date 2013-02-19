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
import uk.nhs.hdn.common.reflection.toString.delegates.Delegate;
import uk.nhs.hdn.common.reflection.toString.delegates.StaticMethodDelegate;
import uk.nhs.hdn.common.serialisers.FieldTokenName;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static uk.nhs.hdn.common.reflection.toString.delegates.ConstructorDelegate.constructorDelegate;

public final class StringToSomethingElseFieldExpectation<S> extends FieldExpectation<S>
{
	@NotNull
	public static <S> FieldExpectation<S> nullStringToSomethingElseField(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<S> constructorParameterType)
	{
		return nonNullStringToSomethingElseField(key, constructorParameterType, constructorDelegate(constructorParameterType, String.class), true);
	}

	@NotNull
	public static <S> FieldExpectation<S> nonNullStringToSomethingElseField(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<S> constructorParameterType)
	{
		return nonNullStringToSomethingElseField(key, constructorParameterType, constructorDelegate(constructorParameterType, String.class), false);
	}

	@NotNull
	public static <S> FieldExpectation<S> nonNullStringToSomethingElseField(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<S> constructorParameterType, @NotNull final Class<?> staticMethodClass, @NonNls @NotNull final String methodName)
	{
		return nonNullStringToSomethingElseField(key, constructorParameterType, StaticMethodDelegate.<S>staticMethodDelegate(staticMethodClass, methodName), false);
	}

	@NotNull
	public static <S> FieldExpectation<S> nonNullStringToSomethingElseField(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<S> constructorParameterType, @NotNull final Delegate<S> delegate, final boolean nullPermitted)
	{
		return new StringToSomethingElseFieldExpectation<>(key, constructorParameterType, delegate, nullPermitted);
	}

	@NotNull
	private final Delegate<S> delegate;

	public StringToSomethingElseFieldExpectation(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<S> constructorParameterType, @NotNull final Delegate<S> delegate, final boolean nullPermitted)
	{
		super(key, constructorParameterType, nullPermitted, null, null, null);
		this.delegate = delegate;
	}

	@SuppressWarnings("RefusedBequest")
	@Override
	public void putConstantStringValue(@NotNull final Object[] constructorArguments, @NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		@Nullable final S convertedValue;
		try
		{
			convertedValue = delegate.invoke(value);
		}
		catch (RuntimeException e)
		{
			throw new SchemaViolationInvalidJsonException(format(ENGLISH, "string for parameter %1$s can not be value '%2$s'", constructorParameterType, value), e);
		}
		if (convertedValue == null)
		{
			throw new SchemaViolationInvalidJsonException(format(ENGLISH, "string for parameter %1$s can not be value '%2$s' and null", constructorParameterType, value));
		}
		putValue(constructorArguments, convertedValue);
	}
}
