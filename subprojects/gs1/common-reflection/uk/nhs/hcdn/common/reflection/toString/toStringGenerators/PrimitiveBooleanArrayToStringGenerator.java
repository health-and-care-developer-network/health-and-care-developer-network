/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class PrimitiveBooleanArrayToStringGenerator extends AbstractToStringGenerator<boolean[]>
{
	@NotNull
	public static final ToStringGenerator<?> PrimitiveBooleanArrayToStringGeneratorInstance = new PrimitiveBooleanArrayToStringGenerator();

	private PrimitiveBooleanArrayToStringGenerator()
	{
		super(boolean[].class);
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	@Override
	public String toString(@NotNull final boolean[] value)
	{
		return Arrays.toString(value);
	}
}
