/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class PrimitiveLongArrayToStringGenerator extends AbstractToStringGenerator<long[]>
{
	@NotNull
	public static final ToStringGenerator<?> PrimitiveLongArrayToStringGeneratorInstance = new PrimitiveLongArrayToStringGenerator();

	private PrimitiveLongArrayToStringGenerator()
	{
		super(long[].class);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	@Override
	public String toString(@NotNull final long[] value)
	{
		return Arrays.toString(value);
	}
}
