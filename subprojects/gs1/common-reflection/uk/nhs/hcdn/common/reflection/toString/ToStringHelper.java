/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.reflection.toString;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.nhs.hcdn.common.exceptions.ShouldNeverHappenException;
import uk.nhs.hcdn.common.reflection.ClassInformation;
import uk.nhs.hcdn.common.reflection.toString.toStringGenerators.*;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static uk.nhs.hcdn.common.reflection.ClassInformation.classInformation;
import static uk.nhs.hcdn.common.reflection.FieldInformation.fieldInformation;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.ByteToStringGenerator.ByteToStringGeneratorInstance;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.CharacterToStringGenerator.CharacterToStringGeneratorInstance;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.GregorianCalendarToStringGenerator.GregorianToStringGeneratorInstance;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.LockToStringGenerator.LockToStringGeneratorInstance;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.ObjectArrayToStringGenerator.ObjectArrayToStringGeneratorInstance;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.ObjectToStringGenerator.ObjectToStringGeneratorInstance;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveBooleanArrayToStringGenerator.PrimitiveBooleanArrayToStringGeneratorInstance;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveByteArrayToStringGenerator.PrimitiveByteArrayToStringGeneratorInstance;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveCharArrayToStringGenerator.PrimitiveCharArrayToStringGeneratorInstance;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveDoubleArrayToStringGenerator.PrimitiveDoubleArrayToStringGeneratorInstance;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveFloatArrayToStringGenerator.PrimitiveFloatArrayToStringGeneratorInstance;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveIntArrayToStringGenerator.PrimitiveIntArrayToStringGeneratorInstance;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveLongArrayToStringGenerator.PrimitiveLongArrayToStringGeneratorInstance;
import static uk.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveShortArrayToStringGenerator.PrimitiveShortArrayToStringGeneratorInstance;

@SuppressWarnings("ClassNamePrefixedWithPackageName")
public final class ToStringHelper
{
	private static final Map<Class<?>, ToStringGenerator<?>> ToStringGeneratorsRegister = register
	(
		ByteToStringGeneratorInstance,
		CharacterToStringGeneratorInstance,
		GregorianToStringGeneratorInstance,
		LockToStringGeneratorInstance,
		PrimitiveBooleanArrayToStringGeneratorInstance,
		PrimitiveByteArrayToStringGeneratorInstance,
		PrimitiveShortArrayToStringGeneratorInstance,
		PrimitiveCharArrayToStringGeneratorInstance,
		PrimitiveIntArrayToStringGeneratorInstance,
		PrimitiveLongArrayToStringGeneratorInstance,
		PrimitiveFloatArrayToStringGeneratorInstance,
		PrimitiveDoubleArrayToStringGeneratorInstance
	);

	private static Map<Class<?>, ToStringGenerator<?>> register(@NotNull final ToStringGenerator<?>... toStringGenerators)
	{
		final Map<Class<?>, ToStringGenerator<?>> register = new HashMap<>(toStringGenerators.length * 2);
		for (final ToStringGenerator<?> toStringGenerator : toStringGenerators)
		{
			toStringGenerator.register(register);
		}
		return register;
	}
	private ToStringHelper()
	{}

	@SuppressWarnings("FeatureEnvy")
	@NotNull
	public static String toString(@NotNull final Object instanceToString)
	{
		@NotNull final Class<?> aClass = instanceToString.getClass();

		final StringWriter stringWriter = new StringWriter();

		final ClassInformation classInformation = classInformation(aClass);
		stringWriter.write(classInformation.simpleName());
		stringWriter.write("(");

		boolean afterFirstField = false;
		for (final Field field : classInformation.allPrivateOrProtectedFinalFields())
		{
			if (fieldInformation(field).hasAnnotation(ExcludeFromToString.class))
			{
				continue;
			}
			try
			{
				if (afterFirstField)
				{
					stringWriter.write(", ");
				}
				final Object value = field.get(instanceToString);
				writeValue(stringWriter, value);
			}
			catch (IllegalAccessException e)
			{
				throw new ShouldNeverHappenException(e);
			}
			afterFirstField = true;
		}

		stringWriter.write(")");
		return stringWriter.toString();
	}

	public static void writeValue(@NotNull final StringWriter stringWriter, @Nullable final Object value)
	{
		final String response;
		if (value == null)
		{
			response = "null";
		}
		else
		{
			final Class<?> aClass = value.getClass();
			@Nullable final ToStringGenerator<?> nullableToStringGenerator = ToStringGeneratorsRegister.get(aClass);
			@SuppressWarnings("rawtypes") @NotNull final ToStringGenerator notNullToStringGenerator;
			if (nullableToStringGenerator == null)
			{
				notNullToStringGenerator = aClass.isArray() ? ObjectArrayToStringGeneratorInstance : ObjectToStringGeneratorInstance;
			}
			else
			{
				notNullToStringGenerator = nullableToStringGenerator;
			}
			//noinspection unchecked
			response = notNullToStringGenerator.toString(value);
		}
		stringWriter.write(response);
	}

}
