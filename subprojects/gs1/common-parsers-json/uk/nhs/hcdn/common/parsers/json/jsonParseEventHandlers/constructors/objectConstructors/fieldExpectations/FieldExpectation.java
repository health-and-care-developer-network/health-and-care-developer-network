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

package uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.fieldExpectations;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.arrayConstructors.ArrayConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.constructors.objectConstructors.ObjectConstructor;
import uk.nhs.hcdn.common.parsers.json.jsonParseEventHandlers.schemaViolationInvalidJsonExceptions.*;
import uk.nhs.hcdn.common.reflection.toString.AbstractToString;
import uk.nhs.hcdn.common.serialisers.FieldTokenName;

import java.math.BigDecimal;
import java.util.Map;

public class FieldExpectation<X> extends AbstractToString
{
	@NotNull
	@FieldTokenName
	@NonNls
	private final String key;
	@NotNull
	private final Class<X> constructorParameterType;
	private final boolean nullDisallowed;
	private final boolean booleanDisallowed;
	private final boolean integerDisallowed;
	@Nullable
	private final ArrayConstructor<?> arrayConstructor;
	@Nullable
	private final ObjectConstructor<?> objectConstructor;
	private int constructorParameterIndex;

	public FieldExpectation(@FieldTokenName @NonNls @NotNull final String key, @NotNull final Class<X> constructorParameterType, final boolean nullPermitted, @Nullable final ArrayConstructor<?> arrayConstructor, @Nullable final ObjectConstructor<?> objectConstructor)
	{
		if (key.isEmpty())
		{
			throw new IllegalArgumentException("key can not be empty");
		}
		if (arrayConstructor != null && objectConstructor != null)
		{
			throw new IllegalArgumentException("arrayConstructor and objectConstructor can not both be specified");
		}
		this.key = key;
		this.constructorParameterType = constructorParameterType;
		nullDisallowed = !nullPermitted;
		booleanDisallowed = isDisallowed(constructorParameterType, boolean.class);
		integerDisallowed = isDisallowed(constructorParameterType, long.class);
		this.arrayConstructor = arrayConstructor;
		this.objectConstructor = objectConstructor;
		constructorParameterIndex = -1;
	}

	private static boolean isDisallowed(final Class<?> constructorParameterType, final Class<?> obj)
	{
		return !constructorParameterType.equals(obj);
	}

	public final void register(@NotNull final Map<String, FieldExpectation<?>> register, @NotNull final Class<?>[] parameterTypes, final int constructorParameterIndex)
	{
		if (register.put(key, this) != null)
		{
			throw new IllegalStateException("Already registered");
		}
		this.constructorParameterIndex = constructorParameterIndex;
		parameterTypes[constructorParameterIndex] = constructorParameterType;
	}

	@NotNull
	public ObjectConstructor<?> getObjectConstructor() throws SchemaViolationInvalidJsonException
	{
		if (objectConstructor == null)
		{
			throw new NullObjectValueNotAllowedForSchemaViolationInvalidJsonException();
		}
		return objectConstructor;
	}

	@NotNull
	public ArrayConstructor<?> getArrayConstructor() throws SchemaViolationInvalidJsonException
	{
		if (arrayConstructor == null)
		{
			throw new NullArrayValueNotAllowedForSchemaViolationInvalidJsonException();
		}
		return arrayConstructor;
	}

	public void putObjectValue(@NotNull final Object[] constructorArguments, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		if (value == null && nullDisallowed)
		{
			throw new NullObjectValueNotAllowedForSchemaViolationInvalidJsonException();
		}
		try
		{
			constructorArguments[constructorParameterIndex] = constructorParameterType.cast(value);
		}
		catch(ClassCastException e)
		{
			throw new ObjectValueTypeMismatchForSchemaViolationInvalidJsonException(e);
		}
	}

	public void putArrayValue(@NotNull final Object[] constructorArguments, @Nullable final Object value) throws SchemaViolationInvalidJsonException
	{
		if (value == null && nullDisallowed)
		{
			throw new NullArrayValueNotAllowedForSchemaViolationInvalidJsonException();
		}
		try
		{
			constructorArguments[constructorParameterIndex] = constructorParameterType.cast(value);
		}
		catch (ClassCastException e)
		{
			throw new ArrayValueTypeMismatchForSchemaViolationInvalidJsonException(e);
		}
	}

	public void putLiteralBooleanValue(@NotNull final Object[] constructorArguments, final boolean value) throws SchemaViolationInvalidJsonException
	{
		if (booleanDisallowed)
		{
			throw new UnexpectedLiteralBooleanValueForSchemaViolationInvalidJsonException();
		}
		constructorArguments[constructorParameterIndex] = value;
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	public void putLiteralNullValue(@NotNull final Object[] constructorArguments) throws SchemaViolationInvalidJsonException
	{
		if (nullDisallowed)
		{
			throw new UnexpectedLiteralNullValueForSchemaViolationInvalidJsonException();
		}
	}

	public void putConstantStringValue(@NotNull final Object[] constructorArguments, @NotNull final String value) throws SchemaViolationInvalidJsonException
	{
		try
		{
			constructorArguments[constructorParameterIndex] = constructorParameterType.cast(value);
		}
		catch (ClassCastException e)
		{
			throw new StringValueTypeMismatchForSchemaViolationInvalidJsonException(e);
		}
	}

	public void putConstantNumberValue(@NotNull final Object[] constructorArguments, final long value) throws SchemaViolationInvalidJsonException
	{
		if (integerDisallowed)
		{
			throw new UnexpectedConstantNumberValueForSchemaViolationInvalidJsonException();
		}
		constructorArguments[constructorParameterIndex] = value;
	}

	public void putConstantNumberValue(@NotNull final Object[] constructorArguments, @NotNull final BigDecimal value) throws SchemaViolationInvalidJsonException
	{
		try
		{
			constructorArguments[constructorParameterIndex] = constructorParameterType.cast(value);
		}
		catch (ClassCastException e)
		{
			throw new NumberValueTypeMismatchForSchemaViolationInvalidJsonException(e);
		}
	}
}
