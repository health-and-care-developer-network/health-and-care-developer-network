/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class PrimitiveIntArrayToStringGenerator extends AbstractToStringGenerator<int[]>
{
	@NotNull
	public static final ToStringGenerator<?> PrimitiveIntArrayToStringGeneratorInstance = new PrimitiveIntArrayToStringGenerator();

	private PrimitiveIntArrayToStringGenerator()
	{
		super(int[].class);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	@Override
	public String toString(@NotNull final int[] value)
	{
		return Arrays.toString(value);
	}
}
