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
import uk.nhs.hdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.SchemaViolationInvalidJsonException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static uk.nhs.hdn.common.VariableArgumentsHelper.copyOf;

public final class JavaObjectFromArrayConstructor<X> implements ArrayConstructor<Object[]>
{
	@SuppressWarnings("MethodNamesDifferingOnlyByCase")
	@NotNull
	public static <X> ArrayConstructor<?> objectFromArray(@NotNull final Class<X> classX, @NotNull final FieldExpectation<?>... fieldExpectations)
	{
		return new JavaObjectFromArrayConstructor<>(classX, fieldExpectations);
	}

	private final int length;

	@NotNull
	private final FieldExpectation<?>[] fieldExpectations;

	@NotNull
	private final Constructor<X> constructor;

	public JavaObjectFromArrayConstructor(@NotNull final Class<X> classX, @NotNull final FieldExpectation<?>... fieldExpectations)
	{
		length = fieldExpectations.length;
		this.fieldExpectations = copyOf(fieldExpectations);

		final Map<String, FieldExpectation<?>> register = new HashMap<>(length);
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
			throw new IllegalArgumentException("class does not have a public constructor that matches field expectations for array", e);
		}
	}

	@NotNull
	private FieldExpectation<?> fieldExpectation(final int index) throws SchemaViolationInvalidJsonException
	{
		if (index >= length)
		{
			throw new SchemaViolationInvalidJsonException("index out of range (does not relate to an object constructor's field)");
		}
		return fieldExpectations[index];
	}

	@Override
	public void addLiteralBooleanValue(@NotNull final Object[] arrayCollector, final int index, final boolean value) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(index).putLiteralBooleanValue(arrayCollector, value);
	}

	@Override
	public void addLiteralNullValue(@NotNull final Object[] arrayCollector, final int index) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(index).putLiteralNullValue(arrayCollector);
	}

	@Override
	public void addConstantStringValue(@NotNull final Object[] arrayCollector, final int index, @NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(index).putConstantStringValue(arrayCollector, value);
	}

	@Override
	public void addConstantNumberValue(@NotNull final Object[] arrayCollector, final int index, final long value) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(index).putConstantNumberValue(arrayCollector, value);
	}

	@Override
	public void addConstantNumberValue(@NotNull final Object[] arrayCollector, final int index, @NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(index).putConstantNumberValue(arrayCollector, value);
	}

	@Override
	public void addObjectValue(@NotNull final Object[] arrayCollector, final int index, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(index).putObjectValue(arrayCollector, value);
	}

	@Override
	public void addArrayValue(@NotNull final Object[] arrayCollector, final int index, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		fieldExpectation(index).putArrayValue(arrayCollector, value);
	}

	@NotNull
	@Override
	public ArrayConstructor<?> arrayConstructor(final int index) throws SchemaViolationInvalidJsonException
	{
		return fieldExpectation(index).getArrayConstructor();
	}

	@NotNull
	@Override
	public ObjectConstructor<?> objectConstructor(final int index) throws SchemaViolationInvalidJsonException
	{
		return fieldExpectation(index).getObjectConstructor();
	}

	@NotNull
	@Override
	public Object[] newCollector()
	{
		return new Object[length];
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@Override
	public Object collect(@NotNull final Object[] collector) throws SchemaViolationInvalidJsonException
	{
		for (int index = 0; index < length; index++)
		{
			fieldExpectations[index].assignDefaultValueIfMissing(collector);
		}
		try
		{
			return constructor.newInstance(collector);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException e)
		{
			throw new ShouldNeverHappenException(e);
		}
		catch (InvocationTargetException e)
		{
			throw new SchemaViolationInvalidJsonException("Could not collect object from array", e);
		}
	}
}
