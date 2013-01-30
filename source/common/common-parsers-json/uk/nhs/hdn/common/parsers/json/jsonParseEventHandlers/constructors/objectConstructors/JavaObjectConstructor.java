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

package uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations.FieldExpectation;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.ObjectKeyUnexpectedSchemaViolationInvalidJsonException;
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class JavaObjectConstructor<X> implements ObjectConstructor<Object[]>
{
	@NotNull
	public static <X> ObjectConstructor<?> object(@NotNull final Class<X> classX, @NotNull final FieldExpectation<?>... fieldExpectations)
	{
		return new JavaObjectConstructor<>(classX, fieldExpectations);
	}

	private final int length;

	@NotNull
	private final Map<String, FieldExpectation<?>> register;

	@NotNull
	private final Constructor<X> constructor;

	public JavaObjectConstructor(@NotNull final Class<X> classX, @NotNull final FieldExpectation<?>... fieldExpectations)
	{
		length = fieldExpectations.length;
		register = new HashMap<>(length);
		final Class<?>[] parameterTypes = new Class[length];
		for(int index = 0; index < length; index++)
		{
			final FieldExpectation<?> fieldExpectation = fieldExpectations[index];
			fieldExpectation.register(register, parameterTypes, index);
		}
		try
		{
			constructor = classX.getDeclaredConstructor(parameterTypes);
		}
		catch (NoSuchMethodException e)
		{
			throw new IllegalArgumentException("class does not have a public constructor that matches field expectations", e);
		}
	}

	@NotNull
	private FieldExpectation<?> fieldExpectation(final String key) throws SchemaViolationInvalidJsonException
	{
		@Nullable final FieldExpectation<?> fieldExpectation = register.get(key);
		if (fieldExpectation == null)
		{
			throw new ObjectKeyUnexpectedSchemaViolationInvalidJsonException(key);
		}
		return fieldExpectation;
	}

	@Override
	public void putObjectValue(@NotNull final Object[] objectCollector, @NotNull final String key, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(key).putObjectValue(objectCollector, value);
	}

	@Override
	public void putArrayValue(@NotNull final Object[] objectCollector, @NotNull final String key, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(key).putArrayValue(objectCollector, value);
	}

	@Override
	public void putLiteralBooleanValue(@NotNull final Object[] objectCollector, @NotNull final String key, final boolean value) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(key).putLiteralBooleanValue(objectCollector, value);
	}

	@Override
	public void putLiteralNullValue(@NotNull final Object[] objectCollector, @NotNull final String key) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(key).putLiteralNullValue(objectCollector);
	}

	@Override
	public void putConstantStringValue(@NotNull final Object[] objectCollector, @NotNull final String key, @NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(key).putConstantStringValue(objectCollector, value);
	}

	@Override
	public void putConstantNumberValue(@NotNull final Object[] objectCollector, @NotNull final String key, final long value) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(key).putConstantNumberValue(objectCollector, value);
	}

	@Override
	public void putConstantNumberValue(@NotNull final Object[] objectCollector, @NotNull final String key, @NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(key).putConstantNumberValue(objectCollector, value);
	}

	@NotNull
	@Override
	public Object[] newCollector()
	{
		return new Object[length];
	}

	@NotNull
	@Override
	public ArrayConstructor<?> arrayConstructor(@NotNull final String key) throws SchemaViolationInvalidJsonException
	{
		return fieldExpectation(key).getArrayConstructor();
	}

	@NotNull
	@Override
	public ObjectConstructor<?> objectConstructor(@NotNull final String key) throws SchemaViolationInvalidJsonException
	{
		return fieldExpectation(key).getObjectConstructor();
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public Object collect(@NotNull final Object[] collector) throws SchemaViolationInvalidJsonException
	{
		try
		{
			return constructor.newInstance(collector);
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new ShouldNeverHappenException(e);
		}
		catch (InvocationTargetException e)
		{
			throw new SchemaViolationInvalidJsonException("Could not out object", e);
		}
	}
}
