/*
 * © Crown copyright 2013
 * Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package uk.nhs.hcdn.common.reflection.toString.toStringGenerators;

import org.jetbrains.annotations.NotNull;

import static java.util.Arrays.deepToString;

public final class ObjectArrayToStringGenerator extends AbstractToStringGenerator<Object[]>
{
	@NotNull
	public static final ToStringGenerator<?> ObjectArrayToStringGeneratorInstance = new ObjectArrayToStringGenerator();

	private ObjectArrayToStringGenerator()
	{
	}

	@SuppressWarnings("MethodCanBeVariableArityMethod")
	@NotNull
	@Override
	public String toString(@NotNull final Object[] value)
	{
		return deepToString(value);
	}
}
