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
import uk.nhs.hdn.common.reflection.toString.delegates.ConstructorDelegate;
import uk.nhs.hdn.common.reflection.toString.delegates.Delegate;
import uk.nhs.hdn.common.reflection.toString.delegates.StaticMethodDelegate;
import uk.nhs.hdn.common.serialisers.FieldTokenName;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;

public final class NonNullOriginallyStringFieldExpectation<S> extends FieldExpectation<S>
{
	@NotNull
	public static <S> FieldExpectation<S> nonNullStringField(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<S> constructorParameterType)
	{
		final Constructor<S> constructor;
		try
		{
			constructor = constructorParameterType.getConstructor(String.class);
		}
		catch (NoSuchMethodException e)
		{
			throw new IllegalStateException(e);
		}
		return nonNullStringField(key, constructorParameterType, new ConstructorDelegate<>(constructor));
	}

	@NotNull
	public static <S> FieldExpectation<S> nonNullStringFieldFromValueOf(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<S> constructorParameterType)
	{
		final Method method;
		try
		{
			method = constructorParameterType.getDeclaredMethod("valueOf", String.class);
		}
		catch (NoSuchMethodException e)
		{
			throw new IllegalStateException(e);
		}
		return nonNullStringField(key, constructorParameterType, new StaticMethodDelegate<S>(method));
	}

	@NotNull
	public static <S> FieldExpectation<S> nonNullStringField(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<S> constructorParameterType, @NotNull final Delegate<S> delegate)
	{
		return new NonNullOriginallyStringFieldExpectation<>(key, constructorParameterType, delegate);
	}

	@NotNull
	private final Delegate<S> delegate;

	public NonNullOriginallyStringFieldExpectation(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<S> constructorParameterType, @NotNull final Delegate<S> delegate)
	{
		super(key, constructorParameterType, false, null, null, null);
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
