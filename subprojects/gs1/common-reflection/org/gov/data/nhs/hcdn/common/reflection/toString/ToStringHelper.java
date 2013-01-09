package org.gov.data.nhs.hcdn.common.reflection.toString;

import org.gov.data.nhs.hcdn.common.exceptions.ShouldNeverHappenException;
import org.gov.data.nhs.hcdn.common.reflection.ClassInformation;
import org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.ToStringGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.gov.data.nhs.hcdn.common.reflection.ClassInformation.classInformation;
import static org.gov.data.nhs.hcdn.common.reflection.FieldInformation.fieldInformation;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.ByteToStringGenerator.ByteToStringGeneratorInstance;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.CharacterToStringGenerator.CharacterToStringGeneratorInstance;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.GregorianCalendarToStringGenerator.GregorianToStringGeneratorInstance;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.LockToStringGenerator.LockToStringGeneratorInstance;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.ObjectArrayToStringGenerator.ObjectArrayToStringGeneratorInstance;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.ObjectToStringGenerator.ObjectToStringGeneratorInstance;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveBooleanArrayToStringGenerator.PrimitiveBooleanArrayToStringGeneratorInstance;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveByteArrayToStringGenerator.PrimitiveByteArrayToStringGeneratorInstance;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveCharArrayToStringGenerator.PrimitiveCharArrayToStringGeneratorInstance;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveDoubleArrayToStringGenerator.PrimitiveDoubleArrayToStringGeneratorInstance;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveFloatArrayToStringGenerator.PrimitiveFloatArrayToStringGeneratorInstance;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveIntArrayToStringGenerator.PrimitiveIntArrayToStringGeneratorInstance;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveLongArrayToStringGenerator.PrimitiveLongArrayToStringGeneratorInstance;
import static org.gov.data.nhs.hcdn.common.reflection.toString.toStringGenerators.PrimitiveShortArrayToStringGenerator.PrimitiveShortArrayToStringGeneratorInstance;

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
		for (final Field field : classInformation.allPrivateFinalFields())
		{
			if (fieldInformation(field).doesNotHaveAnnotation())
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
