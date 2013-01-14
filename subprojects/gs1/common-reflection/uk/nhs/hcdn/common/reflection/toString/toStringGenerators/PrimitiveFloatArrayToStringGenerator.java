/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class PrimitiveFloatArrayToStringGenerator extends AbstractToStringGenerator<float[]>
{
	@NotNull
	public static final ToStringGenerator<?> PrimitiveFloatArrayToStringGeneratorInstance = new PrimitiveFloatArrayToStringGenerator();

	private PrimitiveFloatArrayToStringGenerator()
	{
		super(float[].class);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	@Override
	public String toString(@NotNull final float[] value)
	{
		return Arrays.toString(value);
	}
}
